-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Implementation of origins; printers and utilities.
package origins {
  implicit import ideal.library.elements;
  implicit import ideal.library.texts;
  implicit import ideal.library.channels;
  implicit import ideal.library.resources;
  implicit import ideal.library.patterns;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.texts;
  implicit import ideal.runtime.patterns;
  implicit import ideal.runtime.logs;
  implicit import ideal.development.elements;
  implicit import ideal.development.names;

  class special_origin;
  class source_content;
  class text_origin;
  class fragment_origin;
  namespace origin_utilities;
  namespace origin_printer;

  test_suite test_origin_printer;
}
