/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.extensions;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.components.*;
import ideal.development.names.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;
import ideal.development.notifications.*;
import javax.annotation.Nullable;

public abstract class extension_analyzer extends single_pass_analyzer {

  public extension_analyzer(origin the_origin) {
    super(the_origin);
  }

  public abstract analyzable expand();

  protected analysis_result do_single_pass_analysis() {
    analyzable expanded = expand();
    @Nullable error_signal error = find_error(expanded);
    if (error != null) {
      return error;
    }

    return new extension_action(action_not_error(expanded), this);
  }
}