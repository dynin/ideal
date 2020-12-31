/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.reflections;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.scanners.*;
import ideal.development.types.*;
import ideal.development.actions.*;
import ideal.development.analyzers.*;
import ideal.development.flavors.*;
import ideal.development.kinds.*;
import ideal.development.values.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;
public class enum_type {
  private type_declaration declaration;
  private final list<enum_value> values;

  public enum_type(type_declaration declaration) {
    this.declaration = declaration;
    assert declaration.get_kind() == type_kinds.enum_kind;
    values = new base_list<enum_value>();
    readonly_list<variable_declaration> variables =
        declaration_util.get_declared_variables(declaration);
    int ordinal = 0;
    for (int i = 0; i < variables.size(); ++i) {
      variable_declaration the_variable = variables.get(i);
      if (the_variable.get_category() == variable_category.ENUM_VALUE) {
        enum_value value = new enum_value(the_variable, ordinal, declaration.get_declared_type());
        values.append(value);
        ordinal += 1;
      }
    }
    assert !values.is_empty();
  }

  public action_name short_name() {
    return declaration.short_name();
  }

  public type value_type() {
    return declaration.get_declared_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  public readonly_list<enum_value> get_values() {
    return values;
  }
}
