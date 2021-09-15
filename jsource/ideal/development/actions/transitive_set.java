/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
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

class transitive_set extends debuggable implements readonly_displayable {

  public final dictionary<type, type_and_action> members;

  private transitive_set(dictionary<type, type_and_action> members) {
    this.members = members;
  }

  public readonly_set<type> types() {
    return members.keys();
  }

  public boolean contains(type element) {
    return members.contains_key(element);
  }

  public static transitive_set make(type from, action_table actions) {
    assert from != null;
    dictionary<type, type_and_action> result = new hash_dictionary<type, type_and_action>();
    list<type> considered = new base_list<type>();

    considered.append(from);
    action from_action;
    if (from instanceof principal_type) {
      from_action = from.to_action(origin_utilities.no_origin);
    } else {
      from_action = new promotion_action(from, false);
    }
    result.put(from, new type_and_action(from, from_action));

    for (int i = 0; i < considered.size(); ++i) {
      type considered_type = considered.get(i);
      action considered_action = result.get(considered_type).get_action();
      assert considered_action != null;
      readonly_list<action> new_actions = actions.lookup(considered_type, special_name.PROMOTION);
      for (int j = 0; j < new_actions.size(); ++j) {
        action new_action = new_actions.get(j);
        type new_action_type = new_action.result().type_bound();
        if (!result.contains_key(new_action_type)) {
          action the_action = action_utilities.combine(considered_action, new_action,
              origin_utilities.no_origin);
          assert the_action.result().type_bound() == new_action_type;
          considered.append(new_action_type);
          result.put(new_action_type, new type_and_action(considered_type, the_action));
        }
      }
    }

    return new transitive_set(result);
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
