/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

public class scan_state implements deeply_immutable_data {
  public final token token;
  public final int prefix_end;
  public final int end;

  public scan_state(token token, int prefix_end, int end) {
    this.token = token;
    this.prefix_end = prefix_end;
    this.end = end;
  }

  int compare_to(scan_state other) {
    int result = this.prefix_end - other.prefix_end;
    if (result == 0) {
      result = this.end - other.end;
    }
    return result;
  }
}
