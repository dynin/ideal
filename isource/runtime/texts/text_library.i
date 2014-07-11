-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Common text identifiers used in HTML, as well as ideal-specific ones.
public namespace text_library {

  HTML_NS : base_namespace.new("html");

  HTML : element_id.new(HTML_NS, "html");
  HEAD : element_id.new(HTML_NS, "head");
  TITLE : element_id.new(HTML_NS, "title");
  LINK : element_id.new(HTML_NS, "link");
  BODY : element_id.new(HTML_NS, "body");
  P : element_id.new(HTML_NS, "p");
  DIV : element_id.new(HTML_NS, "div");
  H1 : element_id.new(HTML_NS, "h1");
  H2 : element_id.new(HTML_NS, "h2");

  TABLE : element_id.new(HTML_NS, "table");
  TR : element_id.new(HTML_NS, "tr");
  TH : element_id.new(HTML_NS, "th");
  TD : element_id.new(HTML_NS, "td");

  SPAN : element_id.new(HTML_NS, "span");
  BR : element_id.new(HTML_NS, "br");
  EM : element_id.new(HTML_NS, "em");
  A : element_id.new(HTML_NS, "a");
  B : element_id.new(HTML_NS, "b");
  UNDERLINE : element_id.new(HTML_NS, "u");
  -- TODO: use some other type of emphasis...
  UNDERLINE2 : element_id.new(HTML_NS, "u2");

  NAME : attribute_id.new(HTML_NS, "name");
  CLEAR : attribute_id.new(HTML_NS, "clear");
  CLASS : attribute_id.new(HTML_NS, "class");
  STYLE : attribute_id.new(HTML_NS, "style");
  HREF : attribute_id.new(HTML_NS, "href");
  REL : attribute_id.new(HTML_NS, "rel");
  TYPE : attribute_id.new(HTML_NS, "style");

  MIDDOT : text_entity.new(HTML_NS, ".", "&middot;");
  MDASH : text_entity.new(HTML_NS, "--", "&mdash;");
  NBSP : text_entity.new(HTML_NS, " ", "&nbsp;");
  THINSP : text_entity.new(HTML_NS, " ", "&thinsp;");

  LARR : text_entity.new(HTML_NS, "<-", "&larr;");
  UARR : text_entity.new(HTML_NS, "^", "&uarr;");
  RARR : text_entity.new(HTML_NS, "->", "&rarr;");
  DARR : text_entity.new(HTML_NS, "V", "&darr;");

  IDEAL_TEXT : base_namespace.new("itext");
  INDENT : element_id.new(IDEAL_TEXT, "indent");
}
