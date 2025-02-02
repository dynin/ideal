/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.functions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;
import ideal.development.flavors.*;
import ideal.development.modifiers.*;
import ideal.development.declarations.*;

public class string_helper {

  private string_helper() { }

  public static type readonly_stringable() {
    return common_types.stringable_type().get_flavored(flavor.readonly_flavor);
  }

  public static string to_string(entity_wrapper the_entity, execution_context the_context) {
    assert the_entity instanceof value_wrapper;
    value_wrapper the_value = (value_wrapper) the_entity;
    assert action_utilities.is_of(the_value, readonly_stringable());

    if (the_value instanceof base_constant_value) {
      return ((base_constant_value) the_value).constant_to_string();
    }

    type_declaration the_declaration = (type_declaration)
        action_utilities.to_type(the_value.type_bound()).principal().get_declaration();
    @Nullable procedure_declaration method = lookup_method(the_declaration,
        common_names.to_string_name);
    if (method == null) {
      return new base_string("Not found to_string() in " + the_value.type_bound());
    }

    entity_wrapper result = action_utilities.execute_procedure(method, the_value,
        new empty<entity_wrapper>(), the_context);
    assert result instanceof string_value;
    return ((string_value) result).unwrap();
  }

  private static @Nullable procedure_declaration lookup_method(type_declaration the_declaration,
        simple_name method_name) {
    readonly_list<procedure_declaration> declared_procedures =
        declaration_util.get_declared_procedures(the_declaration);
    for (int i = 0; i < declared_procedures.size(); ++i) {
      procedure_declaration the_procedure = declared_procedures.get(i);
      if (the_procedure.annotations().has(general_modifier.static_modifier)) {
        continue;
      }
      if (utilities.eq(the_procedure.short_name(), method_name) &&
          the_procedure.get_argument_types().is_empty()) {
        return the_procedure;
      }
    }
    return null;
  }
}
