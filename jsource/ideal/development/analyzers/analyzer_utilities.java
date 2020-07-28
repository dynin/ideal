/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import static ideal.development.flavors.flavor.*;
import ideal.development.declarations.*;
import ideal.development.modifiers.*;
import ideal.development.kinds.*;
import ideal.development.origins.*;
import ideal.development.values.*;

import javax.annotation.Nullable;

public class analyzer_utilities {

  private analyzer_utilities() { }

  public static final origin UNINITIALIZED_POSITION =
      new special_origin(new base_string("[uninitialized]"));

  public static final annotation_set PUBLIC_MODIFIERS =
      new base_annotation_set(access_modifier.public_modifier,
          new hash_set<modifier_kind>(), null);

  public static final annotation_set PRIVATE_MODIFIERS =
      new base_annotation_set(access_modifier.private_modifier,
          new hash_set<modifier_kind>(), null);

  public static final annotation_set PRIVATE_VAR_MODIFIERS =
      new base_annotation_set(access_modifier.private_modifier,
          make_singleton(general_modifier.var_modifier), null);

  public static final annotation_set PRIVATE_FINAL_MODIFIERS =
      new base_annotation_set(access_modifier.private_modifier,
          make_singleton(general_modifier.final_modifier), null);

  // TODO: implement singleton collection
  private static readonly_set<modifier_kind> make_singleton(modifier_kind the_modifier) {
    set<modifier_kind> result = new hash_set<modifier_kind>();
    result.add(the_modifier);
    return result;
  }

  public static @Nullable procedure_declaration get_enclosing_procedure(
      base_analyzer the_analyzable) {
    principal_type frame = the_analyzable.parent();
    while (frame != null) {
      if (frame.get_declaration() != null &&
          frame.get_declaration() instanceof procedure_analyzer) {
        return (procedure_analyzer) frame.get_declaration();
      }
      frame = frame.get_parent();
    }
    return null;
  }

  public static @Nullable loop_action get_enclosing_loop(base_analyzer the_analyzable) {
    principal_type frame = the_analyzable.parent();
    while (frame != null) {
      if (frame.get_declaration() != null &&
          frame.get_declaration() instanceof loop_analyzer) {
        return ((loop_analyzer) frame.get_declaration()).get_loop_action();
      }
      frame = frame.get_parent();
    }
    return null;
  }

  public static boolean is_readonly_reference(type the_type) {
    return common_library.get_instance().is_reference_type(the_type) &&
           the_type.get_flavor() == readonly_flavor;
  }

  public static boolean has_overriden(procedure_declaration the_procedure) {
    return the_procedure.get_category() == procedure_category.METHOD &&
        (the_procedure.annotations().has(general_modifier.override_modifier) ||
         the_procedure.annotations().has(general_modifier.implement_modifier));
  }

  public static boolean is_overloaded(procedure_declaration the_procedure) {
    return the_procedure.annotations().has(general_modifier.overload_modifier);
  }

  public static @Nullable action add_procedure(procedure_declaration the_procedure,
      @Nullable overloaded_procedure the_overloaded_procedure,
      analysis_context the_context) {

    boolean is_explicit = the_procedure.annotations().has(general_modifier.explicit_modifier);
    origin the_origin = the_procedure;
    final type target_type =
        the_procedure.declared_in_type().get_flavored(the_procedure.get_flavor());
    procedure_executor the_executor = new procedure_executor(the_procedure);
    action executor_action = the_executor.to_action(the_origin);

    if (is_overloaded(the_procedure)) {
      if (the_overloaded_procedure != null) {
        the_overloaded_procedure.add(the_executor);
      } else {
        the_overloaded_procedure = new overloaded_procedure(the_executor);
        action the_action = the_overloaded_procedure.to_action(the_origin);
        if (!is_explicit) {
          the_context.add(target_type, the_procedure.short_name(), the_action);
        }
      }
      return executor_action;
    }

    action result_action;
    if (the_procedure.overrides_variable()) {
      // TODO: can this cast ever fail?
      result_action = (action) new procedure_executor(the_procedure).
          bind_parameters(new action_parameters(), the_context, the_origin);
      readonly_list<declaration> overriden = the_procedure.get_overriden();
      for (int i = 0; i < overriden.size(); ++i) {
        if (overriden.get(i) instanceof variable_declaration) {
          variable_declaration super_var = (variable_declaration) overriden.get(i);
          dispatch_action the_dispatch = get_readonly_action(super_var, the_context);
          if (!the_dispatch.handles_type(target_type)) {
            the_dispatch.add_handler(target_type, new proc_as_ref_action(the_procedure));
          }
        }
      }
    } else if (the_procedure.get_category() == procedure_category.METHOD) {
      readonly_list<declaration> overriden_list = the_procedure.get_overriden();
      set<dispatch_action> dispatches = new hash_set<dispatch_action>();
      for (int i = 0; i < overriden_list.size(); ++i) {
        if (! (overriden_list.get(i) instanceof procedure_declaration)) {
          // TODO: handle this case
          // System.out.println(the_procedure + " DECLARES " + overriden_list.get(i));
          continue;
        }
        procedure_declaration overriden = (procedure_declaration) overriden_list.get(i);
        action procedure_action = overriden.procedure_action();
        if (procedure_action == null) {
          continue;
        }
        if (!(procedure_action instanceof dispatch_action)) {
          // TODO: handle this case
          // System.out.println(the_procedure + " OVERRIDES " + procedure_action);
          continue;
        }
        dispatch_action the_dispatch = (dispatch_action) overriden.procedure_action();
        assert !the_dispatch.handles_type(target_type);
        dispatches.add(the_dispatch);
      }

      if (dispatches.is_empty()) {
        result_action = new dispatch_action(executor_action, target_type);
      } else {
        readonly_list<dispatch_action> dispatches_list = dispatches.elements();
        result_action = null;  // To make javac happy
        for (int i = 0; i < dispatches_list.size(); ++i) {
          dispatch_action the_dispatch = dispatches_list.get(i);
          the_dispatch.add_handler(target_type, executor_action);
          // TODO: what of there is more than one dispatch?
          // Which one should we pick then?
          // TODO: retiring .bind_from(target_type.to_action(the_origin), the_origin);
          result_action = the_dispatch;
        }
      }
    } else {
      result_action = executor_action;
    }

    if (!is_explicit) {
      the_context.add(target_type, the_procedure.short_name(), result_action);
    }

    return result_action;
  }

  public static boolean is_immutable(variable_declaration the_variable_declaration) {
    type reference_type = the_variable_declaration.reference_type();
    assert common_library.get_instance().is_reference_type(reference_type);
    type_flavor reference_flavor = reference_type.get_flavor();
    return reference_flavor == immutable_flavor || reference_flavor == deeply_immutable_flavor;
  }

  private static final type_flavor dispatch_flavor = readonly_flavor;

  private static dispatch_action get_readonly_action(variable_declaration the_variable,
      analysis_context the_context) {
    assert the_variable.get_category() == variable_category.INSTANCE;
    readonly_list<action> actions = the_context.lookup(
        the_variable.declared_in_type().get_flavored(dispatch_flavor), the_variable.short_name());
    assert actions.size() == 1;
    return (dispatch_action) actions.first();
  }

  public static void add_instance_variable(variable_declaration the_variable,
      analysis_context the_context) {
    assert the_variable.get_category() == variable_category.INSTANCE;
    principal_type parent_type = the_variable.declared_in_type();
    action_name the_name = the_variable.short_name();

    // All instance variables can be read
    type flavored_from = parent_type.get_flavored(dispatch_flavor);
    dispatch_action the_dispatch = new dispatch_action(
        new instance_variable(the_variable, dispatch_flavor), flavored_from);
    the_context.add(flavored_from, the_name, the_dispatch);

    if (the_variable.reference_type().get_flavor() == mutable_flavor &&
        parent_type.get_flavor_profile() == flavor_profiles.mutable_profile) {
      the_context.add(parent_type.get_flavored(mutable_flavor), the_name,
          new instance_variable(the_variable, mutable_flavor));
    }

    if (action_utilities.supports_constructors(parent_type)) {
      the_context.add(parent_type.get_flavored(raw_flavor), the_name,
          new instance_variable(the_variable, mutable_flavor));
    }
  }

  public static readonly_list<declaration> do_find_overriden(procedure_declaration the_procedure) {
    type_declaration self_declaration = declaration_util.get_type_declaration(
        the_procedure.declared_in_type());
    if (self_declaration == null) {
      return new empty<declaration>();
    }

    list<declaration> result = new base_list<declaration>();
    set<type_declaration> processed_declarations = new hash_set<type_declaration>();
    append_overriden(the_procedure, self_declaration, result, processed_declarations);
    // TODO: there should not be a reason for having more than one overriden declarations,
    //       except for rare circumstances.
    return result;
  }

  private static void append_overriden(procedure_declaration the_procedure,
      type_declaration the_declaration, list<declaration> result,
      set<type_declaration> processed_declarations) {
    if (processed_declarations.contains(the_declaration)) {
      return;
    }
    processed_declarations.add(the_declaration);

    if (the_declaration.get_kind() == type_kinds.procedure_kind &&
        the_procedure.short_name() == special_name.IMPLICIT_CALL) {
      result.append(the_declaration);
    }

    readonly_list<type> supertypes = action_utilities.get_supertypes(
        the_declaration.get_declared_type());
    for (int i = 0; i < supertypes.size(); ++i) {
      type_declaration supertype_declaration =
          declaration_util.get_type_declaration(supertypes.get(i));
      if (supertype_declaration == null) {
        continue;
      }
      type_utilities.prepare(supertype_declaration.get_declared_type(),
          declaration_pass.METHODS_AND_VARIABLES);
      readonly_list<declaration> super_declarations = supertype_declaration.get_signature();
      for (int j = 0; j < super_declarations.size(); ++j) {
        declaration decl = super_declarations.get(j);
        if (decl instanceof procedure_declaration &&
            does_override(the_procedure, (procedure_declaration) decl)) {
          result.append(decl);
        } else if (decl instanceof variable_declaration &&
            utilities.eq(((variable_declaration) decl).short_name(), the_procedure.short_name())) {
          result.append(decl);
        }
      }
    }

    if (result.is_empty()) {
      for (int i = 0; i < supertypes.size(); ++i) {
        type_declaration supertype_declaration =
            declaration_util.get_type_declaration(supertypes.get(i));
        if (supertype_declaration != null) {
          append_overriden(the_procedure, supertype_declaration, result, processed_declarations);
        }
      }
    }
  }

  // TODO: other checks here?...
  private static boolean does_override(procedure_declaration the_procedure,
      procedure_declaration candidate) {
    return the_procedure.original_name() == candidate.original_name() &&
           the_procedure.get_parameter_variables().size() ==
              candidate.get_parameter_variables().size();
  }

  public static action to_action(analyzable the_analyzable) {
    analysis_result result = the_analyzable.analyze();
    if (result instanceof action) {
      return (action) result;
    } else if (result instanceof action_plus_constraints) {
      return ((action_plus_constraints) result).the_action;
    } else if (result instanceof error_signal) {
      return new error_action((error_signal) result);
    } else {
      utilities.panic("Unrecognized analyzable: " + the_analyzable);
      return null;
    }
  }

  public static @Nullable type unify(action first, action second, analysis_context the_context) {
    type first_type = first.result().type_bound();
    type second_type = second.result().type_bound();

    if (first_type == second_type) {
      return first_type;
    } else if (the_context.can_promote(first, second_type)) {
      return second_type;
    } else if (the_context.can_promote(second, first_type)) {
      return first_type;
    }

    type immutable_void_type = common_library.get_instance().immutable_void_type();
    if (the_context.can_promote(first, immutable_void_type) &&
        the_context.can_promote(second, immutable_void_type)) {
      return immutable_void_type;
    }

    return null;
  }

  public static action to_value(action expression, analysis_context the_context,
      origin the_origin) {
    type the_type = expression.result().type_bound();
    if (common_library.get_instance().is_reference_type(the_type)) {
      // TODO: check that flavor is readonly or mutable.
      type value_type = common_library.get_instance().get_reference_parameter(the_type);
      // TODO: replace this with a promotion lookup.
      return the_context.promote(expression, value_type, the_origin);
    } else {
      return expression;
    }
  }

  public static list<constraint> always_by_type(immutable_list<constraint> the_constraints,
      constraint_type filter) {
    list<constraint> filtered_constraints = new base_list<constraint>();
    for (int i = 0; i < the_constraints.size(); ++i) {
      constraint the_constraint = the_constraints.get(i);
      constraint_type current_type = the_constraint.the_constraint_type;
      if (current_type == constraint_type.ALWAYS) {
        filtered_constraints.append(the_constraint);
      } else if (current_type == filter) {
        filtered_constraints.append(new constraint(the_constraint.the_declaration,
            the_constraint.the_value, constraint_type.ALWAYS));
      }
    }
    return filtered_constraints;
  }

  public static boolean is_parametrizable(abstract_value the_value, action_parameters parameters,
      analysis_context the_context) {
    type the_type = the_value.type_bound();
    if (the_type instanceof master_type && ((master_type) the_type).is_parametrizable()) {
      // TODO: more checks...
      return true;
    }

    if (the_value instanceof procedure_value) {
      return ((procedure_value) the_value).is_parametrizable(parameters, the_context);
    }

    type procedure_type = the_context.find_supertype_procedure(the_value);
    if (procedure_type == null) {
      return false;
    }
    principal_type procedure_principal = procedure_type.principal();
    assert procedure_principal.get_kind() == type_kinds.procedure_kind;
    if (! (procedure_principal instanceof parametrized_type)) {
      // This is unexpected...
      utilities.panic("Got non-parametrized procedure");
      return false;
    }

    immutable_list<action> argument_actions = parameters.params();

    if (!action_utilities.is_valid_procedure_arity(procedure_type, argument_actions.size())) {
      return false;
    }

    for (int i = 0; i < argument_actions.size(); ++i) {
      if (!the_context.can_promote(argument_actions.get(i),
          action_utilities.get_procedure_argument(procedure_type, i).type_bound())) {
        return false;
      }
    }

    return true;
  }

  public static type handle_default_flavor(abstract_value the_value) {
    assert the_value instanceof type;
    type the_type = (type) the_value;
    if (type_utilities.is_union(the_type)) {
      immutable_list<abstract_value> parameters = type_utilities.get_union_parameters(the_type);
      list<abstract_value> new_parameters = new base_list<abstract_value>();
      for (int i = 0; i < parameters.size(); ++i) {
        new_parameters.append(handle_default_flavor(parameters.get(i)));
      }
      return type_utilities.make_union(new_parameters);
    } else if (the_type instanceof principal_type && !type_utilities.is_type_alias(the_type)) {
      principal_type principal = (principal_type) the_type;
      return principal.get_flavored(type_utilities.get_flavor_profile(principal).default_flavor());
    } else {
      return the_type;
    }
  }

  public static analysis_result bind_parameters(action the_action, action_parameters parameters,
      analysis_context the_context, origin the_origin) {

    abstract_value action_result = the_action.result();
    // TODO: this is redundant, drop...
    assert is_parametrizable(action_result, parameters, the_context);

    type the_type = action_result.type_bound();
    if (the_type instanceof master_type && ((master_type) the_type).is_parametrizable()) {
      return bind_type_parameters((master_type) the_type, parameters).to_action(the_origin);
    }

    if (action_result instanceof procedure_value &&
        ((procedure_value) action_result).is_parametrizable(parameters, the_context)) {
      return ((procedure_value) action_result).bind_parameters(parameters, the_context, the_origin);
    }

    type procedure_type = the_context.find_supertype_procedure(action_result);
    assert procedure_type != null;
    assert procedure_type.principal().get_kind() == type_kinds.procedure_kind;

    action the_procedure = the_context.promote(the_action, procedure_type, the_origin);
    assert the_procedure != null;
    // TODO: actually, if this happens, just return the error.
    assert !(the_procedure instanceof error_signal);

    immutable_list<action> argument_actions = parameters.params();

    if (!action_utilities.is_valid_procedure_arity(procedure_type, argument_actions.size())) {
      utilities.panic("Invalid procedure arity: " + procedure_type);
    }

    list<action> promoted_parameters = new base_list<action>();

    for (int i = 0; i < argument_actions.size(); ++i) {
      promoted_parameters.append(the_context.promote(argument_actions.get(i),
          action_utilities.get_procedure_argument(procedure_type, i).type_bound(),
          the_origin));
    }

    abstract_value return_type = action_utilities.get_procedure_return(procedure_type);
    return new bound_procedure(the_procedure, return_type,
        new action_parameters(promoted_parameters), the_origin);
  }

  private static type bind_type_parameters(master_type the_type, action_parameters parameters) {
    immutable_list<action> action_parameters = parameters.params();
    list<abstract_value> args = new base_list<abstract_value>();
    for (int i = 0; i < action_parameters.size(); ++i) {
      abstract_value arg = action_parameters.get(i).result();
      if (arg instanceof type) {
        arg = handle_default_flavor(arg);
      }
      args.append(arg);
    }
    return the_type.bind_parameters(new type_parameters(args));
  }

  public static void analyze_and_prepare(principal_type the_type) {
    declaration the_declaration = the_type.get_declaration();
    if (the_declaration instanceof type_announcement_analyzer) {
      ((type_announcement_analyzer) the_declaration).analyze();
    }
    type_utilities.prepare(the_type, declaration_pass.METHODS_AND_VARIABLES);
  }
}
