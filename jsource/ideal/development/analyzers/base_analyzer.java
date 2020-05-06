/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import javax.annotation.Nullable;
import ideal.machine.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.declarations.*;
import ideal.development.comments.*;

public abstract class base_analyzer<C extends position> extends debuggable implements analyzable {

  protected static action_name INSIDE_NAME = new special_name("inside", base_analyzer.class);

  @dont_display
  public final C source;
  @dont_display
  private principal_type parent;
  @dont_display
  private analysis_context context;

  protected base_analyzer(C source, @Nullable principal_type parent,
      @Nullable analysis_context context) {
    this.parent = parent;
    this.context = context;
    this.source = source;
  }

  protected base_analyzer(C source) {
    this(source, null, null);
  }

  @Override
  public final position source_position() {
    return source;
  }

  public static analyzable make(construct new_construct) {
    return new dispatcher().process(new_construct);
  }

  public static readonly_list<analyzable> make_list(readonly_list<construct> constructs) {
    list<analyzable> actions = new base_list<analyzable>();
    for (int i = 0; i < constructs.size(); ++i) {
      actions.append(make(constructs.get(i)));
    }
    return actions;
  }

  public void set_context(principal_type parent, analysis_context context) {
    assert parent != null;
    assert context != null;

    if (this.parent == null && this.context == null) {
      this.parent = parent;
      this.context = context;
    } else {
      assert parent == this.parent;
      assert context == this.context;
    }
  }

  public analysis_context get_context() {
    if (context == null) {
      utilities.panic("Context not set in " + this);
    }
    return context;
  }

  public principal_type parent() {
    if (parent == null) {
      utilities.panic("Parent not set in " + this);
    }
    return parent;
  }

  protected void do_add_dependence(@Nullable principal_type the_principal, declaration_pass pass) {
  }

  protected void add_dependence(analyzable the_analyzable, @Nullable principal_type the_principal,
      declaration_pass pass) {
    init_context(the_analyzable);
    if (the_analyzable instanceof base_analyzer) {
      base_analyzer the_base_analyzer = (base_analyzer) the_analyzable;
      the_base_analyzer.do_add_dependence(the_principal, pass);
    }
  }

  @Override
  public analyzable specialize(specialization_context context, principal_type new_parent) {
    utilities.panic(to_string() + ".specialize() unimplemented");
    return null;
  }

  protected void add_promotion(type from, type to) {
    action_utilities.add_promotion(get_context(), from, to, this);
  }

  public principal_type inner_type() {
    return parent();
  }

  protected semantics language() {
    return context.language();
  }

  protected common_library library() {
    return language().library();
  }

  protected void init_context(analyzable the_analyzable) {
    assert the_analyzable != null;
    if (the_analyzable instanceof base_analyzer) {
      base_analyzer the_base_analyzer = (base_analyzer) the_analyzable;
      the_base_analyzer.set_context(inner_type(), context);
    }
    update_map_in_context(the_analyzable);
  }

  private void update_map_in_context(analyzable the_analyzable) {
    if (the_analyzable.source_position() instanceof construct) {
      construct source = (construct) the_analyzable.source_position();
      @Nullable analyzable before = context.get_analyzable(source);
      if (before == null) {
        context.put_analyzable(source, the_analyzable);
      } else if (before != the_analyzable) {
        // TODO: eliminate this workaround.
        if (false && ! (source instanceof extension_construct)) {
          utilities.panic("Dup for " + source + ": " + before + " & " + the_analyzable);
        }
      }
    }
  }

  protected void special_init_context(analyzable the_analyzable, analysis_context new_context) {
    if (the_analyzable instanceof base_analyzer) {
      base_analyzer the_base_analyzer = (base_analyzer) the_analyzable;
      assert the_base_analyzer.context == null && the_base_analyzer.parent == null;
      the_base_analyzer.parent = inner_type();
      the_base_analyzer.context = new_context;
    }
    update_map_in_context(the_analyzable);
  }

  protected void associate_with_this(construct source) {
    @Nullable analyzable before = context.get_analyzable(source);
    if (before == null) {
      context.put_analyzable(source, this);
    } else if (before != this) {
      utilities.panic("Dup for " + source + ": " + before + " & " + this);
    }
  }

  protected boolean has_errors(analyzable the_analyzable) {
    init_context(the_analyzable);
    return the_analyzable.analyze() instanceof error_signal;
  }

  protected @Nullable error_signal find_error(analyzable the_analyzable) {
    init_context(the_analyzable);
    analysis_result the_analysis_result = the_analyzable.analyze();
    if (the_analysis_result instanceof error_signal) {
      return (error_signal) the_analysis_result;
    } else {
      return null;
    }
  }

  protected @Nullable error_signal find_error(analyzable the_analyzable,
      action_target the_action_target) {

    if (the_analyzable instanceof resolve_analyzer) {
      init_context(the_analyzable);
      analysis_result the_result = ((resolve_analyzer) the_analyzable).resolve(the_action_target);
      if (the_result instanceof error_signal) {
        return (error_signal) the_result;
      } else {
        return null;
      }
    } else {
      return find_error(the_analyzable);
    }
  }

  protected action action_not_error(analyzable the_analyzable) {
    init_context(the_analyzable);
    analysis_result result = the_analyzable.analyze();
    assert !(result instanceof error_signal);
    if (result instanceof action_plus_constraints) {
      return ((action_plus_constraints) result).the_action;
    } else {
      assert result instanceof action;
      return (action) result;
    }
  }

  protected void maybe_report_error(error_signal error) {
    if (!error.is_cascading) {
      error.cause.report();
      if (false) {
        log.debug("Error: " + this + " @ " + parent);
        Thread.dumpStack();
      }
    }
  }

  // TODO: redundant with maybe_report_error()
  protected void handle_error(error_signal signal) {
    maybe_report_error(signal);
  }

  protected void add_type_dependence(@Nullable principal_type subtype, principal_type supertype) {
    if (subtype == null) {
      return;
    }

    graph<principal_type, position> the_type_graph = get_context().type_graph();

    if (the_type_graph.introduces_cycle(subtype, supertype)) {
      handle_error(new error_signal(messages.type_cycle, this));
    } else {
      the_type_graph.add_edge(subtype, supertype, this);
    }
  }

  protected void add_error(type from, action_name name, error_signal the_signal) {
    get_context().add(from, name, new error_action(the_signal));
  }

  protected principal_type make_inside_type(principal_type in_type, declaration the_declaration) {
    principal_type inside_result = make_block(INSIDE_NAME, in_type, the_declaration);
    add_promotion(inside_result, in_type);
    return inside_result;
  }

  protected master_type make_block(action_name name, declaration the_declaration) {
    return make_block(name, parent(), the_declaration);
  }

  protected master_type make_block(action_name name, principal_type block_parent,
      declaration the_declaration) {
    position source = this;
    master_type result = new master_type(type_kinds.block_kind, flavor_profiles.nameonly_profile,
        name, block_parent, get_context(), the_declaration);
    result.process_declaration(declaration_pass.last());
    add_promotion(result, parent());
    return result;
  }
}
