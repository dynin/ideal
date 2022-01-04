-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

test_suite test_json_printer {
  import ideal.machine.characters.unicode_handler;

  private json_printer make_printer() {
    return json_printer.new(unicode_handler.instance);
  }

  test_case test_basic_printer() {
    printer : make_printer();

    json0 : printer.print("'Hello, world!'\n\\");
    assert json0 == "\"'Hello, world!'\\n\\\\\"";

    json1 : printer.print(68);
    assert json1 == "68";

    json_array array : json_array_impl.new();
    -- TODO: cast should be redundant
    array.append_all([ 42, 68 ] .> readonly list[readonly json_data]);
    json2 : printer.print(array);
    assert json2 == "[42, 68]";

    json3 : printer.print(missing.instance);
    assert json3 == "null";

    json_object the_object : json_object_list.new();
    the_object.put("foo", "bar");
    the_object.put("baz", 68);
    json4 : printer.print(the_object);
    assert json4 == "{\"foo\": \"bar\", \"baz\": 68}";

    json_object the_object2 : json_object_list.new();
    the_object2.put("foo", false);
    the_object2.put("bar", true);
    json5 : printer.print(the_object2);
    assert json5 == "{\"foo\": false, \"bar\": true}";
  }
}
