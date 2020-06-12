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

public class dispatch_action extends base_action implements action {

  private final action primary_action;
  private final dictionary<type, action> vtable;
  private final action from;

  public dispatch_action(action primary_action, type from_type) {
    super(primary_action);
    this.primary_action = primary_action;
    vtable = new list_dictionary<type, action>();
    vtable.put(from_type, primary_action);
    from = null;
  }

  private dispatch_action(dispatch_action the_dispatch, action from, origin the_origin) {
    super(the_origin);
    this.primary_action = the_dispatch.primary_action;
    vtable = the_dispatch.vtable;
    assert from != null;
    this.from = from;
  }

  public declaration get_declaration() {
    return primary_action.get_declaration();
  }

  public boolean handles_type(type the_type) {
    return vtable.contains_key(the_type);
  }

  public void add_handler(type the_type, action new_action) {
    if (vtable.contains_key(the_type)) {
      utilities.panic("Duplicate handler in " + this + " for " + the_type);
    }
    assert !vtable.contains_key(the_type);
    vtable.put(the_type, new_action);
  }

  public @Nullable action get_from() {
    return from;
  }

  public action get_primary() {
    return primary_action;
  }

  @Override
  public type result() {
    if (from != null) {
      // TODO: implement full resolution logic
      @Nullable action resolved_action = vtable.get(from.result().type_bound());
      if (resolved_action != null) {
        return resolved_action.result().type_bound();
      }
    }

    return primary_action.result().type_bound();
  }

  @Override
  public dispatch_action bind_from(action new_from, origin the_origin) {
    // TODO: may be narrow result_type here.
    if (from != null) {
      new_from = from.bind_from(new_from, the_origin);
    }

    return new dispatch_action(this, new_from, the_origin);
  }

  @Override
  public entity_wrapper execute(execution_context the_execution_context) {
    if (from == null) {
      utilities.panic("Unbound 'this' in " + primary_action);
    }

    entity_wrapper this_entity = from.execute(the_execution_context);
    if (this_entity instanceof jump_wrapper) {
      return this_entity;
    }

    type this_type = action_utilities.to_type(this_entity.type_bound());

    @Nullable action resolved_action = vtable.get(this_type);
    if (resolved_action == null) {
      immutable_list<entry<type, action>> actions = vtable.elements();
      @Nullable entry<type, action> best = null;
      for (int i = 0; i < actions.size(); ++i) {
        entry<type, action> candidate = actions.get(i);
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
        utilities.panic("Can't resolve " + primary_action + " for " + this_type);
      }
      resolved_action = best.value();
      vtable.put(this_type, resolved_action);
    }

    return resolved_action.bind_from(new entity_action(this_entity, this), this).
        execute(the_execution_context);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, primary_action);
  }
}
