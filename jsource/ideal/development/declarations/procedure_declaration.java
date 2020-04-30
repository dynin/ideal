/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.declarations;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import javax.annotation.Nullable;

public interface procedure_declaration extends declaration {
  simple_name original_name();
  action_name short_name();
  // TODO: deprecate has_errors(), or move to modifiers...
  boolean has_errors();
  annotation_set annotations();
  procedure_category get_category();
  type_flavor get_flavor();
  type get_return_type();
  principal_type declared_in_type();
  readonly_list<type> get_argument_types();
  type get_procedure_type();
  readonly_list<variable_declaration> get_parameter_variables();
  boolean overrides_variable();
  readonly_list<declaration> get_overriden();
  procedure_declaration specialize(specialization_context context, principal_type new_parent);
  @Nullable action get_body_action();
  variable_declaration get_this_declaration();
}
