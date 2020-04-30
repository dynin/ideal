/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.webforms;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.showcase.coach.forms.*;
public interface render_context {
  string to_uri(procedure0 action);
  string to_button_id(procedure0 action);
}
