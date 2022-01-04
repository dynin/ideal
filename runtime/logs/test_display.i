-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

test_suite test_display {

  import ideal.machine.elements.runtime_util;
  import ideal.machine.channels.string_writer;

  -- TODO: use datatype here
  class my_data {
    extends data;

    string foo;
    string bar;
    integer baz;

    -- TODO: generate constructor
    my_data(string foo, string bar, integer baz) {
      this.foo = foo;
      this.bar = bar;
      this.baz = baz;
    }
  }

  test_case test_simple_display() {
    data_object : my_data.new("aaa", "bbb", 68);
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(runtime_util.display(data_object));

    assert "my_data {\n  foo: \"aaa\"\n  bar: \"bbb\"\n  baz: 68\n}\n" == the_writer.elements;
  }

  -- TODO: use datatype here
  class my_data2 {
    extends data;

    string foo;
    dont_display string or null bar;
    dont_display integer baz;

    -- TODO: generate constructor
    my_data2(string foo, string or null bar, integer baz) {
      this.foo = foo;
      this.bar = bar;
      this.baz = baz;
    }
  }

  test_case test_display_with_annotations() {
    data_object : my_data2.new("aaa", missing.instance, 68);
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(runtime_util.display(data_object));

    assert "my_data2 {\n  foo: \"aaa\"\n}\n" == the_writer.elements;
  }
}
