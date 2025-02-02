-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

enum comment_type {
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
