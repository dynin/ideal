-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

test_suite test_json_printer {
  import ideal.machine.characters.normal_handler;

  private json_printer make_printer() {
    return json_printer.new(normal_handler.instance);
  }

  test_case test_basic_printer() {
    printer : make_printer();

    json0 : printer.print("'Hello, world!'\n\\");
    assert json0 == "\"'Hello, world!'\\n\\\\\"";

    json1 : printer.print(68);
    assert json1 == "68";

    json2 : printer.print([ 42, 68 ]);
    assert json2 == "[42, 68]";

    json3 : printer.print(missing.instance);
    assert json3 == "null";

    the_dictionary : list_dictionary[string, readonly value].new();
    the_dictionary.put("foo", "bar");
    the_dictionary.put("baz", 68);
    json4 : printer.print(the_dictionary);
    assert json4 == "{\"foo\": \"bar\", \"baz\": 68}";

    the_dictionary2 : list_dictionary[string, readonly value].new();
    the_dictionary2.put("foo", false);
    the_dictionary2.put("bar", true);
    json5 : printer.print(the_dictionary2);
    assert json5 == "{\"foo\": false, \"bar\": true}";
  }
}
