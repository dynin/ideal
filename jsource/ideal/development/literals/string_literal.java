/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.literals;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;
import ideal.machine.channels.string_writer;
import ideal.development.elements.*;
import ideal.development.names.*;

public class string_literal extends debuggable implements literal<string> {

  private final string value;
  public final immutable_list<literal_fragment> content;
  public final quote_type quote;

  public string_literal(string value, immutable_list<literal_fragment> content,
      quote_type quote) {
    this.value = value;
    this.content = content;
    this.quote = quote;
  }

  public string_literal(string value, quote_type quote) {
    this.value = value;
    this.content = escape_content(value);
    this.quote = quote;
  }

  @Override
  public string the_value() {
    return value;
  }

  // TODO: cache result
  public string content_with_escapes() {
    string_writer the_string_writer = new string_writer();
    for (int i = 0; i < content.size(); ++i) {
      the_string_writer.write_all(content.get(i).to_string());
    }
    return the_string_writer.elements();
  }

  @Override
  public string to_string() {
    return content_with_escapes();
  }

  // TODO: this is a temporary workaround until a quoting framework is developed
  private static immutable_list<literal_fragment> escape_content(string input) {
    list<literal_fragment> result = new base_list<literal_fragment>();
    int start_index = 0;
    for (int index = 0; index < input.size(); ++index) {
      char the_character = input.get(index);
      readonly_list<quoted_character> quoted_list = quoted_character.java_list;
      for (int quoted_index = 0; quoted_index < quoted_list.size(); quoted_index += 1) {
        quoted_character quoted = quoted_list.get(quoted_index);
        if (the_character == quoted.value_character) {
          if (start_index < index) {
            result.append(new string_fragment(input.slice(start_index, index)));
          }
          result.append(new quoted_fragment(quoted));
          start_index = index + 1;
          break;
        }
      }
    }
    if (start_index < input.size()) {
      result.append(new string_fragment(input.skip(start_index)));
    }
    return result.frozen_copy();
  }
}
