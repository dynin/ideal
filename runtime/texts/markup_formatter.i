-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.machine.channels.string_writer;

--- Format text for output as markup.
class markup_formatter {
  extends text_formatter;

  import ideal.machine.elements.runtime_util;

  static OPEN_START_TAG : "<";
  static OPEN_END_TAG : "</";
  static ATTRIBUTE_SEPARATOR : " ";
  static ATTRIBUTE_START : "='";
  static ATTRIBUTE_END : "'";
  static CLOSE_TAG : ">";
  static CLOSE_SELF_CLOSING_TAG : " />";

  static DEFAULT_INDENT : " ";

  private boolean write_newlines;

  overload markup_formatter(output[character] out, string spaces, boolean write_newlines) {
    super(out, spaces);
    this.write_newlines = write_newlines;
  }

  overload markup_formatter(output[character] out, string spaces) {
    this(out, spaces, true);
  }

  overload markup_formatter(output[character] out) {
    this(out, DEFAULT_INDENT, true);
  }

  override process_string(string s) {
    write_escaped(s);
  }

  override process_element(text_element element) {
    is_block : write_newlines && text_utilities.is_block(element);

    attributes : element.attributes;
    children : element.children;

    if (children is null) {
      write_self_closing_tag(element, attributes);
      if (is_block) {
        write_newline();
      }
    } else {
      if (is_block) {
        if (!first) {
          write_newline();
        }
        if (indent > 0) {
          do_indent();
        }
        write_start_tag(element, attributes);
        write_newline();
        do_indent();
      } else {
        write_start_tag(element, attributes);
      }

      process(children);

      if (is_block) {
        if (!first) {
          write_newline();
        }
        do_unindent();
        write_end_tag(element);
        write_newline();
        if (indent > 0) {
          do_unindent();
        }
      } else {
        write_end_tag(element);
      }
    }
  }

  private do_indent() {
    indent += 1;
  }

  private do_unindent() {
    new_indent : indent - 1;
    assert new_indent is nonnegative;
    indent = new_indent;
  }

  override process_special(special_text t) {
    write_string(t.to_markup);
  }

  private write_start_tag(text_element element,
      readonly dictionary[attribute_id, attribute_fragment] attributes) {
    write_string(OPEN_START_TAG);
    write_escaped(element.get_id.short_name);
    write_tag_attributes(attributes);
    write_string(CLOSE_TAG);
  }

  private write_end_tag(text_element element) {
    write_string(OPEN_END_TAG);
    write_escaped(element.get_id.short_name);
    write_string(CLOSE_TAG);
  }

  private write_self_closing_tag(text_element element,
      readonly dictionary[attribute_id, attribute_fragment] attributes) {
    write_string(OPEN_START_TAG);
    write_escaped(element.get_id.short_name);
    write_tag_attributes(attributes);
    write_string(CLOSE_SELF_CLOSING_TAG);
  }

  private write_tag_attributes(
      readonly dictionary[attribute_id, attribute_fragment] attributes) {
    for (attribute : attributes.elements) {
      write_string(ATTRIBUTE_SEPARATOR);
      write_escaped(attribute.key.short_name);
      write_string(ATTRIBUTE_START);
      process(attribute.value);
      write_string(ATTRIBUTE_END);
    }
  }

  private write_escaped(string s) {
    -- TODO: move escape code here...
    write_string(runtime_util.escape_markup(s));
  }
}
