/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
import static ideal.development.flavors.flavor.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.origins.*;
import ideal.development.declarations.*;
import ideal.development.flags.*;

public class base_semantics implements language_settings {

  private dictionary<kind, type_policy> policies = new hash_dictionary<kind, type_policy>();

  public readonly_list<action> resolve(action_table actions, type from, action_name name,
      origin pos) {

    assert name != special_name.PROMOTION;

    promotion_set promotions = promotion_set.make(from, actions);

    readonly_list<dictionary.entry<type, type_and_action>> members = promotions.entry_list();
    list<type_and_action> candidates = new base_list<type_and_action>();

    for (int i = 0; i < members.size(); ++i) {
      dictionary.entry<type, type_and_action> entry = members.get(i);
      action implicit_action = entry.value().get_action();
      type implicit_from = entry.key();
      readonly_list<action> results = actions.lookup(implicit_from, name);
      for (int j = 0; j < results.size(); ++j) {
        action result = results.get(j).combine(implicit_action, pos);

        if (result instanceof error_signal) {
          return new base_list<action>(result);
        }

        candidates.append(new type_and_action(implicit_from, result));
      }
    }

    if (candidates.is_empty()) {
      if (debug.NOT_FOUND && name == debug.TRACE_NAME) {
        log.debug("Not found " + name + " for " + from + ": " + promotions);
      }
      return new empty<action>();
    }

    readonly_list<action> the_best = select_best(actions, candidates);
    assert the_best.is_not_empty();

    return the_best;
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
    promotion_set s1 = promotion_set.make(r1.get_type(), actions);
    promotion_set s2 = promotion_set.make(r2.get_type(), actions);

    return s1.contains(r2.get_type()) && !s2.contains(r1.get_type());
  }

  boolean is_subtype_of(action_table actions, abstract_value the_value, type the_type) {
    return find_supertype(actions, the_value, the_type) != null;
  }

  @Nullable
  public type find_supertype(action_table actions, abstract_value the_value, type target) {
    type subtype = the_value.type_bound();

    if (subtype == target) {
      return subtype;
    }

    // Unreachable type can pretend to be anything.
    if (subtype == common_types.unreachable_type()) {
      return target;
    }

    // TODO: switch to using type_identifiers.
    if (target == common_types.any_type()) {
      return subtype;
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

    if (type_utilities.is_union(target)) {
      immutable_list<abstract_value> parameters = type_utilities.get_union_parameters(target);
      for (int i = 0; i < parameters.size(); ++i) {
        type candidate_target = parameters.get(i).type_bound();
        type result = find_supertype(actions, the_value, candidate_target);
        if (result != null) {
          return result;
        }
      }
      return null;
    }

    if (subtype.principal() instanceof parametrized_type &&
        target.principal() instanceof parametrized_type &&
        maybe_master(subtype) == maybe_master(target)) {
      if (check_variance(actions, subtype, target)) {
        return target;
      }
    }

    supertype_set supertypes = supertype_set.make(subtype, actions);
    return supertypes.contains(target) ? target : null;
  }

  @Nullable
  public readonly_set<type> find_matching_supertype(action_table actions, type subtype,
      predicate<type> the_predicate) {
    if (the_predicate.call(subtype)) {
      return new singleton_collection(subtype);
    }

    if (type_utilities.is_union(subtype)) {
      immutable_list<abstract_value> parameters = type_utilities.get_union_parameters(subtype);
      for (int i = 0; i < parameters.size(); ++i) {
        readonly_set<type> supertypes =
            find_matching_supertype(actions, parameters.get(i).type_bound(), the_predicate);
        if (supertypes.is_empty()) {
          return supertypes;
        }
      }
      return new singleton_collection(subtype);
    }

    supertype_set supertypes = supertype_set.make(subtype, actions);

    // TODO: use filter.
    immutable_list<type> supertypes_list = supertypes.type_list();
    set<type> candidates = new hash_set<type>();
    for (int i = 0; i < supertypes_list.size(); ++i) {
      type candidate = supertypes_list.get(i);
      if (the_predicate.call(candidate)) {
        candidates.add(candidate);
      }
    }

    return candidates;
  }

  @Nullable
  public action find_promotion(action_table actions, type subtype, type target) {
    if (subtype == target || target == common_types.any_type()) {
      return new stub_action(subtype);
    }

    @Nullable type supertype = find_supertype(actions, subtype, target);
    if (supertype != null) {
      return new promotion_action(target, origin_utilities.no_origin);
    }

    // Anything can be promoted to the 'void' value.
    if (target == common_types.immutable_void_type()) {
      return new chain_action(new stub_action(subtype),
          new promotion_action(target, origin_utilities.no_origin),
          origin_utilities.no_origin);
    }

    promotion_set promotions = promotion_set.make(subtype, actions);

    @Nullable action result = promotions.get_action(target);
    if (result != null) {
      return result;
    }
    // TODO: use filter().
    readonly_list<dictionary.entry<type, type_and_action>> promotions_list =
        promotions.entry_list();
    list<type_and_action> candidates = new base_list<type_and_action>();
    for (int i = 0; i < promotions_list.size(); ++i) {
      dictionary.entry<type, type_and_action> entry = promotions_list.get(i);
      if (entry.key() == target) {
        candidates.append(entry.value());
      } else if (is_subtype_of(actions, entry.key(), target)) {
        candidates.append(new type_and_action(entry.key(),
            new chain_action(entry.value().get_action(),
                new promotion_action(target, origin_utilities.no_origin),
                origin_utilities.no_origin)));
      }
    }
    readonly_list<action> best = select_best(actions, candidates);
    if (best.size() == 1) {
      return best.first();
    }
    if (best.size() > 1) {
      utilities.panic("Multiple subtypes");
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
      parametrized_type supertype, type_flavor subtype_flavor, type_flavor supertype_flavor) {
    boolean result = check_variance(actions, subtype, supertype, the_flavor);
    utilities.stack("Subtype: " + subtype + " supertype: " + supertype +
        " SUBF: " + subtype_flavor + " SUPF: " + supertype_flavor + " GOT: " + result);
    return result;
  }
  */

  private @Nullable type_flavor get_super_flavor(type_flavor subtype_flavor,
      type_flavor supertype_flavor) {

    if (subtype_flavor == nameonly_flavor || supertype_flavor == nameonly_flavor) {
      return null;
    }

    if (subtype_flavor == supertype_flavor) {
      return subtype_flavor;
    }

    // TODO: real superflavor check.
    if (subtype_flavor == immutable_flavor && supertype_flavor == readonly_flavor) {
      return readonly_flavor;
    }

    return null;
  }

  private boolean check_variance(action_table actions, type subtype, type supertype) {
    @Nullable type_flavor super_flavor = get_super_flavor(subtype.get_flavor(),
        supertype.get_flavor());
    if (super_flavor == null) {
      return false;
    }

    parametrized_type subtype_principal = (parametrized_type) subtype.principal();
    parametrized_type supertype_principal = (parametrized_type) supertype.principal();
    assert subtype_principal.get_master() == supertype_principal.get_master();
    master_type the_master = subtype_principal.get_master();

    // TODO: implement real variance check...
    immutable_list<abstract_value> subtype_parameters =
        subtype_principal.get_parameters().the_list;
    immutable_list<abstract_value> supertype_parameters =
        supertype_principal.get_parameters().the_list;
    int size = subtype_parameters.size();
    // TODO: handle extra procedure arguments...
    if (supertype_parameters.size() != size) {
      return false;
    }

    for (int i = 0; i < size; ++i) {
      if (!check_one_variance(actions, subtype_parameters.get(i), supertype_parameters.get(i),
          the_master.get_parametrizable().get_variance(i), super_flavor)) {
        return false;
      }
    }

    return true;
  }

  private boolean check_one_variance(action_table actions, abstract_value subtype_value,
      abstract_value supertype_value, variance_modifier variance, type_flavor the_flavor) {
    // TODO: handle non-types.
    assert subtype_value instanceof type;
    assert supertype_value instanceof type;
    if (subtype_value == supertype_value) {
      return true;
    }
    type subtype_type = (type) subtype_value;
    type supertype_type = (type) supertype_value;

    if (variance == variance_modifier.invariant_modifier) {
      readonly_list<action> aliases = actions.lookup(subtype_type.principal(),
          special_name.TYPE_ALIAS);
      if (aliases.size() == 1 && aliases.first().result() == supertype_type) {
        //log.debug("ALIAS " +  subtype_value + " SUPER: " + supertype_value);
        return true;
      }
    } else if (variance == variance_modifier.covariant_modifier) {
      if (is_subtype_of(actions, subtype_value, supertype_type)) {
        return true;
      }
    } else if (variance == variance_modifier.combivariant_modifier) {
      //log.debug("SUB: " +  subtype_value + " SUPER: " + supertype_value + " FL: " + the_flavor);
      if (the_flavor == readonly_flavor ||
          the_flavor == immutable_flavor ||
          the_flavor == deeply_immutable_flavor) {
        if (is_subtype_of(actions, subtype_value, supertype_type)) {
          return true;
        }
      }
    }

    return false;
  }

  public void add_kind(kind the_kind, type_policy the_type_policy) {
    assert !policies.contains_key(the_kind);
    policies.put(the_kind, the_type_policy);
  }

  public readonly_set<kind> all_kinds() {
    return policies.keys();
  }

  public type_policy get_policy(kind the_kind) {
    type_policy the_type_policy = policies.get(the_kind);
    assert the_type_policy != null;
    return the_type_policy;
  }

  public void declare_type(principal_type new_type, declaration_pass pass, action_context context) {
    get_policy(new_type.get_kind()).declare_type(new_type, pass, context);
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
