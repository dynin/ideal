/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.origins;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;

public class origin_utilities {

  public static @Nullable source_content get_source(origin pos) {
    while (pos != null) {
      if (pos instanceof source_content) {
        return (source_content) pos;
      }
      pos = pos.deeper_origin();
    }
    return null;
  }

  public static string get_source_prefix(origin the_origin) {
    while (the_origin != null) {
      if (the_origin instanceof text_origin) {
        text_origin in_text = (text_origin) the_origin;
        source_content source = in_text.source;;
        int line_number = source.line_number(in_text);
        return new base_string(source.name.to_string().toString(), ":",
            String.valueOf(line_number), ": ");
      } else if (the_origin instanceof source_content) {
        source_content source = (source_content) the_origin;
        return new base_string(source.name.to_string(), ": ");
      }
      the_origin = the_origin.deeper_origin();
    }
    return new base_string("");
  }
}
