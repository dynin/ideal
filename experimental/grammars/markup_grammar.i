-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- The grammar for a subset of XML.
---
--- Used https://cs.lmu.edu/~ray/notes/xmlgrammar/ as a reference.
grammar markup_grammar {
  terminal character lt, gt, slash, amp, semicolon, quot, apos, eq;
  terminal character underscore, colon, dot, minus;
  terminal character whitespace, letter, etc;

  nonterminal character name_start, name_part;
  nonterminal character content_part, content_not_apos, content_not_quot;
  nonterminal void space_opt, equals;
  nonterminal string name;
  nonterminal special_text entity_ref;
  nonterminal string quot_attr_value;
  nonterminal attribute_fragment attribute_fragment_in_quot, attribute_value_in_quot;
  nonterminal string apos_attr_value;
  nonterminal attribute_fragment attribute_fragment_in_apos, attribute_value_in_apos;
  nonterminal attribute_fragment attribute_value;
  nonterminal attribute_state attribute;
  nonterminal immutable list[attribute_state] attributes;
  nonterminal text_element element, empty_element;
  nonterminal text_fragment content_element, content_tail, content;
  nonterminal string content_characters_opt;
  nonterminal text_element start_tag;
  nonterminal string end_tag;
  nonterminal text_element document_matcher;

  name_start ::= letter | underscore | colon;
  name_part ::= letter | dot | minus | underscore | colon;

  content_part ::= lt | gt | slash | amp | semicolon | quot | apos | eq |
    underscore | colon | dot | minus |
    whitespace | letter | etc; -- not |lt| and not |amp|

  content_not_apos ::= lt | gt | slash | amp | semicolon | quot | eq |
    underscore | colon | dot | minus |
    whitespace | letter | etc; -- not |lt|, not |amp| and not |apos|

  content_not_quot ::= lt | gt | slash | amp | semicolon | apos | eq |
    underscore | colon | dot | minus |
    whitespace | letter | etc; -- not |lt|, not |amp| and not |quot|

  space_opt ::= whitespace *;

  name ::= name_start name_part *;

  entity_ref ::= amp name semicolon;

  equals ::= space_opt eq space_opt;

  quot_attr_value ::= content_not_quot +;
  attribute_fragment_in_quot ::= quot_attr_value | entity_ref;
  attribute_value_in_quot ::= quot attribute_fragment_in_quot * quot;

  quot_attr_value ::= content_not_apos +;
  attribute_fragment_in_apos ::= apos_attr_value | entity_ref;
  attribute_value_in_apos ::= apos attribute_fragment_in_apos * apos;

  attribute_value ::= attribute_value_in_quot | attribute_value_in_apos;
  attribute ::= space_opt name equals attribute_value;
  attributes ::= attribute *;

  empty_element ::= lt name attributes space_opt slash gt;

  element ::= empty_element | start_tag content end_tag;

  content_element ::= element | entity_ref;
  content_characters_opt ::= content_part *;
  content_tail ::= content_element content_characters_opt;
  content ::= content_characters_opt content_tail *;

  start_tag ::= lt name attributes space_opt gt;
  end_tag ::= lt slash name space_opt gt;

  document_matcher ::= space_opt element space_opt;
}
