/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

public class variable_analyzer extends declaration_analyzer<variable_construct>
    implements variable_declaration {

  private @Nullable variable_category category;
  private @Nullable analyzable variable_type;
  private @Nullable analyzable init;
  private @Nullable type var_value_type;
  private @Nullable type var_reference_type;
  private variable_action the_variable_action;
  private boolean declared_as_reference;
  private @Nullable action init_action;

  public variable_analyzer(variable_construct source) {
    super(source);
    if (source.type != null) {
      variable_type = make(source.type);
    }
    if (source.init != null) {
      init = make(source.init);
    }
  }

  @Override
  public action_name short_name() {
    return source.name;
  }

  @Override
  public variable_category get_category() {
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

  public boolean declared_as_reference() {
    return declared_as_reference;
  }

  @Override
  public @Nullable action get_init() {
    if (init_action != null) {
      return init_action;
    } else {
      assert init == null;
      return null;
    }
  }

  @Override
  protected @Nullable error_signal do_multi_pass_analysis(analysis_pass pass) {

    if (pass == analysis_pass.METHOD_AND_VARIABLE_DECL) {
      // TODO: signal error
      assert short_name() instanceof simple_name;

      // TODO: select default access modifier based on the frame
      process_annotations(source.annotations, language().get_default_variable_access(outer_kind()));

      if (outer_kind() == type_kinds.block_kind) {
        category = variable_category.LOCAL;
      } else if (is_static_declaration()) {
        category = variable_category.STATIC;
      } else {
        category = variable_category.INSTANCE;
      }

      if (get_category() != variable_category.LOCAL) {
        return process_declaration();
      }
    }

    if (pass == analysis_pass.BODY_CHECK) {
      if (get_category() == variable_category.LOCAL) {
        @Nullable error_signal result = process_declaration();
        if (result != null) {
          return result;
        }
      }
      if (init != null) {
        if (has_errors(init)) {
          return report_error(new error_signal(messages.error_in_initializer, init, this));
        }
        init_action = action_not_error(init);
        if (!get_context().can_promote(init_action.result(), value_type())) {
          return action_utilities.cant_promote(init_action.result(), value_type(),
              get_context(), this);
        }
        init_action = get_context().promote(init_action, value_type(), this);
        assert !(init_action instanceof error_signal);
      }
    }

    return null;
  }

  private @Nullable error_signal process_declaration() {
    // TODO: handle init
    if (variable_type != null) {
      add_dependence(variable_type, null, declaration_pass.TYPES_AND_PROMOTIONS);

      if (has_errors(variable_type)) {
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
        if (has_errors(init)) {
          return report_error(new error_signal(messages.error_in_initializer, init, this));
        }
        init_action = action_utilities.to_value(action_not_error(init), init);
        var_value_type = init_action.result().type_bound();
      } else {
        return report_error(new error_signal(messages.var_type_missing, source));
      }
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
          if (! (shadowed instanceof type_action) && ! (shadowed instanceof error_signal)) {
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
          source.type != null ? source.type : this));
    }

    var_value_type = analyzer_utilities.handle_default_flavor(var_value_type);
    @Nullable type_flavor reference_flavor = process_flavor(source.post_annotations);
    declared_as_reference = reference_flavor != null;
    if (!declared_as_reference) {
      // TODO: detect and use deeply_immutable.
      reference_flavor = is_immutable() ? flavor.immutable_flavor : flavor.mutable_flavor;
    }
    var_reference_type = library().get_reference(reference_flavor, var_value_type);

    if (get_category() == variable_category.INSTANCE) {
      analyzer_utilities.add_instance_variable(this, get_context());
    } else {
      if (get_category() == variable_category.LOCAL) {
        the_variable_action = new local_variable(this, reference_flavor);
      } else {
        assert get_category() == variable_category.STATIC;
        the_variable_action = new static_variable(this, reference_flavor);
      }
      add_var_action(nameonly_flavor, the_variable_action);
    }

    return null;
  }

  private boolean is_immutable() {
    return !annotations().has(general_modifier.var_modifier);
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

  @Override
  public specialized_variable specialize(specialization_context new_context,
      principal_type new_parent) {
    assert get_category() == variable_category.LOCAL ||
        get_category() == variable_category.INSTANCE;

    @Nullable action new_init = null;
    if (init != null) {
      new_init = analyzer_utilities.to_action(init.specialize(new_context, new_parent));
    }

    // TODO: signal errors instead of asserts.
    action new_value_action;
    if (variable_type != null) {
      new_value_action =  analyzer_utilities.to_action(
          variable_type.specialize(new_context, new_parent));
    } else {
      assert new_init != null;
      new_value_action = new_init;
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
        new_reference_type, new_init);
    new_variable.add(get_context());
    return new_variable;
  }

  @Override
  protected action do_get_result() {
    assert the_variable_action != null;
    if (init_action == null) {
      return library().void_instance().to_action(this);
    } else {
      return new variable_initializer(the_variable_action, init_action);
    }
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}
