-- Copyright 2014-2022 The Ideal Authors. All rights reserved.

-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

namespace common_names {

  instance_name : simple_name.make("instance");

  ideal_name : simple_name.make("ideal");
  library_name : simple_name.make("library");
  elements_name : simple_name.make("elements");
  operators_name : simple_name.make("operators");

  entity_name : simple_name.make("entity");
  value_name : simple_name.make("value");
  void_name : simple_name.make("void");
  data_name : simple_name.make("data");
  enum_data_name : simple_name.make("enum_data");
  boolean_name : simple_name.make("boolean");
  character_name : simple_name.make("character");
  integer_name : simple_name.make("integer");
  nonnegative_name : simple_name.make("nonnegative");
  string_name : simple_name.make("string");
  null_name : simple_name.make("null");
  missing_name : simple_name.make("missing");
  undefined_name : simple_name.make("undefined");
  reference_name : simple_name.make("reference");
  stringable_name : simple_name.make("stringable");
  equality_comparable_name : simple_name.make("equality_comparable");
  reference_equality_name : simple_name.make("reference_equality");
  procedure_name : simple_name.make("procedure");
  function_name : simple_name.make("function");
  list_name : simple_name.make("list");

  get_name : simple_name.make("get");
  set_name : simple_name.make("set");
  size_name : simple_name.make("size");
  call_name : simple_name.make("call");
  to_string_name : simple_name.make("to_string");

  private first : simple_name.make("first");
  private second : simple_name.make("second");
  private third : simple_name.make("third");
  private fourth : simple_name.make("fourth");
  private fifth : simple_name.make("fifth");

  simple_name make_numbered_name(nonnegative index) pure {
    switch (index) {
      case 0:
        return first;
      case 1:
        return second;
      case 2:
        return third;
      case 3:
        return fourth;
      case 4:
        return fifth;
      default:
        return simple_name.make("number" ++ (index + 1));
    }
  }
}
