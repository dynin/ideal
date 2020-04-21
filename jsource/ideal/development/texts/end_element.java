/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.texts;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;

/**
 * An event indicating the end of a structured text element.
 */
public class end_element extends debuggable implements text_event {
  private final element_id id;

  public end_element(element_id id) {
    this.id = id;
  }

  public element_id get_id() {
    return id;
  }

  @Override
  public string to_string() {
    return new base_string("</", id.to_string(), ">");
  }
}
