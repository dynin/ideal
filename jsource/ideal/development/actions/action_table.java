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
import ideal.development.types.*;

import javax.annotation.Nullable;

public class action_table implements value {

  private static final empty<action> none = new empty<action>();

  private static class action_key extends debuggable implements immutable_data {
    final type from;
    final action_name name;

    public action_key(type from, action_name name) {
      this.from = from;
      this.name = name;
    }

    @Override
    public string to_string() {
      return new base_string(from.to_string(), "/", name.to_string());
    }
  }

  private static class action_key_equivalence implements equivalence_with_hash<action_key> {
    @Override
    public Boolean call(action_key first, action_key second) {
      return first.from == second.from && first.name == second.name;
    }

    public int hash(action_key the_key) {
      return System.identityHashCode(the_key.from) + 37 * System.identityHashCode(the_key.name);
    }
  };

  private dictionary<action_key, list<action>> data =
      new hash_dictionary<action_key, list<action>>(new action_key_equivalence());
  private static boolean debug = false;

  public readonly_list<action> lookup(type from, action_name name) {
    @Nullable list<action> rows = data.get(new action_key(from, name));
    if (rows != null) {
      if (DEBUG.trace && name == DEBUG.trace_name) {
        log.debug("Found " + name + " in " + from);
      }
      return rows;
    } else {
      return none;
    }
  }

  public void add(type from, action_name name, action the_action) {
    if (DEBUG.trace && name == DEBUG.trace_name) {
      log.debug("Adding " + name + " in " + from);
    }

    action_key key = new action_key(from, name);
    list<action> rows = data.get(key);

    if (rows == null) {
      list<action> ops = new base_list<action>();
      ops.append(the_action);
      data.put(key, ops);
    } else {
      rows.append(the_action);
    }
  }
}
