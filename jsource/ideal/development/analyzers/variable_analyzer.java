/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.values.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import static ideal.development.flavors.flavor.*;
import ideal.development.values.*;
import ideal.development.declarations.*;
import ideal.machine.annotations.dont_display;

public class variable_analyzer extends declaration_analyzer
    implements variable_declaration {

  private final @Nullable readonly_list<annotation_construct> variable_annotations;
  private final @Nullable analyzable variable_type;
  private final action_name name;
  private final @Nullable readonly_list<annotation_construct> post_annotations;
  private final @Nullable analyzable init;
  private @Nullable variable_category category;
  private @Nullable type var_value_type;
  private @Nullable type var_reference_type;
  private variable_action the_variable_action;
  private boolean declared_as_reference;
  private @Nullable action init_action;
  @dont_display private @Nullable readonly_list<declaration> overriden;

  public variable_analyzer(variable_construct source) {
    super(source);
    variable_annotations = source.annotations;
    if (source.variable_type != null) {
      variable_type = make(source.variable_type);
    } else {
      variable_type = null;
    }
    name = source.name;
    post_annotations = source.post_annotations;
    if (source.init != null) {
      init = make(source.init);
    } else {
      init = null;
    }
  }

  public variable_analyzer(annotation_set annotations, @Nullable analyzable variable_type,
      action_name name, @Nullable analyzable init, origin the_origin) {
    super(the_origin);
    set_annotations(annotations);
    variable_annotations = null;
    this.variable_type = variable_type;
    this.name = name;
    post_annotations = null;
    this.init = init;
  }

  public @Nullable readonly_list<annotation_construct> annotations_list() {
    return variable_annotations;
  }

  @Override
  public action_name short_name() {
    return name;
  }

  @Override
  public variable_category get_category() {
    if (category == null) {
      utilities.panic("Null category in " + this + ", pass " + last_pass);
    }
    assert category != null;
    return category;
  }

  /** Get (flavored) variable type. */
  @Override
  public type value_type() {
    assert var_value_type != null;
    return var_value_type;
  }

  @Override
  public type reference_type() {
    assert var_reference_type != null;
    return var_reference_type;
  }

  @Override
  public boolean declared_as_reference() {
    return declared_as_reference;
  }

  @Override
  public @Nullable action init_action() {
    if (init_action != null) {
      return init_action;
    } else {
      assert init == null;
      return null;
    }
  }

  @Override
  public readonly_list<analyzable> children() {
    list<analyzable> result = new base_list<analyzable>();
    result.append(annotations());
    if (variable_type != null) {
      result.append(variable_type);
    }
    if (init != null) {
      result.append(init);
    }
    return result;
  }

  @Override
  protected signal do_multi_pass_analysis(analysis_pass pass) {
    if (pass == analysis_pass.PREPARE_METHOD_AND_VARIABLE) {
      // TODO: signal error
      assert short_name() instanceof simple_name;

      // TODO: select default access modifier based on the frame
      if (variable_annotations != null || post_annotations != null) {
        list<annotation_construct> joined_annotations = new base_list<annotation_construct>();
        if (variable_annotations != null) {
          joined_annotations.append_all(variable_annotations);
        }
        if (post_annotations != null) {
          joined_annotations.append_all(post_annotations);
        }
        // TODO: if not specified, inherit access modifier from the overriden method
        process_annotations(joined_annotations,
            language().get_default_variable_access(outer_kind()));
      }

      if (outer_kind() == type_kinds.block_kind) {
        category = variable_category.LOCAL;
      } else if (is_static_declaration()) {
        category = variable_category.STATIC;
      } else {
        category = variable_category.INSTANCE;
      }
    }

    if (pass == analysis_pass.METHOD_AND_VARIABLE_DECL) {
      return process_declaration();
    }

    if (pass == analysis_pass.BODY_CHECK) {
      if (init != null) {
        if (has_analysis_errors(init)) {
          return report_error(new error_signal(messages.error_in_initializer, init, this));
        }
        set_init();
        if (!get_context().can_promote(init_action, value_type())) {
          return action_utilities.cant_promote(init_action.result(), value_type(),
              get_context(), this);
        }
        init_action = get_context().promote(init_action, value_type(), this);
        assert !(init_action instanceof error_signal);
      }
    }

    return ok_signal.instance;
  }

  private void set_init() {
    assert init != null;
    origin the_origin = init;
    init_action = analyzer_utilities.to_value(action_not_error(init), get_context(), the_origin);
  }

  private signal process_declaration() {
    // TODO: handle init
    if (variable_type != null) {
      add_dependence(variable_type, null, declaration_pass.TYPES_AND_PROMOTIONS);

      if (has_analysis_errors(variable_type)) {
        return report_error(new error_signal(messages.error_in_var_type, variable_type, this));
      }

      action expected_type = action_not_error(variable_type);

      if (expected_type instanceof type_action) {
        var_value_type = ((type_action) expected_type).get_type();
      } else {
        return report_error(new error_signal(messages.type_expected, variable_type));
      }
    } else {
      if (init != null) {
        if (has_analysis_errors(init)) {
          return report_error(new error_signal(messages.error_in_initializer, init, this));
        }
        set_init();
        var_value_type = init_action.result().type_bound();
      } else {
        return report_error(new error_signal(messages.var_type_missing, source));
      }
    }

    if (analyzer_utilities.has_overriden(this)) {
      readonly_list<declaration> overriden = analyzer_utilities.do_find_overriden(this);
      if (overriden.is_empty()) {
        return new error_signal(new base_string("Can't find overriden for '" +
            short_name() + "' in " + declared_in_type()), this);
      }
    } else {
      // TODO: check that override/implement modifiers are not present on
      // static methods/constructors...
      overriden = new empty<declaration>();
    }

    // Check for shadow variables
    // This check is dedicated to the memory of dictionary_state.reserve() bug on August 13, 2014.
    // TODO: add a feature/flag to toggle it.
    if (true) {
      readonly_list<action> shadowed_actions = get_context().resolve(
          declared_in_type().get_flavored(flavor.mutable_flavor), short_name(), this);
      if (shadowed_actions.size() > 0) {
        for (int i = 0; i < shadowed_actions.size(); ++i) {
          action shadowed = shadowed_actions.get(i);
          if (! (shadowed instanceof type_action) && ! (shadowed instanceof error_signal)
              && !annotations().has(general_modifier.override_modifier)) {
            notification original = new base_notification("Shadowed declaration",
                shadowed.get_declaration());
            new base_notification(new base_string("Variable shadows another declaration"), this,
                new base_list<notification>(original)).report();
          }
        }
      }
    }

    assert var_value_type != core_types.error_type();
    if (library().is_reference_type(var_value_type)) {
      return report_error(new error_signal(
          new base_string("Reference type not allowed in the variable declaration"),
          variable_type != null ? variable_type : this));
    }

    var_value_type = analyzer_utilities.handle_default_flavor(var_value_type);
    @Nullable type_flavor reference_flavor = process_flavor(post_annotations);
    declared_as_reference = reference_flavor != null;
    if (!declared_as_reference) {
      // TODO: detect and use deeply_immutable.
      reference_flavor = is_immutable() ? flavor.immutable_flavor : flavor.mutable_flavor;
    }
    var_reference_type = library().get_reference(reference_flavor, var_value_type);

    if (get_category() == variable_category.INSTANCE) {
      the_variable_action = analyzer_utilities.add_instance_variable(this, get_context());
    } else {
      if (get_category() == variable_category.LOCAL) {
        the_variable_action = new local_variable(this, reference_flavor);
      } else {
        assert get_category() == variable_category.STATIC;
        the_variable_action = new static_variable(this, reference_flavor);
      }
      add_var_action(nameonly_flavor, the_variable_action);
    }

    return ok_signal.instance;
  }

  private boolean is_immutable() {
    return !annotations().has(general_modifier.var_modifier) &&
           !annotations().has(general_modifier.mutable_var_modifier);
  }

  private error_signal report_error(error_signal signal) {
    add_error(parent(), short_name(), signal);
    return signal;
  }

  private void add_var_action(type_flavor flavor, variable_action var_action) {
    type flavored_from = declared_in_type().get_flavored(flavor);
    get_context().add(flavored_from, short_name(), var_action);
  }

  public @Nullable action get_init_action() {
    if (init_action == null) {
      return null;
    }
    assert !(init_action instanceof error_signal);
    return init_action;
  }

  public readonly_list<declaration> get_overriden() {
    if (overriden == null) {
      utilities.panic("Null overriden in " + this);
    }
    assert overriden != null;
    return overriden;
  }

  @Override
  public specialized_variable specialize(specialization_context new_context,
      principal_type new_parent) {
    assert get_category() == variable_category.LOCAL ||
        get_category() == variable_category.INSTANCE;

    //type_analyzable = variable_type.specialize(new_context, new_parent);

    // TODO: signal errors instead of asserts.
    action new_value_action;
    if (variable_type != null) {
      new_value_action = analyzer_utilities.to_action(
          variable_type.specialize(new_context, new_parent));
    } else {
      assert init != null;
      new_value_action = analyzer_utilities.to_action(init.specialize(new_context, new_parent));
    }

    // TODO: handle errors better.
    if (new_value_action instanceof error_signal) {
      utilities.panic("In specialize(): " + new_value_action);
      return null;
    }

    assert ! (new_value_action instanceof error_signal);

    type new_value_type = new_value_action.result().type_bound();
    assert new_value_type != core_types.error_type();
    new_value_type = analyzer_utilities.handle_default_flavor(new_value_type);

    type_flavor reference_flavor = var_reference_type.get_flavor();
    type new_reference_type = library().get_reference(reference_flavor, new_value_type);

    specialized_variable new_variable = new specialized_variable(this, new_parent, new_value_type,
        new_reference_type, declared_as_reference, new_value_action);
    new_variable.add(get_context());
    return new_variable;
  }

  @Override
  protected action do_get_result() {
    assert the_variable_action != null;
    return new variable_initializer(the_variable_action, init_action);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, new base_string(parent_name(), ".",
        short_name().to_string()));
  }
}
