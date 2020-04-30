/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.webforms;

import ideal.library.elements.*;
public class input_id_generator  {

  private int index;

  public String next_escaped_id() {
    return "id" + (index++);
  }
}
