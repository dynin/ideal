/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.library.reflections.*;
import ideal.library.messages.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;

public class builtin_declaration implements declaration {

  public static final builtin_declaration instance = new builtin_declaration();

  @Override
  public @Nullable origin deeper_origin() {
    return null;
  }

  @Override
  public principal_type declared_in_type() {
    return core_types.root_type();
  }

  @Override
  public boolean has_errors() {
    return false;
  }

  @Override
  public string to_string() {
    return new base_string("<builtin_declaration>");
  }
}
