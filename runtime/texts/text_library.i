-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Common text identifiers used in HTML, as well as ideal-specific ones.
public namespace text_library {

  HTML_NS : base_namespace.new("html");

  HTML : base_element_id.new(HTML_NS, "html");
  HEAD : base_element_id.new(HTML_NS, "head");
  TITLE : base_element_id.new(HTML_NS, "title");
  META : base_element_id.new(HTML_NS, "meta");
  LINK : base_element_id.new(HTML_NS, "link");
  BODY : base_element_id.new(HTML_NS, "body");
  P : base_element_id.new(HTML_NS, "p");
  DIV : base_element_id.new(HTML_NS, "div");
  H1 : base_element_id.new(HTML_NS, "h1");
  H2 : base_element_id.new(HTML_NS, "h2");
  PRE : base_element_id.new(HTML_NS, "pre");

  UL : base_element_id.new(HTML_NS, "ul");
  LI : base_element_id.new(HTML_NS, "li");

  TABLE : base_element_id.new(HTML_NS, "table");
  TR : base_element_id.new(HTML_NS, "tr");
  TH : base_element_id.new(HTML_NS, "th");
  TD : base_element_id.new(HTML_NS, "td");

  SPAN : base_element_id.new(HTML_NS, "span");
  BR : base_element_id.new(HTML_NS, "br");
  EM : base_element_id.new(HTML_NS, "em");
  A : base_element_id.new(HTML_NS, "a");
  B : base_element_id.new(HTML_NS, "b");
  U : base_element_id.new(HTML_NS, "u");
  HR : base_element_id.new(HTML_NS, "hr");

  DL : base_element_id.new(HTML_NS, "dl");
  DT : base_element_id.new(HTML_NS, "dt");
  DD : base_element_id.new(HTML_NS, "dd");

  -- TODO: use some other type of emphasis...
  U2 : base_element_id.new(HTML_NS, "u2");

  ID : base_attribute_id.new(HTML_NS, "id");
  NAME : base_attribute_id.new(HTML_NS, "name");
  CONTENT : base_attribute_id.new(HTML_NS, "content");
  CHARSET : base_attribute_id.new(HTML_NS, "charset");
  CLEAR : base_attribute_id.new(HTML_NS, "clear");
  CLASS : base_attribute_id.new(HTML_NS, "class");
  STYLE : base_attribute_id.new(HTML_NS, "style");
  HREF : base_attribute_id.new(HTML_NS, "href");
  REL : base_attribute_id.new(HTML_NS, "rel");
  TYPE : base_attribute_id.new(HTML_NS, "type");

  AMP : text_entity.new(HTML_NS, "&", "amp");
  LT : text_entity.new(HTML_NS, "<", "lt");
  GT : text_entity.new(HTML_NS, ">", "gt");
  APOS : text_entity.new(HTML_NS, "'", "apos");
  QUOT : text_entity.new(HTML_NS, "\"", "quot");

  BULL : text_entity.new(HTML_NS, "*", "bull");
  MIDDOT : text_entity.new(HTML_NS, ".", "middot");
  MDASH : text_entity.new(HTML_NS, "--", "mdash");
  NBSP : text_entity.new(HTML_NS, " ", "nbsp");
  THINSP : text_entity.new(HTML_NS, " ", "thinsp;");

  LARR : text_entity.new(HTML_NS, "<-", "larr");
  UARR : text_entity.new(HTML_NS, "^", "uarr");
  RARR : text_entity.new(HTML_NS, "->", "rarr");
  DARR : text_entity.new(HTML_NS, "V", "darr");

  immutable list[element_id] HTML_ELEMENTS : [
    HTML, HEAD, TITLE, META, LINK, BODY,
    P, DIV, H1, H2, PRE,
    UL, LI,
    TABLE, TR, TH, TD,
    SPAN, BR, EM, A, B, U,
    HR, DL, DT, DD,
    -- TODO: include U2?
    -- TODO: the cast should be redundant; use deeply_immutable
  ];

  immutable list[attribute_id] HTML_ATTRIBUTES : [
    ID, NAME, CONTENT, CHARSET, CLEAR, CLASS, STYLE, HREF, REL, TYPE
    -- TODO: the cast should be redundant; use deeply_immutable
  ];

  immutable list[special_text] HTML_ENTITIES : [
    AMP, LT, GT, APOS, QUOT,
    BULL, MIDDOT, MDASH, NBSP, THINSP,
    LARR, UARR, RARR, DARR
    -- TODO: the cast should be redundant; use deeply_immutable
  ];

  FRAGMENT_SEPARATOR : "#";

  IDEAL_TEXT : base_namespace.new("itext");
  INDENT : base_element_id.new(IDEAL_TEXT, "indent");

  ERROR_ELEMENT : base_element_id.new(IDEAL_TEXT, "_error_");
  ERROR_ATTRIBUTE : base_attribute_id.new(IDEAL_TEXT, "_error_");
  ERROR_ENTITY : text_entity.new(IDEAL_TEXT, "_error_", "_error_");
}
