-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

test_suite test_plain_text {

  import ideal.machine.channels.string_writer;

  static FOO : "foo";
  static BAR : "bar";
  static BAZ : "baz";
  static WYZZY : "wyzzy";

  test_case test_writer_trivial() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(base_element.new(text_library.P, FOO));

    assert "foo\n" == the_writer.elements;
  }

  test_case test_writer_indent0() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(base_element.new(text_library.P, FOO));
    the_formatter.write(base_element.new(text_library.INDENT, BAR));

    assert "foo\n  bar\n" == the_writer.elements;
  }

  test_case test_writer_indent1() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(base_element.new(text_library.P, FOO));
    bar : base_element.new(text_library.P, BAR);
    baz : base_element.new(text_library.P, BAZ);
    the_formatter.write(base_element.new(text_library.INDENT,
        base_list_text_node.make(bar, baz)));
    the_formatter.write(base_element.new(text_library.P, WYZZY));

    assert "foo\n  bar\n  baz\nwyzzy\n" == the_writer.elements;
  }

  test_case test_writer_indent2() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(base_element.new(text_library.P, FOO));
    the_formatter.write(base_element.new(text_library.INDENT, "bar\nbaz"));
    the_formatter.write(base_element.new(text_library.P, WYZZY));

    assert "foo\n  bar\n  baz\nwyzzy\n" == the_writer.elements;
  }

  test_case test_self_closing_tag() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(FOO);
    the_formatter.write(base_element.new(text_library.BR,
        text_library.CLEAR, "all", missing.instance));
    the_formatter.write("bar\n");

    assert "foo\nbar\n" == the_writer.elements;
  }

  test_case test_writer_fragment() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    fragment : text_entity.new(text_library.IDEAL_TEXT, "*", "middot");

    the_formatter.write("one");
    the_formatter.write(fragment);
    the_formatter.write("two");

    assert "one*two" == the_writer.elements;
  }

  test_case test_underline_tag() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write("hello ");

    world_string : "world";
    br : base_element.new(text_library.BR, missing.instance);
    the_formatter.write(base_element.new(text_library.U,
        base_list_text_node.make(world_string, br, FOO)));
    the_formatter.write(" bar");
    the_formatter.write(base_element.new(text_library.BR, missing.instance));

    assert "hello world\n      ^^^^^\nfoo bar\n^^^\n" == the_writer.elements;
  }

  test_case test_underline2_tag() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write("hello ");

    world_string : "world";
    br : base_element.new(text_library.BR, missing.instance);
    the_formatter.write(base_element.new(text_library.U2,
        base_list_text_node.make(world_string, br, FOO)));
    the_formatter.write(" bar");
    the_formatter.write(base_element.new(text_library.BR, missing.instance));

    assert "hello world\n      -----\nfoo bar\n---\n" == the_writer.elements;
  }

  test_case test_two_underlines() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    text_element hi : base_element.new(text_library.U, "hi");
    -- TODO: Implement empty dictionary
    text_element mid : base_element.new(text_library.U2,
        list_dictionary[attribute_id, attribute_fragment].new(),
        base_list_text_node.make("start ", hi, " end"));
    the_formatter.write(text_utilities.join("foo ", mid, " bar"));
    the_formatter.write(base_element.new(text_library.BR, missing.instance));

    assert "foo start hi end bar\n    ------^^----\n" == the_writer.elements;
  }

  test_case test_blank_line() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(base_element.new(text_library.DIV, FOO));
    the_formatter.write(base_element.new(text_library.BR));
    the_formatter.write(base_element.new(text_library.DIV, "bar"));

    assert "foo\n\nbar\n" == the_writer.elements;
  }
}
