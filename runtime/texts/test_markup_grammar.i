-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_markup_grammar {
  implicit import ideal.runtime.texts.text_library;
  import ideal.machine.characters.normal_handler;

  markup_grammar make_grammar() {
    grammar : markup_grammar.new(normal_handler.instance);
    grammar.add_entities(text_library.HTML_ENTITIES);
    grammar.complete();
    return grammar;
  }

  testcase test_entity_ref() {
    entity_ref : make_grammar().entity_ref;

    assert entity_ref("&lt;");
    assert entity_ref("&amp;");
    assert entity_ref("&bull;");
    assert !entity_ref("foo");
    assert !entity_ref("&foo");
    assert !entity_ref("foo;");

    assert entity_ref.parse("&lt;") == LT;
    assert entity_ref.parse("&gt;") == GT;
    assert entity_ref.parse("&apos;") == APOS;
    assert entity_ref.parse("&quot;") == QUOT;
    assert entity_ref.parse("&mdash;") == MDASH;
    assert entity_ref.parse("&nbsp;") == NBSP;
  }

  testcase test_attribute_value() {
    grammar : make_grammar();
    quot_attr_value : grammar.quot_attr_value;

    assert quot_attr_value("foo");
    assert quot_attr_value("*bar*");
    assert quot_attr_value("'baz'");
    assert !quot_attr_value("&lt;");
    assert !quot_attr_value("\"a");

    apos_attr_value : grammar.apos_attr_value;

    assert apos_attr_value("foo");
    assert apos_attr_value("*bar*");
    assert apos_attr_value("\"baz\"");
    assert !apos_attr_value("&lt;");
    assert !apos_attr_value("'a");
  }

  testcase test_simple_parse() {
    document_pattern : make_grammar().document_pattern;

    assert document_pattern("<html>foo</html>");
    assert document_pattern("  <html>foo</html>  ");
    assert document_pattern("  <html  >foo</html  >  ");
    assert document_pattern("  <html  >Hello &amp; goodbye!</html  >  ");
    assert document_pattern("  <html  />  ");
    assert document_pattern("<html/>");

    assert document_pattern("  <html>Hello <em>world!</em></html>  ");
    assert document_pattern("  <html><body ><p>Hello <em >world!</em ></p></body ></html>  ");
    assert document_pattern("  <html><body > <p>Hello<br />world!</p> </body ></html>  ");

    assert document_pattern("  <html><body > Hello &lt;world!&gt; </body ></html>  ");
    assert document_pattern("<html><p class='klass'>foo</p></html>");
    assert document_pattern("<html><a class = 'klass' href = 'link'>bar</a></html>");
    assert document_pattern("<html><p class = 'value\">==' attr=\"foo'\">foo</p></html>");
    assert document_pattern("<html><p class = '***' attr=\"baz\">foo</p></html>");

    assert !document_pattern(" no markup ");
    assert !document_pattern("  <html>foo  ");
    assert !document_pattern("  <html>foo<bar>  ");
    assert !document_pattern("  <>foo  ");
    assert !document_pattern("  &amp;<html>foo</html>  ");
    assert !document_pattern("<html><p class='klass\">foo</p></html>");
    assert !document_pattern("<html><p class='klass'>foo</p class=\"foo\"></html>");
    assert !document_pattern("<html foo= ><p class='klass'>foo</p></html>");
    assert !document_pattern("<html foo=bar><p class='klass'>foo</p></html>");

    -- TODO: this should fail.
    assert document_pattern("  <abc>foo</def>  ");
  }
}
