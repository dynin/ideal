/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import static ideal.development.kinds.type_kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.declarations.*;

import ideal.development.values.singleton_value;

public class base_semantics implements semantics {

  public static final boolean DEBUG_RESOLVE = false;

  public static final boolean DEBUG_SUBTYPE = false;

  private static final boolean DEBUG_PROMOTION = false;

  private @Nullable value_printer the_value_printer;

  @Override
  public common_library library() {
    return common_library.get_instance();
  }

  public readonly_list<action> resolve(action_table actions, type from, action_name name,
      @Nullable action_target target, origin pos) {

    assert name != special_name.PROMOTION;

    transitive_set promotions = transitive_set.make(from, actions);

    readonly_list<dictionary.entry<type, type_and_action>> members = promotions.members.elements();
    list<type_and_action> candidates = new base_list<type_and_action>();

    for (int i = 0; i < members.size(); ++i) {
      dictionary.entry<type, type_and_action> entry = members.get(i);
      action implicit_action = entry.value().get_action();
      type implicit_from = entry.key();
      readonly_list<action> results = actions.lookup(implicit_from, name);
      for (int j = 0; j < results.size(); ++j) {
        action result = results.get(j).bind_from(implicit_action, pos);

        if (result instanceof error_signal) {
          return new base_list<action>(result);
        }

        candidates.append(new type_and_action(implicit_from, result));
      }
    }

    if (candidates.is_empty()) {
      if (DEBUG.not_found && name == DEBUG.trace_name) {
        log.debug("Not found " + name + " for " + from + ": " + promotions);
      }
      return new empty<action>();
    }

    readonly_list<type_and_action> filtered;
    if (target != null) {
      filtered = filter(candidates, target);
      if (filtered.is_empty()) {
        filtered = candidates;
      }
    } else {
      filtered = candidates;
    }

    readonly_list<action> the_best = select_best(actions, filtered);
    assert the_best.is_not_empty();

    if (the_best.size() == 1) {
      action the_action = the_best.first();
      abstract_value action_result = the_action.result();
      if (target != null &&
          !target.matches(action_result) &&
          !(action_result instanceof error_signal)) {
        @Nullable action promoted = find_promotion(actions, action_result, target);
        if (promoted != null) {
          return new base_list<action>(promoted.bind_from(the_action, pos));
        }
      }
    }

    return the_best;
  }

  private readonly_list<type_and_action> filter(readonly_list<type_and_action> candidates,
      action_target target) {
    list<type_and_action> filtered = new base_list<type_and_action>();
    for (int i = 0; i < candidates.size(); ++i) {
      if (target.matches(candidates.get(i).result())) {
        filtered.append(candidates.get(i));
      }
    }
    return filtered;
  }

  private readonly_list<action> select_best(action_table actions,
      readonly_list<type_and_action> candidates) {
    if (candidates.is_empty()) {
      return new empty<action>();
    }

    if (candidates.size() == 1) {
      return new base_list<action>(candidates.first().get_action());
    }

    for (int i = 0; i < candidates.size(); ++i) {
      type_and_action candidate = candidates.get(i);
      action the_action = candidate.get_action();

      // Error signals are always selected as "the best" candidate
      if (the_action instanceof error_signal) {
        return new base_list<action>(the_action);
      }

      // TODO: cleanup this code.
      boolean best = true;
      for (int j = 0; j < candidates.size(); ++j) {
        if (i == j) {
          continue;
        }
        type_and_action challenger = candidates.get(j);
        if (!is_better(actions, candidate, challenger)) {
          best = false;
          break;
        }
      }
      if (best) {
        return new base_list<action>(the_action);
      }
    }

    // TODO: use list.map()
    list<action> results = new base_list<action>();
    for (int i = 0; i < candidates.size(); ++i) {
      results.append(candidates.get(i).get_action());
    }

    return results;
  }

  private boolean is_better(action_table actions, type_and_action r1, type_and_action r2) {
    transitive_set s1 = transitive_set.make(r1.get_type(), actions);
    transitive_set s2 = transitive_set.make(r2.get_type(), actions);

    return s1.contains(r2.get_type()) && !s2.contains(r1.get_type());
  }

  boolean is_subtype_of(action_table actions, abstract_value the_value, type the_type) {
    return find_supertype(actions, the_value, new specific_type_target(the_type)) != null;
  }

  @Nullable
  public type find_supertype(action_table actions, abstract_value the_value,
      action_target target) {

    type subtype = the_value.type_bound();
    @Nullable type specific_type = null;
    if (target instanceof specific_type_target) {
      specific_type = ((specific_type_target) target).target_type;
    }

    if (target.matches(the_value)) {
      return specific_type != null ? specific_type : subtype;
    }

    if (specific_type != null) {
      // Unreachable type can pretend to be anything.
      if (subtype == core_types.unreachable_type()) {
        return specific_type;
      }

      // TODO: switch to using type_identifiers.
      if (specific_type == core_types.any_type()) {
        return subtype;
      }

      if (type_utilities.is_union(specific_type)) {
        immutable_list<abstract_value> parameters =
            type_utilities.get_union_parameters(specific_type);
        for (int i = 0; i < parameters.size(); ++i) {
          type candidate_target = parameters.get(i).type_bound();
          type result = find_supertype(actions, the_value,
              new specific_type_target(candidate_target));
          if (result != null) {
            return result;
          }
        }
        return null;
      }

      if (subtype.get_flavor() == specific_type.get_flavor() &&
          subtype.get_flavor() != flavors.nameonly_flavor &&
          maybe_master(subtype) == maybe_master(specific_type)) {
        if (check_variance(actions, (parametrized_type) subtype.principal(),
            (parametrized_type) specific_type.principal(), subtype.get_flavor())) {
          return specific_type;
        }
      }
    }

    if (type_utilities.is_union(subtype)) {
      immutable_list<abstract_value> parameters = type_utilities.get_union_parameters(subtype);
      for (int i = 0; i < parameters.size(); ++i) {
        if (find_supertype(actions, parameters.get(i), target) == null) {
          return null;
        }
      }
      return subtype;
    }

    supertype_set supertypes = supertype_set.make(subtype, actions);

    if (specific_type != null) {
      return supertypes.contains(specific_type) ? specific_type : null;
    } else {
      // TODO: use filter.
      immutable_list<type> supertypes_list =  supertypes.members.elements();
      list<type> candidates = new base_list<type>();
      for (int i = 0; i < supertypes_list.size(); ++i) {
        type candidate = supertypes_list.get(i);
        if (target.matches(candidate)) {
          candidates.append(candidate);
        }
      }
      if (candidates.size() > 1) {
        // TODO: unexpected--can just return null here...
        utilities.panic("Too many supertypes");
      } else if (candidates.size() == 1) {
        return candidates.first();
      }
    }

    return null;
  }

  @Nullable
  public action find_promotion(action_table actions, abstract_value the_value,
      action_target target) {

    type subtype = the_value.type_bound();
    @Nullable type specific_type = null;
    if (target instanceof specific_type_target) {
      specific_type = ((specific_type_target) target).target_type;
    }

    if (target.matches(the_value)) {
      return new promotion_action(specific_type != null ? specific_type : subtype);
    }

    type the_supertype = find_supertype(actions, the_value, target);
    if (the_supertype != null) {
      return new promotion_action(the_supertype);
    }

    // Anything can be promoted to the 'void' value.
    if (specific_type == library().immutable_void_type()) {
      return library().void_instance().to_action(action_utilities.no_origin);
    }

    transitive_set promotions = transitive_set.make(subtype, actions);

    if (specific_type != null) {
      @Nullable type_and_action result = promotions.members.get(specific_type);
      if (result != null) {
        return result.get_action();
      }
      // TODO: use filter().
      readonly_list<dictionary.entry<type, type_and_action>> promotions_list =
          promotions.members.elements();
      list<type_and_action> candidates = new base_list<type_and_action>();
      for (int i = 0; i < promotions_list.size(); ++i) {
        dictionary.entry<type, type_and_action> entry = promotions_list.get(i);
        if (is_subtype_of(actions, entry.key(), specific_type)) {
          candidates.append(entry.value());
        }
      }
      readonly_list<action> best = select_best(actions, candidates);
      if (best.size() == 1) {
        return best.first();
      }
      if (best.size() > 1) {
        utilities.panic("Multiple subtypes");
      }
    } else {
      // TODO: use filter().
      readonly_list<dictionary.entry<type, type_and_action>> promotions_list =
          promotions.members.elements();
      list<type_and_action> candidates = new base_list<type_and_action>();
      for (int i = 0; i < promotions_list.size(); ++i) {
        dictionary.entry<type, type_and_action> entry = promotions_list.get(i);
        if (target.matches(entry.key())) {
          candidates.append(entry.value());
        }
      }
      readonly_list<action> best = select_best(actions, candidates);

      if (best.size() == 1) {
        return best.first();
      }
    }

    return null;
  }

  // Is this is a possibly flavored parametrized type, return the corresponding master_type;
  // otherwise just return the type itself.
  private type maybe_master(type the_type) {
    principal_type the_principal = the_type.principal();
    if (the_principal instanceof parametrized_type) {
      return ((parametrized_type) the_principal).get_master();
    } else {
      return the_type;
    }
  }

  /*
  private boolean xx_check_variance(action_table actions, parametrized_type subtype,
      parametrized_type supertype, type_flavor the_flavor) {
    boolean result = check_variance(actions, subtype, supertype, the_flavor);
    log.debug("Subtype: " + subtype + " supertype: " + supertype +
        " FL: " + the_flavor + " GOT: " + result);
    return result;
  }
  */

  private boolean check_variance(action_table actions, parametrized_type subtype,
      parametrized_type supertype, type_flavor the_flavor) {
    assert subtype.get_master() == supertype.get_master();
    // TODO: implement real variance check...
    immutable_list<abstract_value> subtype_parameters =
        subtype.get_parameters().internal_access();
    immutable_list<abstract_value> supertype_parameters =
        supertype.get_parameters().internal_access();
    int size = subtype_parameters.size();
    // TODO: handle extra procedure arguments...
    if (supertype_parameters.size() != size) {
      return false;
    }
    for (int i = 0; i < size; ++i) {
      if (!check_equivalence(actions, subtype_parameters.get(i), supertype_parameters.get(i))) {
        return false;
      }
    }
    return true;
  }

  private boolean check_equivalence(action_table actions, abstract_value subtype_value,
      abstract_value supertype_value) {
    // TODO: handle non-types.
    assert subtype_value instanceof type;
    assert supertype_value instanceof type;
    if (subtype_value == supertype_value) {
      return true;
    }
    type subtype_type = (type) subtype_value;
    type supertype_type = (type) supertype_value;

    readonly_list<action> aliases = actions.lookup(subtype_type.principal(),
        special_name.TYPE_ALIAS);
    if (aliases.size() == 1 && aliases.first().result() == supertype_type) {
      //log.debug("ALIAS " +  subtype_value + " SUPER: " + supertype_value);
      return true;
    }
    return false;
  }

  public void declare_type(principal_type new_type, declaration_pass pass,
      analysis_context context) {

    kind the_kind = new_type.get_kind();
    if (the_kind.is_namespace()) {
      assert !(new_type instanceof parametrized_type);
      if (new_type.has_flavor_profile()) {
        assert new_type.get_flavor_profile() == flavor_profiles.nameonly_profile;
      } else {
        ((base_principal_type) new_type).set_flavor_profile(flavor_profiles.nameonly_profile);
      }
      return;
    }

    if (the_kind == union_kind) {
      return;
    }

    readonly_list<principal_type> adjacent = context.type_graph().adjacent(new_type).elements();
    for (int k = 0; k < adjacent.size(); ++k) {
      type_utilities.prepare(adjacent.get(k), pass);
    }

    type_declaration the_type_declaration = (type_declaration) new_type.get_declaration();

    if (the_type_declaration == null) {
      assert pass == declaration_pass.TYPES_AND_PROMOTIONS;
      assert new_type instanceof parametrized_type;
      // or utilities.panic("No declaration for " + new_type);
      parametrized_type ptype = (parametrized_type) new_type;
      principal_type declared_type =
          ((type_declaration) ptype.get_master().get_declaration()).get_declared_type();
      assert declared_type != new_type : "Got type " + new_type;
      assert declared_type instanceof parametrized_type;
      the_type_declaration = specialize_declaration(ptype,
          (parametrized_type) declared_type, pass, context);
      ptype.set_declaration(the_type_declaration);
    }

    /*
      if (the_type_declaration == null) {
        utilities.panic("No declaration for " + new_type);
      }

      principal_type declared_type = the_type_declaration.get_declared_type();

      if (declared_type != new_type) {
        if (declared_type instanceof parametrized_type &&
            new_type instanceof parametrized_type) {
          if (false) {
            assert pass == declaration_pass.TYPES_AND_PROMOTIONS;
            the_type_declaration = specialize_declaration((parametrized_type) new_type,
                (parametrized_type) declared_type, pass, context);
            ((parametrized_type) new_type).set_declaration(the_type_declaration);
          } else {
            specialize_declaration((parametrized_type) new_type,
                (parametrized_type) declared_type, pass, context).process_declaration(pass);
          }
        } else {
          // This happens when declare_type() is called for the master type
          // of a parametrized type, such as collection (with no parameters).
          // TODO: this should never happen.
          assert declared_type instanceof parametrized_type &&
                 new_type instanceof master_type;
          return;
        }
      }
    }
    */

    the_type_declaration.process_declaration(pass);

    flavor_profile profile;
    if (new_type.has_flavor_profile()) {
      profile = new_type.get_flavor_profile();
    } else {
      utilities.panic("Flavor profile not set in " + new_type);
      return;
    }

    if (profile == flavor_profiles.nameonly_profile) {
      // Most of the time, namespace case is handled in the condition at
      // the top of the procedure.
      assert the_kind == type_alias_kind;
      return;
    }

    origin pos = the_type_declaration;

    if (pass == declaration_pass.TYPES_AND_PROMOTIONS) {
      if (the_kind == procedure_kind &&
          new_type instanceof parametrized_type &&
          new_type.short_name() == common_library.function_name) {
        // TODO: this should be done in type_declaration_analyzer.
        type procedure_type = library().procedure_type().bind_parameters(
          ((parametrized_type) new_type).get_parameters()).get_flavored(
            flavors.immutable_flavor);
        process_super_flavors(new_type, null, procedure_type, pos, context);
      }

      immutable_list<type_flavor> supported_flavors = profile.supported_flavors();
      for (int i = 0; i < supported_flavors.size(); ++i) {
        type_flavor the_flavor = supported_flavors.get(i);
        immutable_list<type_flavor> superflavors = the_flavor.get_superflavors();
        for (int j = 0; j < superflavors.size(); ++j) {
          type_flavor superflavor = superflavors.get(j);
          if (profile.supports(superflavor)) {
            add_supertype_and_promotion(new_type.get_flavored(the_flavor),
                new_type.get_flavored(superflavor), context, pos);
          }
        }
      }

      readonly_list<declaration> declarations = the_type_declaration.get_signature();
      for (int j = 0; j < declarations.size(); ++j) {
        declaration the_declaration = declarations.get(j);
        if (the_declaration instanceof supertype_declaration) {
          supertype_declaration the_supertype_declaration =
              (supertype_declaration) the_declaration;
          if (the_supertype_declaration.has_errors()) {
            continue;
          }
          type the_supertype = the_supertype_declaration.get_supertype();
          process_super_flavors(new_type, the_supertype_declaration.subtype_flavor(),
              the_supertype, the_supertype_declaration, context);
        }
      }
    } else {
      // TODO: make this cleaner.
      readonly_list<type> supertypes = action_utilities.get_supertypes(new_type);
      for (int j = 0; j < supertypes.size(); ++j) {
        type supertype = supertypes.get(j);
        type_utilities.prepare(supertype, declaration_pass.METHODS_AND_VARIABLES);
      }

      if (action_utilities.supports_constructors(new_type)) {
        context.add(new_type, special_name.NEW, new allocate_action(new_type, pos));
      } else if (new_type.get_kind() == reference_kind &&
                 new_type instanceof parametrized_type) {
        declare_reference((parametrized_type) new_type, context);
      } else if (the_kind == procedure_kind && new_type instanceof parametrized_type) {
        // TODO: describe how IMPLICIT name works, and why it is needed here
        // (to make runtime_util.default_equivalence work)
        type_flavor default_flavor = new_type.get_flavor_profile().default_flavor();
        type flavored_type = new_type.get_flavored(default_flavor);
        context.add(flavored_type, special_name.IMPLICIT_CALL,
            new promotion_action(flavored_type, pos));
      } else if (the_kind == singleton_kind) {
        context.add(new_type, INSTANCE_NAME, new singleton_value(new_type).to_action(pos));
      }
    }
  }

  private void process_super_flavors(principal_type the_subtype,
      @Nullable type_flavor subtype_flavor, type the_supertype, origin pos,
      analysis_context the_context) {

    immutable_list<type_flavor> supported_flavors =
        the_subtype.get_flavor_profile().supported_flavors();

    if (subtype_flavor == null && the_supertype instanceof principal_type) {
      for (int i = 0; i < supported_flavors.size(); ++i) {
        type_flavor the_flavor = supported_flavors.get(i);
        if (type_utilities.get_flavor_profile(the_supertype.principal()).supports(the_flavor)) {
          add_supertype_and_promotion(the_subtype.get_flavored(the_flavor),
              the_supertype.get_flavored(the_flavor), the_context, pos);
        }
      }
    } else {
      if (subtype_flavor == null) {
        subtype_flavor = the_supertype.get_flavor();
      }
      add_supertype_and_promotion(the_subtype.get_flavored(subtype_flavor),
          the_supertype, the_context, pos);
    }
  }

  private static void add_supertype_and_promotion(type from, type to, analysis_context context,
      origin pos) {
    assert !(from instanceof principal_type);
    assert !(to instanceof principal_type);

    context.add(from, special_name.SUPERTYPE, to.to_action(pos));
    action_utilities.add_promotion(context, from, to, pos);
  }

  private specialization_context make_specialization_context(parametrized_type new_type,
      parametrized_type declared_type) {

    dictionary<master_type, abstract_value> specialization =
        new list_dictionary<master_type, abstract_value>();

    if (new_type.get_kind() != procedure_kind) {
      immutable_list<abstract_value> new_parameters = new_type.get_parameters().internal_access();
      immutable_list<abstract_value> declared_parameters =
          declared_type.get_parameters().internal_access();

      // TODO: signal error?
      if (new_parameters.size() != declared_parameters.size()) {
        utilities.panic("new " + new_type + ", declared " + declared_type);
        assert new_parameters.size() == declared_parameters.size();
      }

      for (int i = 0; i < new_parameters.size(); ++i) {
        if (new_parameters.get(i) != declared_parameters.get(i)) {
          master_type declared_param = (master_type) declared_parameters.get(i);
          assert declared_param.get_kind() == type_alias_kind;
          specialization.put(declared_param, new_parameters.get(i));
        }
      }
    }

    return new base_specialization_context(specialization);
  }

  private specialized_type_declaration specialize_declaration(parametrized_type new_type,
      parametrized_type declared_type, declaration_pass pass,
      analysis_context context) {

    specialization_context spec_context = make_specialization_context(new_type, declared_type);

    return new specialized_type_declaration(new_type, declared_type, spec_context, context);

    //the_type_decl.process_declaration(pass);
  }

  private void declare_reference(parametrized_type new_type, analysis_context context) {
    declaration the_declaration = new_type.get_declaration();
    assert the_declaration != null;

    immutable_list<abstract_value> params = new_type.get_parameters().fixed_size_list();
    // TODO: signal an error
    assert params.size() == 1;
    assert params.first() instanceof type;
    type param = (type) params.first();

    action deref = new dereference_action(param, the_declaration, the_declaration);
    context.add(new_type.get_flavored(flavors.readonly_flavor), special_name.PROMOTION, deref);
  }

  public string print_value(abstract_value the_value) {
    if (the_value_printer == null) {
      the_value_printer = new base_value_printer(library().elements_package());
    }

    return the_value_printer.print_value(the_value);
  }

  @Override
  public access_modifier get_default_type_access(kind the_kind) {
    return access_modifier.public_modifier;
  }

  @Override
  public access_modifier get_default_variable_access(kind the_kind) {
    if (the_kind == block_kind) {
      return access_modifier.local_modifier;
    } else {
      return access_modifier.public_modifier;
    }
  }

  @Override
  public access_modifier get_default_procedure_access(kind the_kind) {
    return access_modifier.public_modifier;
  }
}
