/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;
import ideal.development.types.*;

public class constraint extends debuggable implements readonly_data, stringable {

  public final variable_declaration the_declaration;
  public final action the_action;
  public final constraint_type the_constraint_type;

  public constraint(variable_declaration the_declaration, action the_action,
      constraint_type the_constraint_type) {
    this.the_declaration = the_declaration;
    this.the_action = the_action;
    this.the_constraint_type = the_constraint_type;
  }

  public final abstract_value the_value() {
    return the_action.result();
  }

  public variable_category get_category() {
    return the_declaration.get_category();
  }

  public boolean is_local() {
    return get_category() == variable_category.LOCAL;
  }

  @Override
  public string to_string() {
    //return utilities.describe(this, the_declaration);
    string result = (the_action instanceof type_action) ?
        ((type_action) the_action).get_type().to_string() : the_action.to_string();
    return new base_string(the_declaration.to_string(), new base_string(" => "),
        result, new base_string(" (" + the_constraint_type + ")"));
  }
}
