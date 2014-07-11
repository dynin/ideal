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

/**
 * Abstract value denotes a set of values used in abstract interpretation.
 * It can either be a value (e.g. 42) or a type (e.g. integer).
 * There is ordering defined on a type.
 */
public interface abstract_value extends readonly_data, convertible_to_string {
  type type_bound();
  action to_action(position pos);
}
