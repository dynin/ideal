-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- JSON tokens that are produced by the tokenizer.
enum json_token {
  OPEN_BRACE: new('{');
  CLOSE_BRACE: new('}');
  OPEN_BRACKET: new('[');
  CLOSE_BRACKET: new(']');
  COMMA: new(',');
  COLON: new(':');

  character the_character;

  private json_token(character the_character) {
    this.the_character = the_character;
  }
}
