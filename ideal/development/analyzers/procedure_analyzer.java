/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
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
import ideal.development.comments.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.values.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class procedure_analyzer extends declaration_analyzer<procedure_construct>
    implements procedure_declaration {

  private action_name name;
  private procedure_category category;
  private master_type inside;
  private list<variable_declaration> parameter_variables;
  private list<type> proc_args;
  private type return_type;
  private type proc_type;
  private procedure_executor result_value;
  private @Nullable type_flavor the_flavor;
  private @Nullable analyzable body;
  private @Nullable analyzable return_analyzable;
  private @Nullable readonly_list<declaration> overriden;
  private @Nullable local_variable_declaration this_decl;
  private boolean calls_this_constructor;

  public procedure_analyzer(procedure_construct source) {
    super(source);

    // TODO: handle namespace in the name...
    assert source.name instanceof simple_name;
  }

  @Override
  public principal_type inner_type() {
    return inside;
  }

  @Override
  public action_name short_name() {
    assert name != null;
    return name;
  }

  @Override
  public procedure_category get_category() {
    assert category != null;
    return category;
  }

  @Override
  public simple_name original_name() {
    return (simple_name) source.name;
  }

  @Override
  public type_flavor get_flavor() {
    assert the_flavor != null;
    return the_flavor;
  }

  public type get_procedure_type() {
    return proc_type;
  }

  @Override
  public readonly_list<variable_declaration> get_parameter_variables() {
    return parameter_variables;
  }

  public @Nullable analyzable get_body() {
    return body;
  }

  @Override
  public @Nullable action get_body_action() {
    if (body != null) {
      // TODO: should we return null on error?
      assert !has_errors(body);
      return action_not_error(body);
    } else {
      return null;
    }
  }

  public boolean is_pure() {
    return annotations().has(general_modifier.pure_modifier);
  }

  @Override
  protected @Nullable error_signal do_multi_pass_analysis(analysis_pass pass) {

    if (pass == analysis_pass.METHOD_AND_VARIABLE_DECL) {
      list<annotation_construct> joined_annotations = new base_list<annotation_construct>();
      joined_annotations.append_all(source.annotations);
      joined_annotations.append_all(source.post_annotations);
      // TODO: if not specified, inherit access modifier from the overriden method
      process_annotations(joined_annotations,
          language().get_default_procedure_access(outer_kind()));
      the_flavor = process_flavor(source.post_annotations);

      assert category == null;
      if (source.ret == null && !annotations().has(general_modifier.testcase_modifier)) {
        category = procedure_category.CONSTRUCTOR;
      } else if (is_static_declaration()) {
        category = procedure_category.STATIC;
      } else {
        category = procedure_category.METHOD;
      }

      if (source.body != null) {
        readonly_list<analyzable> body_statements = make_list(source.body);
        if (category == procedure_category.CONSTRUCTOR) {
          body_statements = rewrite_ctor_body(body_statements);
        }
        body = new statement_list_analyzer(body_statements, this);
      }

      return process_declaration();
    } else if (pass == analysis_pass.BODY_CHECK) {
      // TODO:...check that it's |void_type|
      if (body != null) {
        declare_this_and_super();
	analyze_and_ignore_errors(body, pass);
      }
    }

    return null;
  }

  private @Nullable error_signal process_declaration() {
    if (get_category() == procedure_category.CONSTRUCTOR) {
      name = special_name.IMPLICIT_CALL;
    } else {
      if (annotations().has(general_modifier.implicit_modifier)) {
        name = special_name.IMPLICIT_CALL;
      } else {
        name = source.name;
        // TODO: signal error
        assert name instanceof simple_name;
      }
    }
    inside = make_block(original_name(), declared_in_type(), this);

    switch (get_category()) {
      case CONSTRUCTOR:
        // TODO: signal error if flavor is non-null
        assert the_flavor == null;
        the_flavor = flavors.raw_flavor;
        break;
      case STATIC:
        // TODO: signal error if flavor is non-null
        assert the_flavor == null;
        the_flavor = flavors.nameonly_flavor;
        break;
      case METHOD:
        if (the_flavor == null) {
          if (is_pure()) {
            the_flavor = flavors.readonly_flavor;
          } else {
            the_flavor = flavors.DEFAULT_FLAVOR;
          }
        }
        break;
    }

    readonly_list<construct> parameters = source.parameters.elements;
    parameter_variables = new base_list<variable_declaration>();
    proc_args = new base_list<type>();

    error_signal arg_error = null;
    for (int i = 0; i < parameters.size(); ++i) {
      construct the_parameter = parameters.get(i);
      variable_analyzer the_argument;
      if (the_parameter instanceof variable_construct) {
        the_argument = new variable_analyzer((variable_construct) the_parameter);
      } else if (the_parameter instanceof name_construct) {
        action_name the_name = ((name_construct) the_parameter).the_name;
        // TODO: construct variable_analyzer directly.
        // TODO: 'this' should be immutable.
        the_argument = new variable_analyzer(
            new variable_construct(new empty<annotation_construct>(), null,
                the_name, new empty<annotation_construct>(), null, the_parameter));
      } else {
        arg_error = new error_signal(new base_string("Variable expected"), the_parameter);
        continue;
      }
      parameter_variables.append(the_argument);
      @Nullable error_signal ae = find_error(the_argument);
      if (ae != null) {
	arg_error = new error_signal(messages.error_in_fn_param, ae, source);
      } else {
        if (the_argument.declared_as_reference()) {
          proc_args.append(the_argument.reference_type());
        } else {
          proc_args.append(the_argument.value_type());
        }
      }
    }

    if (arg_error != null) {
      add_error(declared_in_type(), name, arg_error);
      return arg_error;
    }

    if (get_category() == procedure_category.CONSTRUCTOR) {
      // what happens for readonly types?  FIXME
      // Also: implicit returns for constructors?
      principal_type the_type = declared_in_type();
      assert the_type.get_declaration() instanceof type_declaration;
      return_type = the_type.get_flavored(flavors.mutable_flavor);
    } else {
      // what if return expression is not a type?
      // TODO: expect static types.
      if (source.ret != null) {
        return_analyzable = make(source.ret);
        add_dependence(return_analyzable, null, declaration_pass.TYPES_AND_PROMOTIONS);
      } else {
        return_analyzable = analyzable_action.from_value(library().void_type(), this);
      }
      @Nullable error_signal return_error = find_error(return_analyzable);
      if (return_error != null) {
        return new error_signal(new base_string("Error in return declaration"),
            return_error, source.ret);
      }

      action return_action = action_not_error(return_analyzable);
      if (! (return_action instanceof type_action)) {
        return new error_signal(messages.type_expected, source.ret);
      }

      if (annotations().has(general_modifier.noreturn_modifier)) {
        // TODO: check that return original return type is void..
        return_type = core_types.unreachable_type();
      } else {
        return_type = analyzer_utilities.handle_default_flavor(
            ((type_action) return_action).get_type());
      }
    }

    // TODO: should this be handled in METHOD_AND_VARIABLE_DECL pass?
    if (analyzer_utilities.has_overriden(this)) {
      readonly_list<declaration> found_overriden = analyzer_utilities.do_find_overriden(this);
      if (found_overriden.is_empty()) {
        return new error_signal(new base_string("Can't find overriden for '" +
            original_name() + "' in " + declared_in_type()), this);
      }
      overriden = found_overriden;
      update_annotations();
    } else {
      // TODO: check that override/implement modifiers are not present on
      // static methods/constructors...
      overriden = new empty<declaration>();
    }

    // TODO: bind...
    list<abstract_value> proc_params = new base_list<abstract_value>();
    proc_params.append(return_type);
    // TODO: this cast is redundant.
    proc_params.append_all((list<abstract_value>)(list) proc_args);
    // TODO: use procedure_util...
    master_type the_procedure_type = is_pure() ? library().function_type() :
        library().procedure_type();
    proc_type = the_procedure_type.bind_parameters(new type_parameters(proc_params)).
        get_flavored(flavors.immutable_flavor);

    result_value = new procedure_executor(this);
    analyzer_utilities.add_procedure(this, result_value, get_context());

    return null;
  }

  private void declare_this_and_super() {
    if (get_category() == procedure_category.STATIC) {
      return;
    }

    @Nullable type superclass = find_super_class();

    switch (get_category()) {
      case CONSTRUCTOR:
        if (superclass != null) {
          add_super_reference(special_name.SUPER_CONSTRUCTOR, superclass, flavors.raw_flavor);
        }
        get_context().add(inside, special_name.THIS_CONSTRUCTOR,
            make_this_variable(flavors.raw_flavor).get_access().to_action(source));

        // Here comes a subtle point.  If one constructor invokes another using this() call,
        // then it is assumed that all the invariants a met after first constructor executes,
        // and 'this' is of mutable flavor and not of 'raw' flavor.  Needs to be documented.
        do_declare_this(calls_this_constructor ? flavors.mutable_flavor : flavors.raw_flavor);
        break;

      case METHOD:
        if (superclass != null) {
          add_super_reference(special_name.SUPER, superclass, get_flavor());
        }
        do_declare_this(get_flavor());
        break;

      default:
        utilities.panic("Unknown procedure category");
    }
  }

  @Override
  public readonly_list<declaration> get_overriden() {
    assert overriden != null;
    return overriden;
  }

  @Override
  public boolean overrides_variable() {
    readonly_list<declaration> overriden = get_overriden();
    for (int i = 0; i < overriden.size(); ++i) {
      if (overriden.get(i) instanceof variable_declaration) {
        return true;
      }
      // TODO: what if it overrides variable indirectly?..
    }
    return false;
  }

  private void update_annotations() {
    // TODO: we should update access modifier here as well.
    if (annotations().the_documentation() == null) {
      @Nullable documentation new_documentation = null;
      readonly_list<declaration> overriden = get_overriden();
      for (int i = 0; i < overriden.size(); ++i) {
        declaration the_declaration = overriden.get(i);
        @Nullable documentation parent_documentation;
        if (the_declaration instanceof variable_declaration) {
          parent_documentation = ((variable_declaration) the_declaration).
              annotations().the_documentation();
        } else if (the_declaration instanceof procedure_declaration) {
          parent_documentation = ((procedure_declaration) the_declaration).
              annotations().the_documentation();
        } else {
          continue;
        }
        if (parent_documentation != null && parent_documentation != new_documentation) {
          // TODO: signal warning, do not panic...
          assert new_documentation == null;
          new_documentation = parent_documentation;
        }
      }
      if (new_documentation != null) {
        set_annotations(
          ((base_annotation_set) annotations()).update_documentation(new_documentation));
      }
    }
  }

  private @Nullable type find_super_class() {
    principal_type this_type = declared_in_type();
    readonly_list<type> supertypes = action_utilities.get_supertypes(this_type);

    list<type> superclasses = new base_list<type>();
    for (int i = 0; i < supertypes.size(); ++i) {
      type supertype = supertypes.get(i);
      if (supertype.principal().get_kind() == type_kinds.class_kind) {
        superclasses.append(supertype);
      }
    }

    if (superclasses.size() > 1) {
      // TODO: use error_signal
      utilities.panic("More than one superclass for " + this_type);
    }

    return superclasses.is_empty() ? null : superclasses.get(0);
  }

  private void do_declare_this(type_flavor this_flavor) {
    assert this_decl == null;
    local_variable_declaration this_variable = make_this_variable(this_flavor);
    this_decl = this_variable;
    action this_action = this_variable.get_access().to_action(this);
    get_context().add(inside, special_name.THIS, this_action);
    get_context().add(inside, special_name.PROMOTION, this_action);

    // TODO: we should do this automatically when instantiating reference.
    type_utilities.prepare(this_action.result(), declaration_pass.METHODS_AND_VARIABLES);
  }

  @Override
  public specialized_procedure specialize(specialization_context new_context,
      principal_type new_parent) {
    assert get_category() != procedure_category.STATIC;
    type_declaration the_declaration = (type_declaration) declared_in_type().get_declaration();

    principal_type new_inside = make_block(original_name(), new_parent, this);
    type new_return_type;
    if (get_category() == procedure_category.CONSTRUCTOR) {
      new_return_type = new_parent.get_flavored(flavors.mutable_flavor);
    } else {
      analyzable return_specialized = return_analyzable.specialize(new_context, new_inside);
      abstract_value return_value = analyzer_utilities.to_action(return_specialized).result();
      if (return_value instanceof error_signal) {
        new_return_type = return_value.type_bound();
      } else {
        new_return_type = analyzer_utilities.handle_default_flavor(return_value);
      }
    }
    list<variable_declaration> new_parameters = new base_list<variable_declaration>();
    for (int i = 0; i < parameter_variables.size(); ++i) {
      new_parameters.append(parameter_variables.get(i).specialize(new_context, new_inside));
    }
    specialized_procedure result = new specialized_procedure(this, new_return_type, new_parent,
        new_parameters);
    result.add(get_context());
    return result;
  }

  @Override
  public variable_declaration get_this_declaration() {
    assert this_decl != null;
    return this_decl;
  }

  private local_variable_declaration make_this_variable(type_flavor this_flavor) {
    position source = this;
    return new local_variable_declaration(analyzer_utilities.THIS_MODIFIERS, special_name.THIS,
        inside, flavors.immutable_flavor, declared_in_type().get_flavored(this_flavor), null,
        source);
  }

  private void add_super_reference(special_name the_name, type superclass,
      type_flavor super_flavor) {
    position source = this;
    local_variable_declaration super_decl = new local_variable_declaration(
        analyzer_utilities.THIS_MODIFIERS, the_name, inside,
        flavors.immutable_flavor, superclass.get_flavored(super_flavor), null, source);
    get_context().add(inside, the_name, super_decl.get_access().to_action(source));
  }

  @Override
  protected action do_get_result() {
    return result_value.to_action(this);
  }

  @Override
  public readonly_list<type> get_argument_types() {
    assert proc_args != null;
    return proc_args;
  }

  @Override
  public type get_return_type() {
    assert return_type != null;
    return return_type;
  }

  private static @Nullable special_name map_constructor_name(resolve_analyzer source_name) {
    if (!source_name.has_from()) {
      action_name the_name = source_name.short_name();
      if (the_name == special_name.THIS) {
        return special_name.THIS_CONSTRUCTOR;
      } else if (the_name == special_name.SUPER) {
        return special_name.SUPER_CONSTRUCTOR;
      }
    }

    return null;
  }

  private readonly_list<analyzable> rewrite_ctor_body(readonly_list<analyzable> body) {
    if (!body.is_empty()) {
      analyzable first = body.get(0);
      if (first instanceof parameter_analyzer) {
        analyzable first_main = ((parameter_analyzer) first).main_analyzable;
        if (first_main instanceof resolve_analyzer) {
          resolve_analyzer main_name = (resolve_analyzer) first_main;
          @Nullable special_name mapped_name = map_constructor_name(main_name);
          if (mapped_name != null) {
            calls_this_constructor = mapped_name == special_name.THIS_CONSTRUCTOR;
            resolve_analyzer ctor_name = new resolve_analyzer(mapped_name,
                main_name.source_position());
            readonly_list<analyzable> ctor_params =
                ((parameter_analyzer) first).analyzable_parameters;
            analyzable ctor_call = new parameter_analyzer(ctor_name, ctor_params,
                first.source_position());
            list<analyzable> result = new base_list<analyzable>();
            result.append(ctor_call);
            result.append_all(body.slice(1));
            return result;
          }
        }
      }
    }

    return body;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, new base_string(declared_in_type().short_name().to_string(),
        ".", original_name().to_string()));
  }
}
