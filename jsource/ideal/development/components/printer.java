/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.components;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.development.elements.*;

public interface printer extends value {
  text_fragment print(construct c);
  text_fragment print_space();
  text_fragment print_simple_name(simple_name name);
  text_fragment print_line(text_fragment fragment);
  text_fragment print_word(token_type word);
  text_fragment print_punctuation(token_type punct);
  text_fragment print_indented_statement(construct c);
  text_fragment print_grouping_in_statement(text_fragment text);
}
