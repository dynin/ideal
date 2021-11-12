-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.origins.*;

public class string_token_element<P extends deeply_immutable_data> extends base_scanner_element {
  protected final punctuation_type the_punctuation;
  protected final token_type the_type;
  protected final P the_payload;

  public string_token_element(punctuation_type the_punctuation, token_type the_type, P the_payload) {
    this.the_punctuation = the_punctuation;
    this.the_type = the_type;
    this.the_payload = the_payload;
  }

  @Override
  public scan_state process(source_content source, int begin) {
    String input = utilities.s(source.content);
    if (!input.substring(begin).startsWith(utilities.s(the_punctuation.name()))) {
      return null;
    }
    int end = begin + the_punctuation.name().size();
    origin pos = source.make_origin(begin, end);
    return new scan_state(new base_token<P>(the_type, the_payload, pos), end, end);
  }
}
