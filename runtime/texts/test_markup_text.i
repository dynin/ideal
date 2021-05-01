-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_markup_text {

  import ideal.machine.channels.string_writer;

  test_case test_writer_trivial() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_formatter.write(base_element.make(text_library.P, "foo" !> base_string));

    assert "<p>\n foo\n</p>\n" == the_writer.elements();
  }

  test_case test_quoted() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_formatter.write("AT&T <etc.> q1:' q2:\"" !> base_string);

    assert "AT&amp;T &lt;etc.&gt; q1:&apos; q2:&quot;" == the_writer.elements();
  }

  test_case test_writer_indent() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_formatter.write(base_element.make(text_library.P, "foo" !> base_string));
    the_formatter.write(base_element.make(text_library.INDENT, "bar" !> base_string));

    assert "<p>\n foo\n</p>\n<indent>\n bar\n</indent>\n" == the_writer.elements();
  }

  test_case test_attribute() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_formatter.write(base_element.make(text_library.P,
        text_library.NAME, "foo" !> base_string, "bar" !> base_string));

    assert "<p name='foo'>\n bar\n</p>\n" == the_writer.elements();
  }

  test_case test_self_closing_tag() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_formatter.write("foo" !> base_string);
    the_formatter.write(base_element.make(text_library.BR,
        text_library.CLEAR, "all" !> base_string, missing.instance));
    the_formatter.write("bar\n" !> base_string);

    assert "foo<br clear='all' />\nbar\n" == the_writer.elements();
  }

  test_case test_writer_fragment() {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer);

    the_entity : text_entity.new(text_library.IDEAL_TEXT, "*", "middot");

    the_formatter.write("one" !> base_string);
    the_formatter.write(the_entity);
    the_formatter.write("two" !> base_string);

    assert "one&middot;two" == the_writer.elements();
  }
}
