-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

test_suite test_doc_grammar {
  implicit import ideal.runtime.texts.text_library;
  import ideal.machine.characters.normal_handler;
  import ideal.machine.channels.string_writer;

  var string error_message;

  doc_grammar make_grammar() {
    return doc_grammar.new(normal_handler.instance);
  }

  test_case test_simple_parse() {
    grammar : make_grammar();
    content_matcher : grammar.content;

    assert content_matcher("<html>foo</html>");
    assert content_matcher("  <html>foo</html>  ");
    assert content_matcher("  <html  >foo</html  >  ");
    assert content_matcher("  <html  >Hello &amp; goodbye!</html  >  ");
    assert content_matcher("  <html  />  ");
    assert content_matcher("<html/>");

    assert content_matcher("  <html>Hello <em>world!</em></html>  ");
    assert content_matcher("  <html><body ><p>Hello <em >world!</em ></p></body ></html>  ");
    assert content_matcher("  <html><body > <p>Hello<br />world!</p> </body ></html>  ");
    assert content_matcher("  <html><body > Hello &lt;world!&gt; </body ></html>  ");

    assert content_matcher("<html><p class='klass'>foo</p></html>");
    assert content_matcher("<html><a class = 'klass' href = 'link'>bar</a></html>");
    assert content_matcher("<html><p class = 'value\">==' attr=\"foo'\">foo</p></html>");
    assert content_matcher("<html><p class = '***' attr=\"baz\">foo</p></html>");

    assert content_matcher(" no markup ");
    assert content_matcher("  &amp;<html>foo</html>  ");

    assert !content_matcher("  <html>foo  ");
    assert !content_matcher("  <html>foo<bar>  ");
    assert !content_matcher("  <>foo  ");
    assert !content_matcher("<html><p class='klass\">foo</p></html>");
    assert !content_matcher("<html><p class='klass'>foo</p class=\"foo\"></html>");
    assert !content_matcher("<html foo= ><p class='klass'>foo</p></html>");
    assert !content_matcher("<html foo=bar><p class='klass'>foo</p></html>");
    assert !content_matcher("  foo| unmatched  ");

    -- TODO: this should fail.
    assert content_matcher("  <abc>foo</def>  ");

    assert matches(content_matcher.parse("  <html>foo</html>  "), "  <html>foo</html>  ");
    assert matches(content_matcher.parse("  <html  >Hello &amp; goodbye!</html  >  "),
        "  <html>Hello &amp; goodbye!</html>  ");
    assert matches(content_matcher.parse("  <html  />  "), "  <html />  ");

    assert matches(content_matcher.parse("  <html>Hello <em>world!</em></html>  "),
        "  <html>Hello <em>world!</em></html>  ");
    assert matches(content_matcher.parse(
        "  <html><body > <p>Hello<br />world!</p> </body ></html>  "),
        "  <html><body> <p>Hello<br />world!</p> </body></html>  ");

    assert matches(content_matcher.parse("<html><p class='klass'>foo</p></html>"),
        "<html><p class='klass'>foo</p></html>");
    assert matches(content_matcher.parse("<html><p id='f&amp;f'>foo</p></html>"),
        "<html><p id='f&amp;f'>foo</p></html>");
    assert matches(content_matcher.parse("<html><a class = 'klass' href = 'link'>bar</a></html>"),
        "<html><a class='klass' href='link'>bar</a></html>");
    assert matches(content_matcher.parse(
        "<html><p class = 'value\">==' id=\"foo'\">foo</p></html>"),
        "<html><p class='value&quot;&gt;==' id='foo&apos;'>foo</p></html>");
    assert matches(content_matcher.parse(
        "<html><p class = '***' id=\"baz\">foo</p></html>"),
        "<html><p class='***' id='baz'>foo</p></html>");

    assert matches(content_matcher.parse(" |<em>Hello</em>, world!| "),
        " <code><em>Hello</em>, world!</code> ");
    assert matches(content_matcher.parse("doc <p class='klass'>foo: |bar|</p>"),
        "doc <p class='klass'>foo: <code>bar</code></p>");
    assert matches(content_matcher.parse("<j class = 'klass' href = 'link'>bar |foo|</j>"),
        "<j class='klass' href='link'>bar <code>foo</code></j>");
    assert matches(content_matcher.parse(
        "<c><p class = 'value\">==' id=\"foo'\">foo</p></c> |bar| "),
        "<c><p class='value&quot;&gt;==' id='foo&apos;'>foo</p></c> <code>bar</code> ");
  }

  test_case test_parse_errors() {
    grammar : make_grammar();
    parser : doc_parser.new(grammar, report_error);

    assert matches_with_error(parser.parse_content("<html>&bug;</html>"), "<html>&_error_;</html>",
        "Unrecognized entity: bug");
    assert matches_with_error(parser.parse_content("<html><foo>Hello!</foo></html>"),
        "<html><_error_>Hello!</_error_></html>", "Unrecognized element name: foo");
    assert matches_with_error(parser.parse_content("<html><b attr=\"value\">Hello!</b></html>"),
        "<html><b _error_='value'>Hello!</b></html>", "Unrecognized attribute name: attr");
    assert matches_with_error(parser.parse_content("<html><a>Hello!</b></html>"),
        "<html><a>Hello!</a></html>", "Mismatched element name: start a, end b");
  }

  private void report_error(string error_message) {
    this.error_message = error_message;
  }

  private boolean matches_with_error(text_fragment the_text_fragment, string expected,
      string expected_error) {
    return matches(the_text_fragment, expected) && error_message == expected_error;
  }

  private boolean matches(text_fragment the_text_fragment, string expected) {
    the_writer : string_writer.new();
    the_formatter : markup_formatter.new(the_writer, "", false);
    the_formatter.write(the_text_fragment);
    return the_writer.elements() == expected;
  }
}
