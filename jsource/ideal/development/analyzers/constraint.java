/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
public class constraint extends debuggable implements readonly_data, stringable {

  public final declaration the_declaration;
  public final abstract_value the_value;
  public final constraint_type the_constraint_type;

  public constraint(declaration the_declaration, abstract_value the_value,
      constraint_type the_constraint_type) {
    this.the_declaration = the_declaration;
    this.the_value = the_value;
    this.the_constraint_type = the_constraint_type;
  }

  @Override
  public string to_string() {
    //return utilities.describe(this, the_declaration);
    return new base_string(the_declaration.to_string(), " => ", the_value.to_string());
  }
}
