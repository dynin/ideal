-- Copyright 2014 The Ideal Authors. All rights reserved.
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

  --- This interface that hides the differences between element and attribute ids was
  --- proposed by Erik Naggum.
  ---
  --- @see http://genius.cat-v.org/erik-naggum/lisp-markup
  interface text_element {
    extends text_node;

    text_id get_id();
    immutable list[text_node] children;
  }

  interface list_text_node {
    extends text_fragment;
    immutable list[text_node] nodes();
  }

  interface special_text {
    extends text_node;
    string to_plain_text();
    string to_markup();
  }

  interface string_text_node {
    extends text_node, string;
  }

  interface text_id {
    extends identifier;
    text_namespace get_namespace();
    string short_name;
  }

  interface text_namespace {
    extends identifier;
    string short_name;
  }
}
