-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Names (identifiers, operators, keywords...) used in the ideal system.
package names {
  implicit import ideal.library.elements;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.logs;
  implicit import ideal.development.elements;
  import ideal.development.symbols.base_symbols;

  class base_token_type;
  class punctuation_type;
  class operator_type;
  namespace punctuation;
  class operator;
  namespace keyword;
--  interface special_name;
--  interface special_token_type;
  namespace name_utilities;
}
