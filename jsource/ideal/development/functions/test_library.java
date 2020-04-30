/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.functions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;

public class test_library {

  private static simple_name PRINTLN_NAME = simple_name.make(new base_string("println"));
  private static simple_name PLUS_NAME = simple_name.make(new base_string("plus"));

  public static void init(analysis_context context, type parent) {
    position pos = semantics.BUILTIN_POSITION;
    context.add(parent, PRINTLN_NAME, new info_fn(PRINTLN_NAME).to_action(pos));
    context.add(parent, PLUS_NAME, new add_op().to_action(pos));
  }
}
