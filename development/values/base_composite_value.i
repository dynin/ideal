-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class base_composite_value {
  extends debuggable;
  implements composite_wrapper, any composite_value;

  private final type bound;
  private final dictionary[variable_id, value_wrapper] bindings;

  base_composite_value(type bound) {
    this.bound = bound;
    bindings = hash_dictionary[variable_id, value_wrapper].new();
  }

  override type type_bound => bound;

  override any composite_value unwrap() => this;

  override put_var(variable_id key, value_wrapper value) => bindings.put(key, value);

  override value_wrapper get_var(variable_id key) {
    -- TODO: wrap null in value_wrapper?
    result : bindings.get(key);
    assert result is_not null;
    return result;
  }

  string to_string => "(composite)" ++ type_bound ++ ":{" ++ utilities.string_of(bindings) ++ "}";
}
