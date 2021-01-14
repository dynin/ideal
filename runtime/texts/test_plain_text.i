-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_plain_text {

  import ideal.machine.channels.string_writer;

  static FOO : "foo" as base_string;
  static BAR : "bar" as base_string;
  static BAZ : "baz" as base_string;
  static WYZZY : "wyzzy" as base_string;

  testcase test_writer_trivial() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(base_element.make(text_library.P, FOO));

    assert "foo\n" == the_writer.elements();
  }

  testcase test_writer_indent0() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(base_element.make(text_library.P, FOO));
    the_formatter.write(base_element.make(text_library.INDENT, BAR));

    assert "foo\n  bar\n" == the_writer.elements();
  }

  testcase test_writer_indent1() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(base_element.make(text_library.P, FOO));
    bar : base_element.make(text_library.P, BAR);
    baz : base_element.make(text_library.P, BAZ);
    the_formatter.write(base_element.make(text_library.INDENT,
        base_list_text_node.make(bar, baz)));
    the_formatter.write(base_element.make(text_library.P, WYZZY));

    assert "foo\n  bar\n  baz\nwyzzy\n" == the_writer.elements();
  }

  testcase test_writer_indent2() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(base_element.make(text_library.P, FOO));
    the_formatter.write(base_element.make(text_library.INDENT, "bar\nbaz" as base_string));
    the_formatter.write(base_element.make(text_library.P, WYZZY));

    assert "foo\n  bar\n  baz\nwyzzy\n" == the_writer.elements();
  }

  testcase test_self_closing_tag() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(FOO);
    the_formatter.write(base_element.make(text_library.BR,
        text_library.CLEAR, "all" as base_string, missing.instance));
    the_formatter.write("bar\n" as base_string);

    assert "foo\nbar\n" == the_writer.elements();
  }

  testcase test_writer_fragment() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    fragment : text_entity.new(text_library.IDEAL_TEXT, "*", "middot");

    the_formatter.write("one" as base_string);
    the_formatter.write(fragment);
    the_formatter.write("two" as base_string);

    assert "one*two" == the_writer.elements();
  }

  testcase test_underline_tag() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write("hello " as base_string);

    world_string : "world" as base_string;
    br : base_element.make(text_library.BR, missing.instance);
    the_formatter.write(base_element.make(text_library.U,
        base_list_text_node.make(world_string, br, FOO)));
    the_formatter.write(" bar" as base_string);
    the_formatter.write(base_element.make(text_library.BR, missing.instance));

    assert "hello world\n      ^^^^^\nfoo bar\n^^^\n" == the_writer.elements();
  }

  testcase test_underline2_tag() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write("hello " as base_string);

    world_string : "world" as base_string;
    br : base_element.make(text_library.BR, missing.instance);
    the_formatter.write(base_element.make(text_library.U2,
        base_list_text_node.make(world_string, br, FOO)));
    the_formatter.write(" bar" as base_string);
    the_formatter.write(base_element.make(text_library.BR, missing.instance));

    assert "hello world\n      -----\nfoo bar\n---\n" == the_writer.elements();
  }

  testcase test_two_underlines() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    text_element hi : base_element.make(text_library.U, "hi" as base_string);
    -- TODO: Implement empty dictionary
    text_element mid : base_element.new(text_library.U2,
        list_dictionary[attribute_id, attribute_fragment].new(),
        base_list_text_node.make("start " as base_string, hi, " end" as base_string));
    the_formatter.write(text_util.join("foo " as base_string, mid, " bar" as base_string));
    the_formatter.write(base_element.make(text_library.BR, missing.instance));

    assert "foo start hi end bar\n    ------^^----\n" == the_writer.elements();
  }

  testcase test_blank_line() {
    the_writer : string_writer.new();
    the_formatter : plain_formatter.new(the_writer);

    the_formatter.write(base_element.make(text_library.DIV, FOO));
    the_formatter.write(base_element.new(text_library.BR));
    the_formatter.write(base_element.make(text_library.DIV, "bar" as base_string));

    assert "foo\n\nbar\n" == the_writer.elements();
  }
}
