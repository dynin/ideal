-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Scanner (lexer) infrastructure.
package scanners {
  implicit import ideal.library.elements;
  implicit import ideal.library.characters;
  implicit import ideal.library.patterns;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.characters;
  implicit import ideal.runtime.patterns;
  implicit import ideal.runtime.logs;
  implicit import ideal.machine.characters;
  import ideal.machine.channels.string_writer;
  implicit import ideal.development.elements;
  implicit import ideal.development.names;
  implicit import ideal.development.notifications;
  implicit import ideal.development.origins;
  implicit import ideal.development.comments;
  implicit import ideal.development.literals;
  implicit import ideal.development.modifiers;
  import ideal.development.constructs.constraint_category;
  import ideal.development.jumps.jump_category;

  interface scanner_config;
  interface scanner_element;
  class scan_state;
  class base_scanner_element;
  class base_token;
  class hash_element;
  class line_comment;
  class punctuation_element;
  class integer_token_element;
  class string_token_element;
  class quoted_token_element;
  class scanner_engine;
  class base_scanner_config;
--  class documenter_filter;
}
