/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.elements;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;

public interface token<P extends deeply_immutable_data>
    extends deeply_immutable_data, position, convertible_to_string {
  token_type type();
  P payload();
}
