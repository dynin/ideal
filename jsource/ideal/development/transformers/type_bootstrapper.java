/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.transformers;

import ideal.development.elements.*;
import ideal.development.analyzers.*;

public interface type_bootstrapper {
  void bootstrap_type(principal_type the_type, analysis_pass pass);
}
