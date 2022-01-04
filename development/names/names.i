-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Names (identifiers, operators, keywords...) used in the ideal system.
package names {
  implicit import ideal.library.elements;
  implicit import ideal.library.characters;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.logs;
  implicit import ideal.development.elements;
  import ideal.machine.characters.unicode_handler;

  class base_token_type;
  class punctuation_type;
  class quote_type;
  namespace special_token_type;
  class operator_type;
  enum precedence;
  namespace punctuation;
  class operator;
  class cast_type;
  class keyword;
  namespace keywords;
  class special_name;
  namespace common_names;
  namespace name_utilities;

  test_suite test_names;
}
