/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import static ideal.development.kinds.type_kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.declarations.*;
import ideal.development.origins.*;
import ideal.development.values.*;

public class action_utilities {

  private action_utilities() { }

  static final origin no_origin = new special_origin(new base_string("no-origin"));

  public static readonly_list<type> lookup_types(analysis_context context, type from,
      action_name name) {
    // TODO: use map.
    list<type> result = new base_list<type>();
    readonly_list<action> types = context.lookup(from, name);
    for (int i = 0; i < types.size(); ++i) {
      action a = types.get(i);
      if (a instanceof type_action) {
        result.append(((type_action) a).get_type());
      } else {
        // This is unexpected; but we should be able just to ignore it.
        // For now, panic to get attention.
        utilities.panic("Type expected " + a);
      }
    }
    return result;
  }

  // TODO: use declaration_utils.get_declared_supertypes()
  public static readonly_list<type> get_supertypes(principal_type the_type) {
    declaration the_declaration = the_type.get_declaration();
    assert the_declaration instanceof type_declaration;
    readonly_list<declaration> signature = ((type_declaration) the_declaration).get_signature();
    list<type> result = new base_list<type>();
    for (int i = 0; i < signature.size(); ++i) {
      declaration d = signature.get(i);
      if (d instanceof supertype_declaration) {
        supertype_declaration the_supertype_declaration = (supertype_declaration) d;
        if (!the_supertype_declaration.has_errors()) {
          result.append(the_supertype_declaration.get_supertype());
        }
      }
    }
    return result;
  }

  public static void add_promotion(analysis_context context, type from, type to, origin pos) {
    if (to instanceof principal_type) {
      context.add(from, special_name.PROMOTION, to.to_action(pos));
    } else {
      context.add(from, special_name.PROMOTION, new promotion_action(to, pos));
    }
  }

  public static master_type make_type(analysis_context context, kind kind,
      @Nullable flavor_profile the_flavor_profile, action_name name, principal_type parent,
      @Nullable declaration the_declaration, origin pos) {
    master_type result = new master_type(kind, the_flavor_profile, name, parent, context,
        the_declaration);
    context.add(parent, name, result.to_action(pos));
    return result;
  }

  public static boolean is_procedure_type(type the_type) {
    return the_type.principal().get_kind() == type_kinds.procedure_kind;
  }

  public static boolean is_valid_procedure_arity(type procedure_type, int arity) {
    assert is_procedure_type(procedure_type);
    return ((parametrized_type) procedure_type.principal()).get_parameters().
        is_valid_arity(arity + 1);
  }

  public static abstract_value get_procedure_argument(type procedure_type, int index) {
    assert is_procedure_type(procedure_type);
    return ((parametrized_type) procedure_type.principal()).get_parameters().get(index + 1);
  }

  public static abstract_value get_procedure_return(type procedure_type) {
    principal_type the_principal = procedure_type.principal();
    assert the_principal.get_kind() == type_kinds.procedure_kind;

    return ((parametrized_type) the_principal).get_parameters().first();
  }

  public static base_execution_context get_context(execution_context the_context) {
    return (base_execution_context) the_context;
  }

  private static entity_wrapper try_execute_native(procedure_declaration the_procedure,
      @Nullable value_wrapper this_argument, readonly_list<entity_wrapper> arguments,
      execution_context the_context) {
    principal_type declared_in_type = the_procedure.declared_in_type();
    if (declared_in_type instanceof parametrized_type &&
        ((parametrized_type) declared_in_type).get_master() ==
            common_library.get_instance().list_type()) {
      if (the_procedure.original_name() == common_library.get_name) {
        // TODO: implement a type_bound check here instead of instanceof
        // assert this_argument instanceof list_value;
        readonly_list<value_wrapper> unwrapped_list =
            (readonly_list<value_wrapper>) this_argument.unwrap();
        assert arguments.size() == 1;
        Integer index = (Integer) ((value_wrapper) arguments.first()).unwrap();
        value_wrapper element = unwrapped_list.get(index);
        type ref_type = common_library.get_instance().get_reference(
            flavor.readonly_flavor, to_type(element.type_bound()));
        return new constant_reference(element, ref_type);
      }
    }
    return null;
  }

  public static entity_wrapper execute_procedure(procedure_declaration the_procedure,
      @Nullable value_wrapper this_argument, readonly_list<entity_wrapper> arguments,
      execution_context the_context) {

    entity_wrapper result = try_execute_native(the_procedure, this_argument,
        arguments, the_context);

    if (result != null) {
      return result;
    }

    base_execution_context new_context = action_utilities.get_context(the_context).
        make_child(the_procedure.original_name());

    if (the_procedure.get_category() == procedure_category.STATIC) {
      assert this_argument == null;
    } else {
      assert this_argument != null;
      assert the_procedure.get_this_declaration() != null;
      new_context.put_var(the_procedure.get_this_declaration(), this_argument);
    }

    assert the_procedure.get_argument_types().size() == arguments.size();
    for (int i = 0; i < arguments.size(); ++i) {
      variable_declaration var_decl = the_procedure.get_parameter_variables().get(i);
      entity_wrapper wrapped_argument = arguments.get(i);
      assert wrapped_argument instanceof value_wrapper;
      new_context.put_var(var_decl, (value_wrapper) wrapped_argument);
    }

    action body_action = the_procedure.get_body_action();
    if (body_action == null)  {
      utilities.panic("No body action for " + the_procedure.short_name());
    }
    assert body_action != null;
    result = body_action.execute(new_context);

    // TODO: uniformly hanlde jump_wrappers; do stack trace.
    if (result instanceof panic_value) {
      utilities.panic(((panic_value) result).message);
    }

    if (the_procedure.get_category() == procedure_category.CONSTRUCTOR) {
      result = this_argument;
    } else {
      if (result instanceof returned_value) {
        result = ((returned_value) result).result;
      }
    }
    return result;
  }

  public static boolean is_result(action the_action, abstract_value the_result) {
    // TODO: For now assume this -- but it doesn't *have* to be true.
    assert the_result instanceof type;
    return the_action.result().type_bound().is_subtype_of(the_result.type_bound());
  }

  // Make it easy to detect where type_id needs to be cast to type.
  public static type to_type(type_id the_type_id) {
    return (type) the_type_id;
  }

  public static boolean is_of(entity_wrapper the_entity, type the_type) {
    return to_type(the_entity.type_bound()).is_subtype_of(the_type);
  }

  public static error_signal cant_promote(abstract_value from, type target,
      analysis_context the_context, origin pos) {
    return new error_signal(new base_string("Can't promote " + the_context.print_value(from) +
        " to " + the_context.print_value(target)), pos);
  }

  public static flavor_profile get_profile(supertype_declaration the_supertype) {
    type the_type = the_supertype.get_supertype();
    type_utilities.prepare(the_type, declaration_pass.TYPES_AND_PROMOTIONS);
    flavor_profile the_profile = the_type.principal().get_flavor_profile();
    if (the_type.get_flavor() != flavor.nameonly_flavor) {
      the_profile = flavor_profiles.combine(the_profile, the_type.get_flavor().get_profile());
    }
    return the_profile;
  }

  public static boolean supports_constructors(principal_type the_type) {
    kind the_kind = the_type.get_kind();
    return the_kind == class_kind || the_kind == datatype_kind || the_kind == enum_kind;
  }

  public static void process_super_flavors(principal_type the_subtype,
      @Nullable type_flavor subtype_flavor, type the_supertype, origin the_origin,
      analysis_context the_context) {

    immutable_list<type_flavor> supported_flavors =
        the_subtype.get_flavor_profile().supported_flavors();

    if (subtype_flavor == null && the_supertype instanceof principal_type) {
      for (int i = 0; i < supported_flavors.size(); ++i) {
        type_flavor the_flavor = supported_flavors.get(i);
        if (type_utilities.get_flavor_profile(the_supertype.principal()).supports(the_flavor)) {
          add_supertype_and_promotion(the_subtype.get_flavored(the_flavor),
              the_supertype.get_flavored(the_flavor), the_context, the_origin);
        }
      }
    } else {
      if (subtype_flavor == null) {
        subtype_flavor = the_supertype.get_flavor();
      }
      add_supertype_and_promotion(the_subtype.get_flavored(subtype_flavor),
          the_supertype, the_context, the_origin);
    }
  }

  public static void add_supertype_and_promotion(type from, type to, analysis_context context,
      origin the_origin) {
    assert !(from instanceof principal_type);
    assert !(to instanceof principal_type);

    context.add(from, special_name.SUPERTYPE, to.to_action(the_origin));
    action_utilities.add_promotion(context, from, to, the_origin);
  }
}
