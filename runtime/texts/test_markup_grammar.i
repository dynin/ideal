-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_markup_grammar {
  implicit import ideal.runtime.texts.text_library;
  import ideal.machine.characters.normal_handler;
  import ideal.machine.channels.string_writer;

  markup_grammar make_grammar() {
    grammar : markup_grammar.new(normal_handler.instance);
    grammar.add_elements(text_library.HTML_ELEMENTS);
    grammar.add_attributes(text_library.HTML_ATTRIBUTES);
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

    attribute_value_in_quot : grammar.attribute_value_in_quot;

    assert attribute_value_in_quot.parse("\"\"").to_string == "";
    assert attribute_value_in_quot.parse("\"foo\"").to_string == "foo";
    assert attribute_value_in_quot.parse("\"&lt;\"").to_string == "&lt;";
    assert attribute_value_in_quot.parse("\"foo&lt;bar\"").to_string == "foo&lt;bar";
    assert attribute_value_in_quot.parse("\"&quot;-&apos;\"").to_string == "&quot;-&apos;";
    assert attribute_value_in_quot.parse("\"&lt;foo&gt;bar'baz\"").to_string ==
        "&lt;foo&gt;bar'baz";

    attribute_value_in_apos : grammar.attribute_value_in_apos;

    assert attribute_value_in_apos.parse("''").to_string == "";
    assert attribute_value_in_apos.parse("'foo'").to_string == "foo";
    assert attribute_value_in_apos.parse("'&lt;'").to_string == "&lt;";
    assert attribute_value_in_apos.parse("'foo&lt;bar'").to_string == "foo&lt;bar";
    assert attribute_value_in_apos.parse("'&quot;-&apos;'").to_string == "&quot;-&apos;";
    assert attribute_value_in_apos.parse("'&lt;foo&gt;bar\"baz'").to_string ==
        "&lt;foo&gt;bar\"baz";
  }

  testcase test_attribute() {
    attribute : make_grammar().attribute;

    assert attribute("id = '68'");
    assert attribute("href = \"https://theideal.org/\"");
    assert attribute("clear='all'");
    assert !attribute("<html>");
    assert !attribute("foo");
    assert !attribute("bar =");
    assert !attribute("&lt;name&gt; = 'value'");

    attribute0 : attribute.parse("id = '68'");
    assert attribute0.id == text_library.ID;
    assert (attribute0.value as string) == "68";

    attribute1 : attribute.parse("href = \"https://theideal.org/\"");
    assert attribute1.id == text_library.HREF;
    assert (attribute1.value as string) == "https://theideal.org/";
  }

  testcase test_empty_element() {
    empty_element : make_grammar().empty_element;

    assert empty_element("<html/>");
    assert empty_element("<body class=\"foo\" />");
    assert !empty_element("<html>");
    assert !empty_element("bar");
    assert !empty_element("&lt;html&gt;");

    element0 : empty_element.parse("<html/>");
    assert element0.get_id == text_library.HTML;
    assert element0.children is null;
    assert element0.attributes.is_empty;

    element1 : empty_element.parse("<body class=\"foo\" />");
    assert element1.get_id == text_library.BODY;
    assert element1.children is null;
    assert element1.attributes.size == 1;
    assert element1.attributes.elements[0].key == text_library.CLASS;
    assert element1.attributes.elements[0].value as base_string == "foo";

    element2 : empty_element.parse("<a class='foo' href='https://theideal.org/'/>");
    assert element2.get_id == text_library.A;
    assert element2.children is null;
    assert element2.attributes.size == 2;
    attributes : element2.attributes.elements;
    assert attributes[0].key == text_library.CLASS;
    assert attributes[0].value as base_string == "foo";
    assert attributes[1].key == text_library.HREF;
    assert attributes[1].value as base_string == "https://theideal.org/";
  }

  testcase test_content() {
    content : make_grammar().content;

    assert content("  hello, world!");
    assert content("&lt;html&gt;");
    assert !content("<bar>");
  }

  testcase test_simple_parse() {
    document_matcher : make_grammar().document_matcher;

    assert document_matcher("<html>foo</html>");
    assert document_matcher("  <html>foo</html>  ");
    assert document_matcher("  <html  >foo</html  >  ");
    assert document_matcher("  <html  >Hello &amp; goodbye!</html  >  ");
    assert document_matcher("  <html  />  ");
    assert document_matcher("<html/>");

    assert document_matcher("  <html>Hello <em>world!</em></html>  ");
    assert document_matcher("  <html><body ><p>Hello <em >world!</em ></p></body ></html>  ");
    assert document_matcher("  <html><body > <p>Hello<br />world!</p> </body ></html>  ");

    assert document_matcher("  <html><body > Hello &lt;world!&gt; </body ></html>  ");
    assert document_matcher("<html><p class='klass'>foo</p></html>");
    assert document_matcher("<html><a class = 'klass' href = 'link'>bar</a></html>");
    assert document_matcher("<html><p class = 'value\">==' attr=\"foo'\">foo</p></html>");
    assert document_matcher("<html><p class = '***' attr=\"baz\">foo</p></html>");

    assert !document_matcher(" no markup ");
    assert !document_matcher("  <html>foo  ");
    assert !document_matcher("  <html>foo<bar>  ");
    assert !document_matcher("  <>foo  ");
    assert !document_matcher("  &amp;<html>foo</html>  ");
    assert !document_matcher("<html><p class='klass\">foo</p></html>");
    assert !document_matcher("<html><p class='klass'>foo</p class=\"foo\"></html>");
    assert !document_matcher("<html foo= ><p class='klass'>foo</p></html>");
    assert !document_matcher("<html foo=bar><p class='klass'>foo</p></html>");

    -- TODO: this should fail.
    assert document_matcher("  <abc>foo</def>  ");

    assert matches(document_matcher.parse("  <html>foo</html>  "), "<html>\nfoo\n</html>\n");
  }

  private boolean matches(text_element the_text_element, string expected) {
    the_writer : string_writer.new();
    -- TODO: introduce a parameter to skip newlines.
    the_formatter : markup_formatter.new(the_writer, "");
    the_formatter.write(the_text_element);
    return the_writer.elements() == expected;
  }
}
