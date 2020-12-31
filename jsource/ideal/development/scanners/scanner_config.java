/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.modifiers.*;
import ideal.development.names.*;
import ideal.development.origins.*;

public interface scanner_config {
  boolean is_whitespace(char c);
  boolean is_name_start(char c);
  boolean is_name_part(char c);
  readonly_list<scanner_element> elements();
  token process_token(token the_token);

  void add_keyword(keyword the_keyword);
  void add_punctuation(punctuation_type the_punctuation_type);
  void add_special(special_name the_special_name, token_type the_token_type);
  void add_kind(kind kind);
  void add_subtype_tag(subtype_tag tag);
  void add_modifier(modifier_kind modifier);
  void add_flavor(type_flavor flavor);

  readonly_list<token> scan(source_content source);
}
