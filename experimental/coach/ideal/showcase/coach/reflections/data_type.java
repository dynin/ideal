/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
public class data_type implements readonly_data {

  private type_declaration declaration;
  private datastore_schema schema;
  private string short_name;
  private final list<variable_id> fields;

  public data_type(type_declaration declaration, datastore_schema schema) {
    this.declaration = declaration;
    this.schema = schema;
    this.short_name = declaration.short_name().to_string();
    fields = new base_list<variable_id>();
  }

  public action_name short_name() {
    return declaration.short_name();
  }

  public string get_type_id() {
    return short_name;
  }

  public type value_type() {
    return declaration.get_declared_type().get_flavored(flavor.mutable_flavor);
  }

  public string get_short_name() {
    return short_name;
  }

  public composite_data_value new_value(datastore_state world) {
    return new composite_data_value(this, world);
  }

  private @Nullable procedure_declaration lookup_method(simple_name method_name) {
    readonly_list<procedure_declaration> declared_procedures =
        declaration_util.get_declared_procedures(declaration);
    for (int i = 0; i < declared_procedures.size(); ++i) {
      procedure_declaration method = declared_procedures.get(i);
      if (method.get_category() == procedure_category.METHOD &&
          utilities.eq(method.short_name(), method_name)) {
        return method;
      }
    }
    return null;
  }

  private @Nullable variable_declaration lookup_field(simple_name field_name) {
    readonly_list<variable_declaration> declared_fields =
        declaration_util.get_declared_variables(declaration);
    for (int i = 0; i < declared_fields.size(); ++i) {
      variable_declaration field = declared_fields.get(i);
      if (field.get_category() == variable_category.INSTANCE &&
          utilities.eq(field.short_name(), field_name)) {
        return field;
      }
    }
    return null;
  }

  private string execute_string_method(procedure_declaration method, composite_data_value this_data_val) {
    entity_wrapper result = action_utilities.execute_procedure(method, this_data_val,
        new empty<entity_wrapper>(), schema.exec_context);
    if (result instanceof error_signal) {
      error_signal error_result = (error_signal) result;
      error_result.cause.report();
      return new base_string("Error: " + error_result);
    }
    assert result instanceof string_value;
    return ((string_value) result).unwrap();
  }

  public string render_name(composite_data_value dv) {
    @Nullable variable_declaration name_field = lookup_field(datastore_schema.NAME);
    string_value the_name = null;
    if (name_field != null) {
      the_name = (string_value) dv.get_var(name_field);
    } else {
      procedure_declaration method = lookup_method(datastore_schema.NAME);
      if (method != null) {
        return execute_string_method(method, dv);
      }
    }

    if (the_name == null) {
      the_name = (string_value) dv.get_var(schema.data_id_field());
    }
    if (the_name != null) {
      return the_name.unwrap();
    } else {
      return new base_string("unknown");
    }
  }

  public boolean supports_publish() {
    return lookup_method(datastore_schema.PUBLISH) != null;
  }

  public String publish(composite_data_value dv) {
    procedure_declaration method = lookup_method(datastore_schema.PUBLISH);
    assert method != null;
    return utilities.s(execute_string_method(method, dv));
  }

  public readonly_list<variable_id> get_fields() {
    // TODO: there is a race condition here in the multithreaded scenario.
    if (fields.is_empty()) {
      readonly_list<variable_declaration> declared_fields =
          declaration_util.get_declared_variables(declaration);
      for (int i = 0; i < declared_fields.size(); ++i) {
        variable_declaration field_declaration = declared_fields.get(i);
        if (field_declaration.get_category() == variable_category.INSTANCE) {
          fields.append(field_declaration);
        }
      }
    }
    return fields;
  }
}
