/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.origins.*;
import ideal.development.declarations.*;

public interface type_policy {
  signal declare_type(principal_type new_type, declaration_pass pass, action_context context);
  void declare_supertype(principal_type new_type, supertype_declaration the_supertype_declaration,
      action_context context);
}
