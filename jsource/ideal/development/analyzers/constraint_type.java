/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
public enum constraint_type implements immutable_data, stringable {
  ON_TRUE,
  ON_FALSE,
  ALWAYS;

  @Override
  public string to_string() {
    return new base_string(toString());
  }
}
