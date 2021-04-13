-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Markup used in ideal doc docmments.
public namespace doc_elements {
  DOC_NS : base_namespace.new("ideal-doc");

  CODE : base_element_id.new(DOC_NS, "code");
  C : base_element_id.new(DOC_NS, "c");
  J : base_element_id.new(DOC_NS, "j");
  CPP : base_element_id.new(DOC_NS, "cpp");

  -- VBAR : text_entity.new(DOC_NS, "|", "vbar");

  immutable list[element_id] HTML_ELEMENTS : [
    CODE, C, J, CPP
    -- TODO: the cast should be redundant; use deeply_immutable
  ] !> immutable list[element_id];
}
