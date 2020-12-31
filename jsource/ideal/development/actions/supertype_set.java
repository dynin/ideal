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
import ideal.development.notifications.*;

class supertype_set extends debuggable implements readonly_displayable {

  public final set<type> members;

  private supertype_set(set<type> members) {
    this.members = members;
  }

  public set<type> types() {
    return members;
  }

  public boolean contains(type element) {
    return members.contains(element);
  }

  public static supertype_set make(type from, action_table actions) {
    assert from != null;
    set<type> result = new hash_set<type>();
    list<type> considered = new base_list<type>();

    considered.append(from);
    result.add(from);

    for (int i = 0; i < considered.size(); ++i) {
      type considered_type = considered.get(i);
      readonly_list<action> new_actions = actions.lookup(considered_type, special_name.SUPERTYPE);
      for (int j = 0; j < new_actions.size(); ++j) {
        action new_action = new_actions.get(j);
        type new_action_type = new_action.result().type_bound();
        if (!result.contains(new_action_type)) {
          considered.append(new_action_type);
          result.add(new_action_type);
        }
      }
    }

    return new supertype_set(result);
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
    // TODO: use join.
    immutable_list<type> the_members = members.elements();
    for (int i = 0; i < the_members.size(); ++i) {
      result.append(first ? "{\n" : ",\n");
      first = false;
      result.append(utilities.s(the_members.get(i).to_string()));
    }
    result.append("\n}\n");
    return new base_string(result.toString());
  }
}
