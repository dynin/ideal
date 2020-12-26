-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_markup_grammar {
  import ideal.machine.characters.normal_handler;

  testcase test_simple_parse() {
    grammar : markup_grammar.new(normal_handler.instance);
    document_pattern : grammar.document_pattern;

    assert document_pattern("<html>foo</html>");
    assert document_pattern("  <html>foo</html>  ");
    assert document_pattern("  <html  >foo</html  >  ");

    -- TODO: these should succeed.
    assert !document_pattern("  <html>Hello <em>world!</em></html>  ");
    assert !document_pattern("  <html><body ><p>Hello <em >world!</em ></p></body ></html>  ");

    assert !document_pattern(" no markup ");
    assert !document_pattern("  <html>foo  ");
    assert !document_pattern("  <html>foo<bar>  ");
    assert !document_pattern("  <>foo  ");

    -- TODO: this should fail.
    assert document_pattern("  <abc>foo</def>  ");
  }
}
