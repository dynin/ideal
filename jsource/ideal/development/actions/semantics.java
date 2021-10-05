/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.modifiers.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;
import ideal.development.origins.*;

public interface semantics extends data {

  access_modifier get_default_type_access(kind the_kind);

  access_modifier get_default_variable_access(kind the_kind);

  access_modifier get_default_procedure_access(kind the_kind);

  static final origin BUILTIN_POSITION = new special_origin(new base_string("[builtin]"));
}
