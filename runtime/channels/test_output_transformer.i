-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

test_suite test_output_transformer {

  static string test_transform(string source) pure {
    return base_string.new("+", source, "!");
  }

  test_case test_appender() {
    the_appender : appender[string].new();

    the_appender.write("foo");
    the_appender.write("bar");
    the_appender.write("baz");

    elements : the_appender.elements;
    assert elements.size == 3;
    assert elements[0] == "foo";
    assert elements[1] == "bar";
    assert elements[2] == "baz";

    the_appender.write_all(elements);
    more_elements : the_appender.elements;
    assert more_elements.size == 6;
    assert more_elements[0] == "foo";
    assert more_elements[1] == "bar";
    assert more_elements[2] == "baz";
    assert more_elements[3] == "foo";
    assert more_elements[4] == "bar";
    assert more_elements[5] == "baz";
  }

  test_case test_transformer() {
    the_appender : appender[string].new();
    the_transformer : output_transformer[string, string].new(test_transform, the_appender);

    the_transformer.write("foo");
    the_transformer.write("bar");
    the_transformer.write("baz");

    elements : the_appender.elements;
    assert elements.size == 3;

    assert elements[0] == "+foo!";
    assert elements[1] == "+bar!";
    assert elements[2] == "+baz!";
  }
}
