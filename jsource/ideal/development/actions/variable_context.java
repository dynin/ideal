/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.declarations.*;

/**
 * Describes the context for accessing variables.
 * Can refer tom local stack, static frame, or composite object state.
 */
public interface variable_context extends value {
  void put_var(variable_id key, value_wrapper value);
  value_wrapper get_var(variable_id key);
}
