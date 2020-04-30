/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.notifications;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.scanners.*;

public class position_util {

  public static @Nullable source_content get_source(position pos) {
    while (pos != null) {
      if (pos instanceof source_content) {
        return (source_content) pos;
      }
      pos = pos.source_position();
    }
    return null;
  }

  public static string get_source_prefix(position the_position) {
    while (the_position != null) {
      if (the_position instanceof text_position) {
        text_position in_text = (text_position) the_position;
        source_content source = in_text.source;;
        int line_number = source.line_number(in_text);
        return new base_string(source.name.to_string().toString(), ":",
            String.valueOf(line_number), ": ");
      } else if (the_position instanceof source_content) {
        source_content source = (source_content) the_position;
        return new base_string(source.name.to_string(), ": ");
      }
      the_position = the_position.source_position();
    }
    return new base_string("");
  }
}
