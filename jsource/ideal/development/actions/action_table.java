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
import ideal.development.names.*;
import ideal.development.flags.*;

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

    public Integer hash(action_key the_key) {
      return System.identityHashCode(the_key.from) + 37 * System.identityHashCode(the_key.name);
    }
  };

  private dictionary<action_key, list<action>> action_dictionary =
      new hash_dictionary<action_key, list<action>>(new action_key_equivalence());
  private dictionary<type, list<procedure0<Void>>> supertype_callbacks =
      new hash_dictionary<type, list<procedure0<Void>>>(type_equivalence.instance);
  private dictionary<type, list<procedure0<Void>>> promotion_callbacks =
      new hash_dictionary<type, list<procedure0<Void>>>(type_equivalence.instance);

  public readonly_list<action> lookup(type from, action_name name) {
    @Nullable list<action> rows = action_dictionary.get(new action_key(from, name));
    if (rows != null) {
      if (debug.TRACE && name == debug.TRACE_NAME) {
        log.debug("Found " + name + " in " + from);
      }
      return rows;
    } else {
      return none;
    }
  }

  public void add(type the_type, action_name name, action the_action) {
    if (debug.TRACE && name == debug.TRACE_NAME) {
      log.debug("Adding " + name + " in " + the_type);
    }

    @Nullable dictionary<type, list<procedure0<Void>>> callbacks = get_callbacks(name);
    if (callbacks != null) {
      list<procedure0<Void>> callback_list = callbacks.get(the_type);
      if (callback_list != null) {
        for (int i = 0; i < callback_list.size(); ++i) {
          callback_list.get(i).call();
        }
        callbacks.remove(the_type);
      }
    }

    action_key key = new action_key(the_type, name);
    list<action> rows = action_dictionary.get(key);

    if (rows == null) {
      list<action> ops = new base_list<action>();
      ops.append(the_action);
      action_dictionary.put(key, ops);
    } else {
      rows.append(the_action);
    }
  }

  private @Nullable dictionary<type, list<procedure0<Void>>> get_callbacks(action_name name) {
    if (name == special_name.SUPERTYPE) {
      return supertype_callbacks;
    } else if (name == special_name.PROMOTION) {
      return promotion_callbacks;
    } else {
      return null;
    }
  }

  public void observe(type the_type, action_name name, procedure0<Void> callback) {
    @Nullable dictionary<type, list<procedure0<Void>>> callbacks = get_callbacks(name);

    if (callbacks == null) {
      utilities.panic("Cannot observe " + name);
    }

    list<procedure0<Void>> callback_list = callbacks.get(the_type);

    if (callback_list == null) {
      callbacks.put(the_type, new base_list<procedure0<Void>>(callback));
    } else {
      callback_list.append(callback);
    }
  }
}
