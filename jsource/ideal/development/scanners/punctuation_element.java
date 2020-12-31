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
import ideal.development.origins.*;
import ideal.development.names.*;

public class punctuation_element implements scanner_element {
  protected final punctuation_type the_punctuation;

  public punctuation_element(punctuation_type the_punctuation) {
    this.the_punctuation = the_punctuation;
  }

  @Override
  public scan_state process(source_content source, int begin) {
    String input = utilities.s(source.content);
    if (!input.substring(begin).startsWith(utilities.s(the_punctuation.name()))) {
      return null;
    }
    int end = begin + the_punctuation.name().size();
    origin pos = source.make_origin(begin, end);
    String image = input.substring(begin, end);
    return new scan_state(
        new base_token<string>(the_punctuation, new base_string(image), pos), end, end);
  }
}
