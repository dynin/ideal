/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;

public class base_execution_context extends debuggable
    implements execution_context, variable_context {

  private final identifier name;
  private final dictionary<variable_id, value_wrapper> var_bindings;
  private final variable_context static_context;

  public base_execution_context(identifier name) {
    this.name = name;
    var_bindings = new hash_dictionary<variable_id, value_wrapper>();
    static_context = this;
  }

  private base_execution_context(identifier name, variable_context static_context) {
    this.name = name;
    var_bindings = new hash_dictionary<variable_id, value_wrapper>();
    this.static_context = static_context;
  }

  public void put_var(variable_id key, value_wrapper value) {
    var_bindings.put(key, value);
  }

  public value_wrapper get_var(variable_id key) {
    return var_bindings.get(key);
  }

  public variable_context static_context() {
    return static_context;
  }

  public base_execution_context make_child(identifier name) {
    return new base_execution_context(name, static_context);
  }

  public string to_string() {
    return new base_string("context:", name.to_string());
  }
}
