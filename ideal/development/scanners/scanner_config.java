/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public interface scanner_config {
  boolean is_whitespace(char c);
  boolean is_name_start(char c);
  boolean is_name_part(char c);
  readonly_list<scanner_element> elements();
  token process_token(token the_token);
}
