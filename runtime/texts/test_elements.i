-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

test_suite test_elements {

  test_case test_namespace_id() {
    assert text_library.HTML_NS.short_name == "html";
    assert text_library.HTML_NS.to_string() == "html";
  }

  test_case test_element_id() {
    assert text_library.P.short_name == "p";
    assert text_library.P.get_namespace == text_library.HTML_NS;
    assert text_library.P.to_string() == "html:p";

    assert text_library.DIV.short_name == "div";
    assert text_library.DIV.get_namespace == text_library.HTML_NS;
    assert text_library.DIV.to_string() == "html:div";
  }

  test_case test_base_element() {
    text_element element : base_element.new(text_library.P);

    assert element.get_id == text_library.P;
    assert element.attributes.is_empty;
    assert element.children is null;
  }

  test_case test_make_element() {
    node0 : base_element.new(text_library.P);
    -- TODO: shouldn't need to specify type
    text_node node1 : "foo";
    --string_text_node node1 : "foo";

    nodes : [node0, node1];
    element : text_utilities.make_element(text_library.BODY, nodes);

    assert element is base_element;
    assert element.get_id == text_library.BODY;
    assert element.attributes.is_empty;

    children : element.children;
    assert children is list_text_node;
    child_nodes : children.nodes;
    assert child_nodes.size == 2;

    child0 : child_nodes.first;
    assert child0 is base_element;
    assert child0.get_id == text_library.P;
    assert child0.attributes.is_empty;
    assert child0.children is null;

    child1 : child_nodes[1];
    assert child1 is string;
    assert child1 == "foo";
  }
}
