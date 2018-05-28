-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

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

  overload markup_formatter(output[character] out, string spaces) {
    super(out, spaces);
  }

  overload markup_formatter(output[character] out) {
    this(out, DEFAULT_INDENT);
  }

  override void process_string(string s) {
    write_escaped(s);
  }

  override void process_element(text_element element) {
    is_block : text_util.is_block(element);

    attributes : get_attributes(element.children);
    non_attributes : skip_attributes(element.children);

    if (non_attributes.is_empty) {
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

      process_all(non_attributes);

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

  private void do_indent() {
    indent += 1;
  }

  private void do_unindent() {
    new_indent : indent - 1;
    assert new_indent is nonnegative;
    indent = new_indent;
  }

  private static boolean is_attribute(text_node node) {
    -- TODO: use and.
    --return node is text_element && node.get_id() is attribute_id;
    if (node is text_element) {
      return node.get_id() is attribute_id;
    } else {
      return false;
    }
  }

  private static readonly list[text_element] get_attributes(readonly list[text_node] nodes) {
    result : base_list[text_element].new();
    for (var nonnegative i : 0; i < nodes.size; i += 1) {
      if (is_attribute(nodes[i])) {
        result.append(nodes[i] as text_element);
      }
    }
    return result;
  }

  private static readonly list[text_node] skip_attributes(readonly list[text_node] nodes) {
    var nonnegative i : 0;
    while (i < nodes.size && is_attribute(nodes[i])) {
      i += 1;
    }
    for (var nonnegative j : i; j < nodes.size; j += 1) {
      assert !is_attribute(nodes[j]);
    }
    return nodes.slice(i);
  }

  override void process_special(special_text t) {
    write_string(t.to_markup());
  }

  private void write_start_tag(text_element element, readonly list[text_element] attributes) {
    write_string(OPEN_START_TAG);
    write_escaped(element.get_id().short_name);
    write_tag_attributes(attributes);
    write_string(CLOSE_TAG);
  }

  private void write_end_tag(text_element element) {
    write_string(OPEN_END_TAG);
    write_escaped(element.get_id().short_name);
    write_string(CLOSE_TAG);
  }

  private void write_self_closing_tag(text_element element,
      readonly list[text_element] attributes) {
    write_string(OPEN_START_TAG);
    write_escaped(element.get_id().short_name);
    write_tag_attributes(attributes);
    write_string(CLOSE_SELF_CLOSING_TAG);
  }

  private void write_tag_attributes(readonly list[text_element] attributes) {
    for (var nonnegative i : 0; i < attributes.size; i += 1) {
      attribute : attributes[i];
      assert attribute.get_id() is attribute_id;
      -- TODO: relax this, support multiple strings.
      assert attribute.children.size == 1;
      assert attribute.children[0] is string;

      write_string(ATTRIBUTE_SEPARATOR);
      write_escaped(attribute.get_id().short_name);
      write_string(ATTRIBUTE_START);
      write_escaped(attribute.children[0] as string);
      write_string(ATTRIBUTE_END);
    }
  }

  private void write_escaped(string s) {
    -- TODO: move escape code here...
    write_string(runtime_util.escape_markup(s));
  }
}
