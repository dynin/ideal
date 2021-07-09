-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- JSON tokens that are produced by the tokenizer.
enum json_token {
  extends deeply_immutable data;
  implements deeply_immutable reference_equality, stringable;

  OPEN_BRACKET('[');
  CLOSE_BRACKET(']');

  character token;

  private json_token(character token) {
    this.token = token;
  }
}
