/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
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
import ideal.development.notifications.*;

import java.io.StringWriter;
import junit.framework.TestCase;

/**
 * A set of tests for the |text_position| printer.
 */
public class position_printer_t extends TestCase {

  private static void show_position_helper(string input, int begin, int end, String output) {
    source_content source = new source_content(simple_name.make("foo"), input);
    text_position pos = (text_position) source.make_position(begin, end);
    string_writer the_writer = new string_writer();
    plain_formatter out = new plain_formatter(the_writer);

    out.write(position_printer.render_text_position(pos, null, null));

    assertEquals(new base_string(output), the_writer.elements());
  }

  public void test_show_positions() {
    string input = new base_string("hello\nworld\n");
    show_position_helper(input, 1, 2, "hello\n ^\n");
    show_position_helper(input, 0, 5, "hello\n^^^^^\n");
    show_position_helper(input, 2, 6, "hello \n  ^^^^\n");
    show_position_helper(input, 0, 10, "hello \n^^^^^^\n");
    show_position_helper(input, 8, 10, "world\n  ^^\n");
    show_position_helper(input, 7, 7, "world\n ^\n");
    show_position_helper(input, 5, 5, "hello \n     ^\n");
    show_position_helper(input, 12, 12, "world \n     ^\n");
  }
}
