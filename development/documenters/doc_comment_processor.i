-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class doc_comment_processor {
  import ideal.machine.characters.normal_handler;
  import ideal.machine.channels.string_writer;

  static var doc_grammar or null the_grammar;
  static var string or null saved_error;

  public static text_fragment parse(string source) {
    parser : doc_parser.new(get_grammar(), report_error);
    result : parser.parse_content(source);
    assert saved_error is null;
    return result;
  }

  -- Lazily initialize grammar
  private static doc_grammar get_grammar() {
    if (the_grammar is null) {
      result : doc_grammar.new(normal_handler.instance);
      the_grammar = result;
      return result;
    } else {
      return the_grammar !> doc_grammar;
    }
  }

  private static void report_error(string error_message) {
    saved_error = error_message;
  }
}
