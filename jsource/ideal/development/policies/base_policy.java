/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.policies;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.origins.*;
import ideal.development.declarations.*;
import ideal.development.flags.*;
import ideal.development.actions.*;
import ideal.development.values.*;

public class base_policy implements type_policy {

  public static final base_policy instance = new base_policy();

  @Override
  public signal declare_type(principal_type new_type, declaration_pass pass,
      action_context context) {
    return ok_signal.instance;
  }

  @Override
  public void declare_supertype(principal_type new_type,
      supertype_declaration the_supertype_declaration, action_context context) {
  }
}
