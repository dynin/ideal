/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
import ideal.development.origins.*;
import ideal.development.comments.*;

public abstract class base_analyzer<C extends origin> extends debuggable implements analyzable {

  protected static action_name INSIDE_NAME =
      new special_name(new base_string("inside"), new base_string("base_analyzer"));

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

    if (source instanceof base_construct) {
      base_construct the_base_construct = (base_construct) source;
      assert the_base_construct.the_analyzable == null;
      the_base_construct.the_analyzable = this;
    } else if (source instanceof analyzable ||
               source instanceof source_content ||
               source == analyzer_utilities.UNINITIALIZED_POSITION) {
      return;
    } else {
      utilities.panic("Unrecognized source: " + source);
    }
  }

  protected base_analyzer(C source) {
    this(source, null, null);
  }

  @Override
  public origin deeper_origin() {
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

  public string parent_name() {
    if (parent == null) {
      return new base_string("<parent>");
    }

    if (parent.short_name() == INSIDE_NAME) {
      return parent.get_parent().short_name().to_string();
    } else {
      return parent.short_name().to_string();
    }
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

  protected language_settings settings() {
    return context.settings();
  }

  protected void init_context(analyzable the_analyzable) {
    assert the_analyzable != null;
    if (the_analyzable instanceof base_analyzer) {
      base_analyzer the_base_analyzer = (base_analyzer) the_analyzable;
      the_base_analyzer.set_context(inner_type(), context);
    }
  }

  protected void special_init_context(analyzable the_analyzable, analysis_context new_context) {
    if (the_analyzable instanceof base_analyzer) {
      base_analyzer the_base_analyzer = (base_analyzer) the_analyzable;
      assert the_base_analyzer.context == null && the_base_analyzer.parent == null;
      the_base_analyzer.parent = inner_type();
      the_base_analyzer.context = new_context;
    }
  }

  protected boolean has_analysis_errors(analyzable the_analyzable) {
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

  protected void handle_error(error_signal the_error_signal) {
    the_error_signal.report_not_cascading();
  }

  protected void add_type_dependence(@Nullable principal_type subtype, principal_type supertype) {
    if (subtype == null) {
      return;
    }

    graph<principal_type, origin> the_type_graph = get_context().type_graph();

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
    origin source = this;
    master_type result = new master_type(type_kinds.block_kind, flavor_profiles.nameonly_profile,
        name, block_parent, get_context(), the_declaration);
    result.process_declaration(declaration_pass.last());
    add_promotion(result, parent());
    return result;
  }
}
