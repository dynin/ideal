-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

package texts {
  implicit import ideal.library.elements;

  --- A text fragment can be a text node or an immutable list of text nodes.
  --- The text fragment is always balanced.
  interface text_fragment {
    extends deeply_immutable data;
    extends stringable;
  }

  --- A text node can be a text element, string, or a special text.
  interface text_node {
    extends text_fragment;
  }

  --- Text element is data structure defining a markup element.
  --- It has an identifier, optional collection of attributes,
  --- and optional children.  If children are missing, then the element
  --- is self-closing.
  interface text_element {
    extends text_node;

    element_id get_id;
    immutable dictionary[attribute_id, string] attributes;
    immutable text_fragment or null children;
  }

  interface list_text_node {
    extends text_fragment;

    immutable list[text_node] nodes;
  }

  interface special_text {
    extends text_node;

    string to_plain_text;
    string to_markup;
  }

  supertype_of_string interface string_text_node {
    extends text_node, string;
  }

  interface element_id {
    extends identifier;
    text_namespace get_namespace;
    string short_name;
  }

  interface attribute_id {
    extends identifier;
    text_namespace get_namespace;
    string short_name;
  }

  interface text_namespace {
    extends identifier;
    string short_name;
  }
}
