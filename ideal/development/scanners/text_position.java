/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.development.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class text_position implements deeply_immutable_data, position, stringable {
  @dont_display
  public final source_content source;
  @dont_display
  public final int begin;
  @dont_display
  public final int end;

  public text_position(source_content source, int begin, int end) {
    this.source = source;
    this.begin = begin;
    this.end = end;
    assert begin >= 0 && end >= begin && source.content.size() >= end;
  }

  /*
  public String input() {
    return source.content;
  }
  */

  public position source_position() {
    return source;
  }

  string image() {
    return source.content.slice(begin, end);
  }

  public string to_string() {
    return new base_string("(" + begin + "-" + end + ")");
  }
}
