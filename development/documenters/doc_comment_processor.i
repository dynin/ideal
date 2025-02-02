-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class doc_comment_processor {
  import ideal.machine.characters.unicode_handler;

  static var string or null saved_error;

  public static text_fragment parse(string source) {
    parser : doc_parser.new(get_grammar(), report_error);
    result : parser.parse_content(source);
    assert saved_error is null;
    return result;
  }

  public static cache_result doc_grammar get_grammar() {
    return doc_grammar.new(unicode_handler.instance);
  }

  private static report_error(string error_message) {
    saved_error = error_message;
  }
}
