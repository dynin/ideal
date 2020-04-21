/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;
import ideal.development.names.*;

public class enum_value extends base_data_value implements identifier {

  private variable_declaration the_declaration;
  private int ordinal;

  public enum_value(variable_declaration the_declaration, int ordinal, type bound) {
    super(bound);
    this.the_declaration = the_declaration;
    this.ordinal = ordinal;
    assert the_declaration.short_name() instanceof simple_name;
  }

  public simple_name short_name() {
    return (simple_name) the_declaration.short_name();
  }

  public declaration get_declaration() {
    return the_declaration;
  }

  public string to_string() {
    return short_name().to_string();
  }
}
