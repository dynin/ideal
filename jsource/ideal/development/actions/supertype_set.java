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
import ideal.development.flags.*;
import ideal.development.notifications.*;

class supertype_set extends debuggable implements readonly_displayable {

  private static dictionary<type, supertype_set> supertype_sets =
      new hash_dictionary<type, supertype_set>(type_equivalence.instance);

  private final procedure0<Void> clear_callback;
  private set<type> members;

  private supertype_set() {
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

  public immutable_list<type> type_list() {
    return members.elements();
  }

  public boolean contains(type element) {
    return members.contains(element);
  }

  public static supertype_set make(type the_type, action_table actions) {
    assert the_type != null;

    supertype_set result;
    if (debug.CACHE_ACTIONS) {
      result = supertype_sets.get(the_type);
      if (result == null) {
        result = new supertype_set();
        supertype_sets.put(the_type, result);
      }
      if (result.members == null) {
        result.populate(the_type, actions);
      }
    } else {
      result = new supertype_set();
      result.populate(the_type, actions);
    }

    return result;
  }

  private void populate(type the_type, action_table actions) {
    set<type> result = new hash_set<type>();
    list<type> considered = new base_list<type>();

    considered.append(the_type);
    result.add(the_type);

    for (int i = 0; i < considered.size(); ++i) {
      type considered_type = considered.get(i);
      readonly_list<action> new_actions = actions.lookup(considered_type, special_name.SUPERTYPE);
      if (debug.CACHE_ACTIONS) {
        actions.observe(considered_type, special_name.SUPERTYPE, clear_callback);
      }
      for (int j = 0; j < new_actions.size(); ++j) {
        action new_action = new_actions.get(j);
        type new_action_type = new_action.result().type_bound();
        if (!result.contains(new_action_type)) {
          considered.append(new_action_type);
          result.add(new_action_type);
        }
      }
    }

    this.members = result;
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
