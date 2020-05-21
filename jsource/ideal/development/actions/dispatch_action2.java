/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.elements.dictionary.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.declarations.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;

public class dispatch_action2 extends base_procedure implements action {

  private final procedure_declaration the_procedure;
  private final type from_type;
  private final dictionary<type, procedure_declaration> vtable;
  private final action from;

  public dispatch_action2(procedure_declaration the_procedure) {
    super(the_procedure.short_name(), the_procedure.get_procedure_type());
    this.the_procedure = the_procedure;
    this.from_type = the_procedure.declared_in_type().get_flavored(the_procedure.get_flavor());
    vtable = new list_dictionary<type, procedure_declaration>();
    vtable.put(from_type, the_procedure);
    from = null;
  }

  private dispatch_action2(dispatch_action2 primary_dispatch, action from, origin the_origin) {
    super(primary_dispatch.the_procedure.short_name(),
        primary_dispatch.the_procedure.get_procedure_type());
    this.the_procedure = primary_dispatch.the_procedure;
    this.from_type = primary_dispatch.from_type;
    vtable = primary_dispatch.vtable;
    assert from != null;
    this.from = from;
  }

  @Override
  public origin deeper_origin() {
    return the_procedure;
  }

  @Override
  public final action to_action(origin the_origin) {
    return this;
  }

  public declaration get_declaration() {
    return the_procedure;
  }

  public boolean handles_type(type the_type) {
    return vtable.contains_key(the_type);
  }

  public void add_handler(type the_type, procedure_declaration new_procedure) {
    if (vtable.contains_key(the_type)) {
      utilities.panic("Duplicate handler in " + this + " for " + the_type);
    }
    assert !vtable.contains_key(the_type);
    vtable.put(the_type, new_procedure);
  }

  public @Nullable action get_from() {
    return from;
  }

  @Override
  public abstract_value result() {
    if (from != null) {
      // TODO: implement full resolution logic
      @Nullable procedure_declaration resolved_procedure = vtable.get(from.result().type_bound());
      if (resolved_procedure != null) {
        return resolved_procedure.get_procedure_type();
      }
    }

    return the_procedure.get_procedure_type();
  }

  @Override
  public boolean has_this_argument() {
    return false;
  }

  @Override
  public dispatch_action2 bind_from(action new_from, origin the_origin) {
    // TODO: may be narrow result_type here.
    if (from != null) {
      new_from = from.bind_from(new_from, the_origin);
    }

    return new dispatch_action2(this, new_from, the_origin);
  }

  @Override
  public entity_wrapper execute(execution_context the_context) {
    if (from == null) {
      utilities.panic("Unbound 'this' in " + the_procedure);
    }

    return this;
  }

  @Override
  public entity_wrapper execute(readonly_list<entity_wrapper> arguments,
      execution_context the_execution_context) {
    entity_wrapper this_entity = from.execute(the_execution_context);
    if (this_entity instanceof jump_wrapper) {
      return this_entity;
    }

    type this_type = action_utilities.to_type(this_entity.type_bound());

    @Nullable procedure_declaration resolved_procedure = vtable.get(this_type);
    if (resolved_procedure == null) {
      immutable_list<entry<type, procedure_declaration>> procedures = vtable.elements();
      @Nullable entry<type, procedure_declaration> best = null;
      for (int i = 0; i < procedures.size(); ++i) {
        entry<type, procedure_declaration> candidate = procedures.get(i);
        if (!this_type.is_subtype_of(candidate.key())) {
          continue;
        }
        if (best == null) {
          best = candidate;
        } else {
          if (candidate.key().is_subtype_of(best.key())) {
            best = candidate;
          } else if (best.key().is_subtype_of(candidate.key())) {
            // best stays.
          } else {
            utilities.panic("Can't decide between candidates...");
          }
        }
      }
      if (best == null) {
        // TODO: should never happen.
        utilities.panic("Can't resolve for " + this_type + ", expected " + from_type);
      }
      // TODO: update vtable.
      resolved_procedure = best.value();
    }

    return action_utilities.execute_procedure(resolved_procedure, (value_wrapper) this_entity,
        arguments, the_execution_context);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_procedure);
  }
}
