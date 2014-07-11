/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.comments;

import ideal.library.elements.*;

public enum comment_type implements deeply_immutable_data {
  NEWLINE(false),
  WHITESPACE(false),
  LINE_COMMENT(false),
  BLOCK_COMMENT(false),
  LINE_DOC_COMMENT(true),
  BLOCK_DOC_COMMENT(true);

  public final boolean is_doc;

  private comment_type(boolean is_doc) {
    this.is_doc = is_doc;
  }
}
