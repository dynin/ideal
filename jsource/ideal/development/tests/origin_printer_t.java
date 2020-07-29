/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tests;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.machine.channels.string_writer;
import ideal.runtime.texts.*;
import ideal.runtime.channels.*;
import ideal.development.elements.*;
import ideal.development.scanners.*;
import ideal.development.origins.*;
import ideal.development.notifications.*;

import java.io.StringWriter;
import junit.framework.TestCase;

/**
 * A set of tests for the |text_origin| printer.
 */
public class origin_printer_t extends TestCase {

  private static void show_origin_helper(string input, int begin, int end, String output) {
    source_content source = new source_content(simple_name.make("foo"), input);
    text_origin pos = (text_origin) source.make_origin(begin, end);
    string_writer the_writer = new string_writer();
    plain_formatter out = new plain_formatter(the_writer);

    out.write(origin_printer.render_text_origin(pos, null, null));

    assertEquals(new base_string(output), the_writer.elements());
  }

  public void test_show_origins() {
    string input = new base_string("hello\nworld\n");
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
