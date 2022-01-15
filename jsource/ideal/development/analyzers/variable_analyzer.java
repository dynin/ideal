/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
  private @Nullable analyzable variable_type;
  private @Nullable action_name name;
  private final @Nullable readonly_list<annotation_construct> post_annotations;
  private final @Nullable analyzable init;
  private @Nullable variable_category category;
  private @Nullable type_flavor reference_flavor;
  private @Nullable type_flavor the_flavor;
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
    assert name != null;
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

  @Override
  public type_flavor get_flavor() {
    assert the_flavor != null;
    return the_flavor;
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
            settings().get_default_variable_access(outer_kind()));
      }

      if (name != null) {
        // TODO: signal error
        assert short_name() instanceof simple_name;
        if (annotations().has(general_modifier.the_modifier)) {
          if (variable_type == null) {
            variable_type = new resolve_analyzer(name, this);
            name = name_utilities.join(general_modifier.the_modifier.name(),
                (simple_name) short_name());
          } else {
            return new error_signal(
               new base_string("Both modifier 'the' and name present"), this);
          }
        }
      } else {
        if (!annotations().has(general_modifier.the_modifier)) {
          return new error_signal(
             new base_string("Both modifier 'the' and name absent"), this);
        }
        // TODO: signal error
        assert variable_type != null;
        simple_name the_simple_name = infer_name(variable_type);
        if (the_simple_name == null) {
          return new error_signal(
             new base_string("Can't infer variable name"), variable_type);
        }
        name = name_utilities.join(general_modifier.the_modifier.name(), the_simple_name);
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
        set_intitializer_action();
        if (!get_context().can_promote(init_action, value_type())) {
          return action_utilities.cant_promote(init_action.result(), value_type(), this);
        }
        init_action = get_context().promote(init_action, value_type(), this);
        assert !(init_action instanceof error_signal);
      }
    }

    return ok_signal.instance;
  }

  private @Nullable simple_name infer_name(analyzable the_analyzable) {
    if (the_analyzable instanceof resolve_analyzer) {
      action_name the_action_name = ((resolve_analyzer) the_analyzable).short_name();
      if (the_action_name instanceof simple_name) {
        return (simple_name) the_action_name;
      }
    } else if (the_analyzable instanceof parameter_analyzer) {
      return infer_name(((parameter_analyzer) the_analyzable).main_analyzable);
    } else if (the_analyzable instanceof flavor_analyzer) {
      return infer_name(((flavor_analyzer) the_analyzable).expression);
    }

    return null;
  }

  private void set_intitializer_action() {
    assert init != null;
    origin the_origin = init;
    init_action = analyzer_utilities.to_value(init.analyze().to_action(), get_context(),
        the_origin);
  }

  private boolean is_private(action the_action) {
    @Nullable declaration the_declaration = the_action.get_declaration();
    if (the_declaration instanceof variable_declaration) {
      return ((variable_declaration) the_declaration).annotations().access_level() ==
          access_modifier.private_modifier;
    } else if (the_declaration instanceof procedure_declaration) {
      return ((procedure_declaration) the_declaration).annotations().access_level() ==
          access_modifier.private_modifier;
    } else {
      return false;
    }
  }

  private signal process_declaration() {
    origin the_origin = this;
    // TODO: handle init
    if (variable_type != null) {
      add_dependence(variable_type, null, declaration_pass.TYPES_AND_PROMOTIONS);

      if (has_analysis_errors(variable_type)) {
        return report_error(new error_signal(messages.error_in_var_type, variable_type,
            the_origin));
      }

      action expected_type = variable_type.analyze().to_action();

      if (expected_type instanceof type_action) {
        var_value_type = ((type_action) expected_type).get_type();
      } else {
        return report_error(new error_signal(messages.type_expected, variable_type));
      }
    } else {
      if (init != null) {
        if (has_analysis_errors(init)) {
          return report_error(new error_signal(messages.error_in_initializer, init, the_origin));
        }
        set_intitializer_action();
        var_value_type = init_action.result().type_bound();
      } else {
        return report_error(new error_signal(messages.var_type_missing, source));
      }
    }

    if (analyzer_utilities.has_overriden(this)) {
      readonly_list<declaration> overriden = analyzer_utilities.do_find_overriden(this);
      if (overriden.is_empty()) {
        return new error_signal(new base_string("Can't find overriden for '" +
            short_name() + "' in " + declared_in_type()), the_origin);
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
          declared_in_type().get_flavored(mutable_flavor), short_name(), the_origin);
      if (shadowed_actions.size() > 0) {
        for (int i = 0; i < shadowed_actions.size(); ++i) {
          action shadowed = shadowed_actions.get(i);
          if (!(shadowed instanceof type_action) &&
              !(shadowed instanceof error_signal) &&
              !analyzer_utilities.has_overriden(this) &&
              !is_private(shadowed)) {
            notification original = new base_notification(messages.shadowed_declaration,
                shadowed.get_declaration());
            new base_notification(new base_string("Variable shadows another declaration"),
                the_origin, new base_list<notification>(original)).report();
          }
        }
      }
    }

    assert var_value_type != common_types.error_type();
    declared_as_reference = common_types.is_reference_type(var_value_type);
    if (declared_as_reference) {
      reference_flavor = var_value_type.get_flavor();
      if (reference_flavor == nameonly_flavor) {
        reference_flavor = mutable_flavor;
      }
      var_value_type = common_types.get_reference_parameter(var_value_type);
    } else {
      var_value_type = analyzer_utilities.handle_default_flavor(var_value_type);
      // TODO: detect and use deeply_immutable.
      reference_flavor = is_variable() ? mutable_flavor : immutable_flavor;
    }

    var_reference_type = common_types.get_reference(reference_flavor, var_value_type);

    if (get_category() == variable_category.INSTANCE) {
      boolean is_mutable_var = annotations().has(general_modifier.mutable_var_modifier);
      the_flavor = process_flavor(post_annotations);
      if (the_flavor == null) {
        if (declared_as_reference) {
          the_flavor = is_variable() ? mutable_flavor : readonly_flavor;
        } else {
          the_flavor = is_mutable_var ? mutable_flavor : readonly_flavor;
        }
      } else {
        // TODO: signal an error
        assert the_flavor != writeonly_flavor;
      }
      the_variable_action = analyzer_utilities.add_instance_variable(this, get_context());
    } else {
      the_flavor = nameonly_flavor;
      if (process_flavor(post_annotations) != null) {
        new base_notification(new base_string("Unexpected flavor postannotation"),
            the_origin).report();
      }
      if (get_category() == variable_category.LOCAL) {
        the_variable_action = new local_variable(this, reference_flavor);
      } else {
        assert get_category() == variable_category.STATIC;
        the_variable_action = new static_variable(this, reference_flavor);
      }
      get_context().add(declared_in_type(), short_name(), the_variable_action);
    }

    return ok_signal.instance;
  }

  private boolean is_variable() {
    return annotations().has(general_modifier.var_modifier) ||
           annotations().has(general_modifier.mutable_var_modifier);
  }

  private error_signal report_error(error_signal signal) {
    add_error(parent(), short_name(), signal);
    return signal;
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
      new_value_action = variable_type.specialize(new_context, new_parent).analyze().to_action();
    } else {
      assert init != null;
      new_value_action = init.specialize(new_context, new_parent).analyze().to_action();
    }

    // TODO: handle errors better.
    if (new_value_action instanceof error_signal) {
      utilities.panic("In specialize(): " + new_value_action);
      return null;
    }

    assert ! (new_value_action instanceof error_signal);

    type new_value_type = new_value_action.result().type_bound();
    assert new_value_type != common_types.error_type();
    if (common_types.is_reference_type(new_value_type)) {
      new_value_type = common_types.get_reference_parameter(new_value_type);
    }
    new_value_type = analyzer_utilities.handle_default_flavor(new_value_type);

    assert reference_flavor != null;
    type new_reference_type = common_types.get_reference(reference_flavor, new_value_type);

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
        name != null ?  name.to_string() : new base_string("<noname>")));
  }
}
