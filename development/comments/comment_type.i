-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

enum comment_type {
  implements deeply_immutable data, reference_equality;

  NEWLINE: new(false);
  WHITESPACE: new(false);
  LINE_COMMENT: new(false);
  BLOCK_COMMENT: new(false);
  LINE_DOC_COMMENT: new(true);
  BLOCK_DOC_COMMENT: new(true);

  final boolean is_doc;

  private comment_type(boolean is_doc) {
    this.is_doc = is_doc;
  }
}
