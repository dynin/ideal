-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.channels.string_writer;

--- Base class for text formatters-types that output |text_fragment|s.
class plain_formatter {
  extends text_formatter;

  private var nonnegative chars_written;
  private list[underline_style] underline_stack;
  private string_writer carets;

  private static character SPACE : ' ';
  private static string DEFAULT_INDENT : "  ";

  overload plain_formatter(output[character] the_output, string spaces) {
    super(the_output, spaces);
    chars_written = 0;
    underline_stack = base_list[underline_style].new();
    carets = string_writer.new();
  }

  public overload plain_formatter(output[character] the_output) {
    this(the_output, DEFAULT_INDENT);
  }

  override void process_string(string s) {
    write_string(s);
  }

  override void process_element(text_element element) {
    if (text_util.is_block(element)) {
      if (!first || element.get_id == text_library.BR) {
        write_newline();
      }

      if (text_util.is_indent(element)) {
        indent += 1;
      }
    }

    style : underline_style.all_styles.get(element.get_id);
    if (style is_not null) {
      underline_stack.append(style);
    }

    children : element.children;
    if (children is_not null) {
      process(children);
    }

    if (text_util.is_block(element)) {
      if (!first) {
        write_newline();
      }

      if (text_util.is_indent(element)) {
        new_indent : indent - 1;
        assert new_indent is nonnegative;
        indent = new_indent;
      }
    }

    if (style is_not null) {
      underline_stack.remove_last();
    }
  }

  override void process_special(special_text t) {
    write_string(t.to_plain_text);
  }

  override void do_write_newline() {
    super.do_write_newline();
    -- TODO: this should be a variable.
    if (carets.size() > 0) {
      the_output.write_all(carets.extract_elements());
      the_output.write(NEWLINE);
    }
    chars_written = 0;
  }

  override void do_write_indent() {
    super.do_write_indent();
    chars_written += spaces.size * indent;
  }

  -- TODO: string should work here.
  override void do_write_string(readonly list[character] the_string) {
    super.do_write_string(the_string);
    if (underline_stack.is_not_empty) {
      -- TODO: this should be a variable.
      -- TODO: use repeat
      while (carets.size() < chars_written) {
        carets.write(SPACE);
      }
      last_underline_index : underline_stack.size - 1;
      assert last_underline_index is nonnegative;
      underline_character : underline_stack[last_underline_index].display_character;
      -- TODO: use repeat
      for (var nonnegative i : 0; i < the_string.size; i += 1) {
        carets.write(underline_character);
      }
    }
    chars_written += the_string.size;
  }
}
