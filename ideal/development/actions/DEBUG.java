/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.types.*;

import javax.annotation.Nullable;

public interface DEBUG {

  boolean trace = true;

  boolean not_found = trace;

  action_name trace_name = simple_name.make("foo_bar_baz");
      // special_name.IMPLICIT;

  boolean in_progress_declaration = false;
}
