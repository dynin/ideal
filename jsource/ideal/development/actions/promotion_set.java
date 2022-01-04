/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.origins.*;
import ideal.development.notifications.*;
import ideal.development.flags.*;

class promotion_set extends debuggable implements readonly_displayable {

  private static dictionary<type, promotion_set> promotion_sets =
      new hash_dictionary<type, promotion_set>(type_equivalence.instance);

  private final procedure0<Void> clear_callback;
  private dictionary<type, type_and_action> members;

  private promotion_set() {
    clear_callback = new procedure0<Void>() {
      @Override
      public Void call() {
        clear();
        return null;
      }
    };
  }

  private void clear() {
    this.members = null;
  }

  public action get_action(type the_type) {
    type_and_action result = members.get(the_type);
    if (result != null) {
      return result.get_action();
    } else {
      return null;
    }
  }

  public readonly_list<dictionary.entry<type, type_and_action>> entry_list() {
    return members.elements();
  }

  public boolean contains(type element) {
    return members.contains_key(element);
  }

  public static promotion_set make(type the_type, action_table actions) {
    assert the_type != null;

    promotion_set result;
    if (debug.CACHE_ACTIONS) {
      result = promotion_sets.get(the_type);
      if (result == null) {
        result = new promotion_set();
        promotion_sets.put(the_type, result);
      }
      if (result.members == null) {
        result.populate(the_type, actions);
      }
    } else {
      result = new promotion_set();
      result.populate(the_type, actions);
    }

    return result;
  }

  private void populate(type from, action_table actions) {
    dictionary<type, type_and_action> result = new hash_dictionary<type, type_and_action>();
    list<type> considered = new base_list<type>();

    considered.append(from);
    action from_action;
    if (from instanceof principal_type) {
      from_action = from.to_action(origin_utilities.no_origin);
    } else {
      from_action = new stub_action(from);
    }
    result.put(from, new type_and_action(from, from_action));

    for (int i = 0; i < considered.size(); ++i) {
      type considered_type = considered.get(i);
      action considered_action = result.get(considered_type).get_action();
      assert considered_action != null;
      readonly_list<action> new_actions = actions.lookup(considered_type, special_name.PROMOTION);
      if (debug.CACHE_ACTIONS) {
        actions.observe(considered_type, special_name.PROMOTION, clear_callback);
      }
      for (int j = 0; j < new_actions.size(); ++j) {
        action new_action = new_actions.get(j);
        type new_action_type = new_action.result().type_bound();
        if (!result.contains_key(new_action_type)) {
          action the_action = new_action.combine(considered_action, origin_utilities.no_origin);
          assert the_action.result().type_bound() == new_action_type;
          considered.append(new_action_type);
          result.put(new_action_type, new type_and_action(considered_type, the_action));
        }
      }
    }

    members = result;
  }

  @Override
  public string display() {
    return to_string();
  }

  @Override
  public string to_string() {
    if (members.is_empty()) {
      return new base_string("{}");
    }

    StringBuilder result = new StringBuilder();
    boolean first = true;
    immutable_list<type> key_list = members.keys().elements();
    for (int i = 0; i < key_list.size(); ++i) {
      result.append(first ? "{\n" : ",\n");
      first = false;
      result.append(utilities.s(key_list.get(i).to_string()));
    }
    result.append("\n}\n");
    return new base_string(result.toString());
  }
}
