-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_elements {

  testcase test_namespace_id() {
    assert text_library.HTML_NS.short_name == "html";
    assert text_library.HTML_NS.to_string() == "html";
  }

  testcase test_element_id() {
    assert text_library.P.short_name == "p";
    assert text_library.P.get_namespace() == text_library.HTML_NS;
    assert text_library.P.to_string() == "html:p";

    assert text_library.DIV.short_name == "div";
    assert text_library.DIV.get_namespace() == text_library.HTML_NS;
    assert text_library.DIV.to_string() == "html:div";
  }

  testcase test_base_element() {
    text_element element : base_element.new(text_library.P);

    assert element.get_id() == text_library.P;
    assert element.attributes().is_empty;
    assert element.children() is null;
  }
}
