-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.channels.string_writer;

class type_parameters {
  implements deeply_immutable data;
  extends debuggable;

  private immutable list[abstract_value] parameters;
  private abstract_value or null repeated_parameter;

  overload type_parameters(readonly list[abstract_value] parameters) {
    this.parameters = parameters.frozen_copy();
    this.repeated_parameter = missing.instance;
  }

  overload type_parameters(readonly list[abstract_value] parameters,
      abstract_value repeated_parameter) {
    this.parameters = parameters.frozen_copy();
    this.repeated_parameter = repeated_parameter;
  }

  boolean is_fixed_size => repeated_parameter is null;

  boolean is_empty => parameters.is_empty && is_fixed_size();

  boolean is_not_empty => !is_empty();

  boolean is_valid_arity(nonnegative arity) {
    if (is_fixed_size()) {
      return arity == parameters.size;
    } else {
      return arity >= parameters.size;
    }
  }

  abstract_value first => this[0];

  implicit abstract_value get(nonnegative index) {
    if (index < parameters.size) {
      return parameters[index];
    } else if (repeated_parameter is_not null) {
      return repeated_parameter;
    } else {
      utilities.panic("Parameter index out of range");
    }
  }

  immutable list[abstract_value] fixed_size_list() {
    assert is_fixed_size();
    return parameters;
  }

  -- TODO: deprecate all uses of this.
  immutable list[abstract_value] internal_access() {
    assert is_fixed_size();
    return parameters;
  }

  -- TODO: we shouldn't need this.
  immutable set[principal_type] principals_set() {
    result : hash_set[principal_type].new();
    for (parameter : parameters) {
      result.add(parameter.type_bound.principal);
    }
    if (repeated_parameter is_not null) {
      result.add(repeated_parameter.type_bound.principal);
    }
    return result.frozen_copy();
  }

  override string to_string() {
    the_writer : string_writer.new();
    the_writer.write_all("[");

    -- TODO: use list.join()
    for (var nonnegative i : 0; i < parameters.size; i += 1) {
      if (i > 0) {
        the_writer.write_all(", ");
      }
      parameter : parameters[i];
      var string name;
      -- TODO: this heuristic needs to be improved...
      if (parameter is base_type) { -- && type_utilities.is_type_alias((type) parameter)
        name = parameter.describe(type_format.TWO_PARENTS);
      } else {
        name = parameter.to_string;
      }
      the_writer.write_all(name);
    }

    the_writer.write_all("]");
    return the_writer.elements();
  }
}
