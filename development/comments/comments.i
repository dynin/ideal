-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Classes for dealing with whitespace, comments and doc comments.
package comments {
  implicit import ideal.library.elements;
  implicit import ideal.library.texts;
  implicit import ideal.runtime.elements;

  enum comment_type;
  class comment;
  enum documentation_section;
  interface documentation;
  class summary_extractor;
}
