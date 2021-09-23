/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.patterns.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.patterns.*;
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
import ideal.development.jumps.*;
import ideal.development.values.*;

public class action_utilities {

  private action_utilities() { }

  // TODO: this should be a multimethod
  public static action combine(action first, action second, origin the_origin) {
    assert first != null;
    assert second != null;

    if (second instanceof stub_action) {
      return first;
    }

    if (second instanceof chain_action) {
      chain_action the_chain_action = (chain_action) second;
      action new_first = action_utilities.combine(first, the_chain_action.first, the_origin);
      if (new_first != the_chain_action.first || the_chain_action.deeper_origin() != the_origin) {
        return new chain_action(new_first, the_chain_action.second, the_origin);
      } else {
        return the_chain_action;
      }
    }

    if (second instanceof dispatch_action ||
        second instanceof proc_as_ref_action ||
        second instanceof dereference_action) {
      return new chain_action(first, second, the_origin);
    }

    if (second instanceof variable_action) {
      if (second instanceof instance_variable) {
        return new chain_action(first, second, the_origin);
      } else {
        return second;
      }
    }

    if (second instanceof base_value_action) {
      base_value_action value_action = (base_value_action) second;
      if (value_action.the_value instanceof procedure_value) {
        procedure_value the_procedure_value = (procedure_value) value_action.the_value;
        return the_procedure_value.bind_value(first, the_origin);
      } else {
        return value_action;
      }
    }

    if (second instanceof bound_procedure) {
      bound_procedure the_bound_procedure = (bound_procedure) second;
      return new bound_procedure(combine(first, the_bound_procedure.the_procedure_action,
              the_origin), the_bound_procedure.return_value,
              the_bound_procedure.parameters, the_origin);
    }

    if (second instanceof promotion_action) {
      promotion_action the_promotion_action = (promotion_action) second;
      if (first.result().type_bound() == the_promotion_action.the_type) {
        return first;
      }

      if (first instanceof chain_action &&
             ((chain_action) first).second instanceof promotion_action) {
        first = ((chain_action) first).first;
      }

      // TODO: verify that first.result() is a subtype of the_type
      // TODO: collapse chained promotion_actions
      return new chain_action(first, the_promotion_action, the_origin);
    }

    return second.bind_from(first, the_origin);
  }

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

  private static pattern<Character> dot_pattern = new singleton_pattern<Character>('.');

  public static principal_type lookup_type(analysis_context context, string full_name) {
    immutable_list<immutable_list<Character>> type_names = dot_pattern.split(full_name);
    principal_type the_type = core_types.root_type();

    for (int i = 0; i < type_names.size(); ++i) {
      simple_name name = simple_name.make((base_string) type_names.get(i));
      readonly_list<action> types = context.lookup(the_type, name);
      assert types.size() == 1;
      the_type = (principal_type) ((type_action) types.first()).get_type();
    }

    return the_type;
  }

  // TODO: use declaration_utils.get_declared_supertypes()
  public static readonly_list<type> get_supertypes(principal_type the_type) {
    declaration the_declaration = the_type.get_declaration();
    readonly_list<declaration> signature = (the_declaration instanceof type_declaration) ?
        ((type_declaration) the_declaration).get_signature() :
        ((type_announcement) the_declaration).get_type_declaration().get_signature();
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

  public static void add_promotion(analysis_context context, type from, type to,
      boolean is_supertype, origin pos) {
    if (to instanceof principal_type) {
      context.add(from, special_name.PROMOTION, to.to_action(pos));
    } else {
      context.add(from, special_name.PROMOTION, new promotion_action(to, is_supertype, pos));
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
    if (the_type.principal().get_kind() == type_kinds.procedure_kind) {
      type_flavor the_flavor = the_type.get_flavor();
      return the_flavor == flavor.immutable_flavor ||
             the_flavor == flavor.deeply_immutable_flavor;
    }
    return false;
  }

  public static boolean is_valid_procedure_arity(type procedure_type, int arity) {
    assert is_procedure_type(procedure_type);
    // TODO: handle variable number of arguments here!
    return ((parametrized_type) procedure_type.principal()).get_parameters().
        the_list.size() == arity + 1;
  }

  public static abstract_value get_procedure_argument(type procedure_type, int index) {
    assert is_procedure_type(procedure_type);
    return ((parametrized_type) procedure_type.principal()).
        get_parameters().the_list.get(index + 1);
  }

  public static abstract_value get_procedure_return(type procedure_type) {
    principal_type the_principal = procedure_type.principal();
    assert the_principal.get_kind() == type_kinds.procedure_kind;

    return ((parametrized_type) the_principal).get_parameters().the_list.first();
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
    result = body_action.execute(this_argument != null ? this_argument : null_wrapper.instance,
        new_context);

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

  public static @Nullable flavor_profile get_profile(supertype_declaration the_supertype) {
    type the_type = the_supertype.get_supertype();
    type_utilities.prepare(the_type, declaration_pass.FLAVOR_PROFILE);

    if (!the_type.principal().has_flavor_profile()) {
      return null;
    }
    flavor_profile the_profile = the_type.principal().get_flavor_profile();
    if (the_type.get_flavor() != flavor.nameonly_flavor) {
      the_profile = flavor_profiles.combine(the_profile, the_type.get_flavor().get_profile());
    }
    return the_profile;
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

    context.add_supertype(from, to);
    add_promotion(context, from, to, true, the_origin);
  }
}
