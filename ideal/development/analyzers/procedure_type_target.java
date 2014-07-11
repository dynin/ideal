/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.actions.*;
public class procedure_type_target extends debuggable implements action_target {
  public static final procedure_type_target instance = new procedure_type_target();

  private master_type procedure_type() {
    return common_library.get_instance().procedure_type();
  }

  @Override
  public boolean matches(abstract_value the_abstract_value) {
    principal_type the_principal = the_abstract_value.type_bound().principal();
    type_utilities.prepare(the_principal, declaration_pass.TYPES_AND_PROMOTIONS);
    return the_principal instanceof parametrized_type &&
           ((parametrized_type) the_principal).get_master() == procedure_type();

  }

  @Override
  public string to_string() {
    return utilities.describe(this);
  }
}
