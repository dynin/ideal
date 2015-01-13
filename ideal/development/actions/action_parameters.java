/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.types.*;
public class action_parameters extends debuggable
    implements deeply_immutable_data, stringable {

  private immutable_list<action> params;
  // private immutable_dictionary<variable_declaration, action> named_params;

  public action_parameters() {
    this(new empty<action>());
  }

  public action_parameters(readonly_list<action> params) {
      // readonly_dictionary<variable_declaration, action> named_params) {
    for (int i = 0; i < params.size(); ++i) {
      assert params.get(i) != null;
    }
    this.params = params.frozen_copy();
    // this.named_params = named_params.frozen_copy();
  }

  public immutable_list<action> params() {
    return params;
  }

  public int arity() {
    return params.size();
  }

  public immutable_list<abstract_value> to_value_list() {
    list<abstract_value> value_list = new base_list<abstract_value>();
    for (int i = 0; i < params.size(); ++i) {
      value_list.append(params.get(i).result());
    }
    return value_list.frozen_copy();
  }

  public type_parameters to_type_parameters() {
    return new type_parameters(to_value_list());
  }

  /*
  public immutable_dictionary<variable_declaration, action> named_params() {
    return named_params;
  }
  */

  @Override
  public string to_string() {
    StringBuilder s = new StringBuilder();
    boolean first = true;
    s.append('[');

    if (!params.is_empty()) {
      for (int i = 0; i < params.size(); ++i) {
        if (first) {
          first = false;
        } else {
          s.append(", ");
        }
        s.append(utilities.s(params.get(i).to_string()));
      }
    }

    /*
    if (!named_params.is_empty()) {
      readonly_list<dictionary.entry<variable_declaration, action>> named_list =
          named_params.elements();
      for (int i = 0; i < named_list.size(); ++i) {
        if (first) {
          first = false;
        } else {
          s.append(", ");
        }
        s.append(utilities.s(named_list.get(i).key().get_name().to_string()));
        s.append(": ");
        s.append(utilities.s(named_list.get(i).value().to_string()));
      }
    }
    */

    s.append(']');
    return new base_string(s.toString());
  }

  public string to_value_string() {
    StringBuilder s = new StringBuilder();
    boolean first = true;
    s.append('[');

    if (!params.is_empty()) {
      for (int i = 0; i < params.size(); ++i) {
        if (first) {
          first = false;
        } else {
          s.append(", ");
        }
        s.append(utilities.s(params.get(i).result().type_bound().to_string()));
      }
    }

    s.append(']');
    return new base_string(s.toString());
  }
}
