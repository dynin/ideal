-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Common text identifiers used in HTML, as well as ideal-specific ones.
public namespace text_library {

  HTML_NS : base_namespace.new("html");

  HTML : base_element_id.new(HTML_NS, "html");
  HEAD : base_element_id.new(HTML_NS, "head");
  TITLE : base_element_id.new(HTML_NS, "title");
  LINK : base_element_id.new(HTML_NS, "link");
  BODY : base_element_id.new(HTML_NS, "body");
  P : base_element_id.new(HTML_NS, "p");
  DIV : base_element_id.new(HTML_NS, "div");
  H1 : base_element_id.new(HTML_NS, "h1");
  H2 : base_element_id.new(HTML_NS, "h2");

  TABLE : base_element_id.new(HTML_NS, "table");
  TR : base_element_id.new(HTML_NS, "tr");
  TH : base_element_id.new(HTML_NS, "th");
  TD : base_element_id.new(HTML_NS, "td");

  SPAN : base_element_id.new(HTML_NS, "span");
  BR : base_element_id.new(HTML_NS, "br");
  EM : base_element_id.new(HTML_NS, "em");
  A : base_element_id.new(HTML_NS, "a");
  B : base_element_id.new(HTML_NS, "b");
  UNDERLINE : base_element_id.new(HTML_NS, "u");
  -- TODO: use some other type of emphasis...
  UNDERLINE2 : base_element_id.new(HTML_NS, "u2");

  NAME : base_attribute_id.new(HTML_NS, "name");
  CLEAR : base_attribute_id.new(HTML_NS, "clear");
  CLASS : base_attribute_id.new(HTML_NS, "class");
  STYLE : base_attribute_id.new(HTML_NS, "style");
  HREF : base_attribute_id.new(HTML_NS, "href");
  REL : base_attribute_id.new(HTML_NS, "rel");
  TYPE : base_attribute_id.new(HTML_NS, "style");

  BULL : text_entity.new(HTML_NS, "*", "&bull;");
  MIDDOT : text_entity.new(HTML_NS, ".", "&middot;");
  MDASH : text_entity.new(HTML_NS, "--", "&mdash;");
  NBSP : text_entity.new(HTML_NS, " ", "&nbsp;");
  THINSP : text_entity.new(HTML_NS, " ", "&thinsp;");

  LARR : text_entity.new(HTML_NS, "<-", "&larr;");
  UARR : text_entity.new(HTML_NS, "^", "&uarr;");
  RARR : text_entity.new(HTML_NS, "->", "&rarr;");
  DARR : text_entity.new(HTML_NS, "V", "&darr;");

  IDEAL_TEXT : base_namespace.new("itext");
  INDENT : base_element_id.new(IDEAL_TEXT, "indent");
}
