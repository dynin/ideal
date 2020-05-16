-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Utility functions for managing text fragments and friends.
namespace text_util {

  import ideal.machine.channels.string_writer;

  text_fragment EMPTY_FRAGMENT : base_list_text_node.new(empty[text_node].new());

  boolean is_indent(text_element element) {
    return element.get_id == text_library.INDENT;
  }

  -- TODO: rewrite this using a set
  boolean is_block(text_element element) {
    id : element.get_id;

    return is_indent(element) ||
           id == text_library.HTML ||
           id == text_library.HEAD ||
           id == text_library.BODY ||
           id == text_library.TITLE ||
           id == text_library.LINK ||
           id == text_library.P ||
           id == text_library.DIV ||
           id == text_library.H1 ||
           id == text_library.H2 ||
           id == text_library.TABLE ||
           id == text_library.TR ||
           id == text_library.TH ||
           id == text_library.TD ||
           id == text_library.BR;
  }

  -- TODO: factor out.
  immutable list[text_node] make_singleton(text_node element) {
    the_list : base_list[text_node].new();
    the_list.append(element);
    return the_list.frozen_copy();
  }

  immutable list[text_node] to_list(text_fragment or null fragment) {
    if (fragment is null) {
      return empty[text_node].new();
    } else if (fragment is text_node) {
      return make_singleton(fragment);
    } else if (fragment is list_text_node) {
      return fragment.nodes;
    } else {
      utilities.panic("Unknown type " ++ fragment);
    }
  }

  overload text_fragment join(readonly list[text_fragment] fragments) {
    nodes : base_list[text_node].new();

    for (fragment : fragments) {
      append(nodes, fragment);
    }

    return to_fragment(nodes);
  }

  overload text_fragment join(text_fragment first, text_fragment second) {
    nodes : base_list[text_node].new();

    append(nodes, first);
    append(nodes, second);

    return to_fragment(nodes);
  }

  -- TODO: refactor and reuse code.
  overload text_fragment join(text_fragment first, text_fragment second, text_fragment third) {
    nodes : base_list[text_node].new();

    append(nodes, first);
    append(nodes, second);
    append(nodes, third);

    return to_fragment(nodes);
  }

  private void append(list[text_node] nodes, text_fragment fragment) {
    if (fragment is string_text_node) {
      if (fragment.is_empty) {
        return;
      }
    }
    if (fragment is text_node) {
      nodes.append(fragment);
    } else {
      nodes.append_all((fragment as list_text_node).nodes);
    }
  }

  private text_fragment to_fragment(readonly list[text_node] nodes) {
    if (nodes.is_empty) {
      return EMPTY_FRAGMENT;
    } else if (nodes.size == 1) {
      return nodes.first;
    } else {
      return base_list_text_node.new(nodes);
    }
  }

  string to_plain_text(text_fragment text) {
    the_writer : string_writer.new();
    formatter : plain_formatter.new(the_writer);
    formatter.write(text);
    return the_writer.elements();
  }

  string to_markup_string(text_fragment text) {
    the_writer : string_writer.new();
    formatter : markup_formatter.new(the_writer);
    formatter.write(text);
    return the_writer.elements();
  }

  text_element make_element(element_id id, readonly list[text_node] children) {
    var text_fragment or null child_fragment;
    -- TODO: use conditional ? operator
    if (children is_not null) {
      child_fragment = base_list_text_node.new(children);
    } else {
      child_fragment = missing.instance;
    }
    -- TODO: use empty dictionary
    return base_element.new(id, list_dictionary[attribute_id, string].new(), child_fragment);
  }

  text_element make_html_link(text_fragment text, string link_target) {
    return base_element.make(text_library.A, text_library.HREF, link_target, text);
  }
}
