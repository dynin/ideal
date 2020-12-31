/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;

public class type_parameters extends debuggable implements deeply_immutable_data {

  private immutable_list<abstract_value> parameters;
  private @Nullable abstract_value repeated_parameter;

  public type_parameters(readonly_list<abstract_value> parameters) {
    this.parameters = parameters.frozen_copy();
    this.repeated_parameter = null;
  }

  public type_parameters(readonly_list<abstract_value> parameters,
      abstract_value repeated_parameter) {
    this.parameters = parameters.frozen_copy();
    this.repeated_parameter = repeated_parameter;
  }

  public boolean is_fixed_size() {
    return repeated_parameter == null;
  }

  public boolean is_empty() {
    return parameters.is_empty() && is_fixed_size();
  }

  public boolean is_not_empty() {
    return !is_empty();
  }

  public boolean is_valid_arity(int arity) {
    if (is_fixed_size()) {
      return arity == parameters.size();
    } else {
      return arity >= parameters.size();
    }
  }

  public abstract_value first() {
    return get(0);
  }

  public abstract_value get(int index) {
    if (index < parameters.size()) {
      return parameters.get(index);
    } else if (repeated_parameter != null) {
      return repeated_parameter;
    } else {
      utilities.panic("Parameter index out of range");
      return null;
    }
  }

  public immutable_list<abstract_value> fixed_size_list() {
    assert is_fixed_size();
    return parameters;
  }

  // TODO: deprecate all uses of this.
  public immutable_list<abstract_value> internal_access() {
    assert is_fixed_size();
    return parameters;
  }

  // TODO: we shouldn't need this.
  public immutable_set<principal_type> principals_set() {
    set<principal_type> result = new hash_set<principal_type>();
    for (int i = 0; i < parameters.size(); ++i) {
      result.add(parameters.get(i).type_bound().principal());
    }
    if (repeated_parameter != null) {
      result.add(repeated_parameter.type_bound().principal());
    }
    return result.frozen_copy();
  }

  @Override
  public string to_string() {
    StringBuilder sb = new StringBuilder("[");

    for (int i = 0; i < parameters.size(); ++i) {
      if (i > 0) {
        sb.append(", ");
      }
      abstract_value parameter = parameters.get(i);
      string name;
      // TODO: this heuristic needs to be improved...
      if (parameter instanceof base_type /*&& type_utilities.is_type_alias((type) parameter)*/) {
        name = ((base_type) parameter).describe(type_format.TWO_PARENTS);
      } else {
        name = parameter.to_string();
      }
      sb.append(utilities.s(name));
    }
    sb.append("]");

    return new base_string(sb.toString());
  }
}
