-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

test_suite test_markup_text {

  import ideal.machine.channels.string_writer;

  test_case test_writer_trivial() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_formatter.write(base_element.new(text_library.P, "foo"));

    assert "<p>\n foo\n</p>\n" == the_writer.elements;
  }

  test_case test_quoted() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_formatter.write("AT&T <etc.> q1:' q2:\"");

    assert "AT&amp;T &lt;etc.&gt; q1:&apos; q2:&quot;" == the_writer.elements;
  }

  test_case test_writer_indent() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_formatter.write(base_element.new(text_library.P, "foo"));
    the_formatter.write(base_element.new(text_library.INDENT, "bar"));

    assert "<p>\n foo\n</p>\n<indent>\n bar\n</indent>\n" == the_writer.elements;
  }

  test_case test_attribute() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_formatter.write(base_element.new(text_library.P, text_library.NAME, "foo", "bar"));

    assert "<p name='foo'>\n bar\n</p>\n" == the_writer.elements;
  }

  test_case test_self_closing_tag() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_formatter.write("foo");
    the_formatter.write(base_element.new(text_library.BR, text_library.CLEAR, "all",
        missing.instance));
    the_formatter.write("bar\n");

    assert "foo<br clear='all' />\nbar\n" == the_writer.elements;
  }

  test_case test_writer_fragment() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_entity : text_entity.new(text_library.IDEAL_TEXT, "*", "middot");

    the_formatter.write("one");
    the_formatter.write(the_entity);
    the_formatter.write("two");

    assert "one&middot;two" == the_writer.elements;
  }
}
