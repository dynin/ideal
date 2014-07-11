/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.elements;

import ideal.library.elements.*;
import ideal.runtime.elements.*;

public interface kind extends deeply_immutable_data, reference_equality, convertible_to_string {
  simple_name name();
  flavor_profile default_profile();
  boolean is_namespace();
}
