/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.transformers;

import ideal.development.elements.*;
import ideal.development.analyzers.*;

public interface type_bootstrapper {
  void bootstrap_type(principal_type the_type, analysis_pass pass);
}
