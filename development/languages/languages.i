-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Declarations that depend on the target language.
package languages {
  implicit import ideal.library.elements;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.logs;
  implicit import ideal.development.elements;
  implicit import ideal.development.names;
  implicit import ideal.development.constructs;
  implicit import ideal.development.modifiers;
  implicit import ideal.development.modifiers.general_modifier;

  namespace java_language;
  namespace javascript_language;
  class annotation_list_order;
}
