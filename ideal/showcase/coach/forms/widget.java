/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.forms;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
public interface widget {
  <R> R accept(widget_visitor<R> the_visitor);

  widget EMPTY_WIDGET = new label("");
}
