-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Helper types for dealing with structured texts,
--- for use in documenters.
-- TODO: move to runtime.texts?
package texts {
  implicit import ideal.library.elements;
  implicit import ideal.library.texts;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.texts;

  interface text_event;
  class string_event;
  class start_element;
  class end_element;
  class text_rewriter;
}
