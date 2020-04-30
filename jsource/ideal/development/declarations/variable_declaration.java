/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.declarations;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.dispatch_action;

public interface variable_declaration extends declaration, variable_id {
  variable_category get_category();
  annotation_set annotations();
  boolean has_errors();
  action_name short_name();
  principal_type declared_in_type();
  /** Get (flavored) variable type. */
  type value_type();
  type reference_type();
  variable_declaration specialize(specialization_context context, principal_type new_parent);
  @Nullable action get_init();
}
