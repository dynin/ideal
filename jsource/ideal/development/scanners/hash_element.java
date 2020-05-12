/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.literals.*;
import ideal.development.notifications.*;

public class hash_element implements scanner_element {
  private final punctuation_type the_token_type;
  private final scanner_config config;
  private final char hash_character;

  private static String ID_PREFIX = "id:";

  public hash_element(punctuation_type the_token_type, scanner_config config) {
    this.the_token_type = the_token_type;
    this.config = config;
    string name = this.the_token_type.name();
    assert name.size() == 1;
    hash_character = name.first();
  }

  @Override
  public scan_state process(source_content source, int begin) {
    String input = utilities.s(source.content);
    if (input.charAt(begin) != hash_character) {
      return null;
    }
    int end = begin + 1;

    if (input.substring(end).startsWith(ID_PREFIX)) {
      int id_begin = end + ID_PREFIX.length();
      if (input.length() > id_begin && config.is_name_start(input.charAt(id_begin))) {
        int id_end = id_begin + 1;
        for (; id_end < input.length(); ++id_end) {
          if (!config.is_name_part(input.charAt(id_end))) {
            break;
          }
        }
        origin pos = source.make_origin(begin, id_end);
        string image = source.content.slice(id_begin, id_end);
        simple_name token_as_name = simple_name.make(image);
        return new scan_state(new base_token<simple_name>(
            special_token_type.SIMPLE_NAME, token_as_name, pos), id_end, id_end);
      }
    }

    origin pos = source.make_origin(begin, end);
    String image = input.substring(begin, end);
    return new scan_state(
        new base_token<string>(the_token_type, new base_string(image), pos), end, end);
  }
}
