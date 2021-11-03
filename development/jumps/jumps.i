-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Jumps, such as panic/break/continue/return.
package jumps {
  implicit import ideal.library.elements;
  implicit import ideal.library.reflections;
  implicit import ideal.runtime.elements;
  implicit import ideal.development.elements;
  implicit import ideal.development.names;
  import ideal.development.types.common_types;

  class jump_wrapper;
  class panic_value;
  class returned_value;
  enum jump_category;
}
