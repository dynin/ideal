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
import javax.annotation.Nullable;

public interface position extends readonly_data, reference_equality, stringable {
  @Nullable position source_position();
}
