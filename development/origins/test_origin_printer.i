-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Testcase for |origin_printer| implementaion.
class test_origin_printer {
  import ideal.machine.channels.string_writer;

  void show_origin_helper(string input, nonnegative begin, nonnegative end, string output) {
    source : source_content.new(simple_name.make("foo"), input);
    the_origin : source.make_origin(begin, end) !> text_origin;
    the_writer : string_writer.new();
    out : plain_formatter.new(the_writer);

    out.write(origin_printer.render_text_origin(the_origin, missing.instance, missing.instance));

    assert the_writer.elements() == output;
  }

  test_case test_show_origins() {
    input : "hello\nworld\n";
    show_origin_helper(input, 1, 2, "hello\n ^\n");
    show_origin_helper(input, 0, 5, "hello\n^^^^^\n");
    show_origin_helper(input, 2, 6, "hello \n  ^^^^\n");
    show_origin_helper(input, 0, 10, "hello \n^^^^^^\n");
    show_origin_helper(input, 8, 10, "world\n  ^^\n");
    show_origin_helper(input, 7, 7, "world\n ^\n");
    show_origin_helper(input, 5, 5, "hello \n     ^\n");
    show_origin_helper(input, 12, 12, "world \n     ^\n");
  }
}
