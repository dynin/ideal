/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
public class datastore_schema {
  public static final String DATA_ID = "data_id";

  public static final simple_name NAME = simple_name.make("name");
  public static final simple_name PUBLISH = simple_name.make("publish");
  public static final simple_name VERSION = simple_name.make("version");

  public final type_declaration declaration;
  public final source_content source;
  private final common_library library;
  public final execution_context exec_context;
  private final variable_id data_id_field;
  private final list<data_type> data_types;
  private final list<enum_type> enum_types;
  public string version;

  public datastore_schema(type_declaration declaration, source_content source,
      common_library library, execution_context exec_context) {
    this.declaration = declaration;
    this.source = source;
    this.library = library;
    this.exec_context = exec_context;

    // TODO: reference should be of immutable flavor
    data_id_field = new simple_variable_id(DATA_ID, library.immutable_string_type(),
        library.get_reference(flavors.immutable_flavor, library.immutable_string_type()));

    data_types = new base_list<data_type>();
    enum_types = new base_list<enum_type>();

    readonly_list<type_declaration> type_declarations =
        declaration_util.get_declared_types(declaration);
    for (int i = 0; i < type_declarations.size(); ++i) {
      type_declaration the_type_declaration = type_declarations.get(i);
      principal_type the_declared_type = the_type_declaration.get_declared_type();
      if (is_data_type(the_declared_type)) {
        data_type dt = new data_type(the_type_declaration, this);
        data_types.append(dt);
      } else if (is_enum_type(the_declared_type)) {
        enum_type et = new enum_type(the_type_declaration);
        enum_types.append(et);
      }
    }

    version = new base_string("unknown version");
    readonly_list<variable_declaration> vars =
        declaration_util.get_declared_variables(declaration);
    for (int i = 0; i < vars.size(); ++i) {
      variable_declaration var_decl = vars.get(i);
      if (utilities.eq(var_decl.short_name(), VERSION) && var_decl instanceof variable_analyzer) {
        action version_action = ((variable_analyzer) var_decl).get_init_action();
        if (version_action != null) {
          entity_wrapper version_value = version_action.execute(exec_context);
          if (version_value instanceof string_value) {
            version = ((string_value) version_value).unwrap();
            break;
          }
        }
      }
    }
  }

  public string source_content() {
    return source.content;
  }

  public action_name short_name() {
    return declaration.short_name();
  }

  public datastore_state new_value(@Nullable string source_version, @Nullable string version_id) {
    return new datastore_state(this, source_version, version_id);
  }

  public type_id immutable_string_type() {
    return library.immutable_string_type();
  }

  public type get_mutable_reference(type_id value_type) {
    return library.get_reference(flavors.mutable_flavor, action_utilities.to_type(value_type));
  }

  public variable_id data_id_field() {
    return data_id_field;
  }

  public boolean is_data_type(type_id the_type_id) {
    return ((type) the_type_id).principal().get_kind() == type_kinds.datatype_kind;
  }

  public boolean is_enum_type(type_id the_type_id) {
    return ((type) the_type_id).principal().get_kind() == type_kinds.enum_kind;
  }

  public boolean is_list_type(type_id the_type_id) {
    principal_type candidate = ((type) the_type_id).principal();
    if (candidate instanceof parametrized_type) {
      master_type the_master_type = ((parametrized_type) candidate).get_master();
      return the_master_type == library.list_type();
    }
    return false;
  }

  public type_id get_element_type(type_id the_type_id) {
    principal_type list_principal = ((type) the_type_id).principal();
    assert list_principal instanceof parametrized_type;
    parametrized_type list_parametrized = (parametrized_type) list_principal;
    assert list_parametrized.get_master() == library.list_type();
    immutable_list<abstract_value> list_parameters =
        list_parametrized.get_parameters().fixed_size_list();
    assert list_parameters.size() == 1;
    return (type) list_parameters.get(0);
  }

  public string_value new_string(string s) {
    return new base_string_value(s, library.immutable_string_type());
  }

  public datastore_state make_new_state() {
    return new datastore_state(this, version, null);
  }

  public data_type lookup_data_type(string type_name) {
    for (int i = 0; i < data_types.size(); ++i) {
      data_type the_data_type = data_types.get(i);
      if (utilities.eq(the_data_type.get_short_name(), type_name)) {
        return the_data_type;
      }
    }
    return null;
  }

  public readonly_list<data_type> all_data_types() {
    return data_types;
  }

  public readonly_list<enum_type> all_enum_types() {
    return enum_types;
  }

  public data_type get_data_type(type_id the_type_id) {
    assert is_data_type(the_type_id);
    // TODO: use a hashmap.
    for (int i = 0; i < data_types.size(); ++i) {
      data_type the_data_type = data_types.get(i);
      if (utilities.eq(the_data_type.value_type(), the_type_id)) {
        return the_data_type;
      }
    }
    utilities.panic("Can't find a data type with id " + the_type_id);
    return null;
  }

  public enum_type get_enum_type(type_id the_type_id) {
    assert is_enum_type(the_type_id);
    // TODO: use a hashmap.
    for (int i = 0; i < enum_types.size(); ++i) {
      enum_type the_enum_type = enum_types.get(i);
      if (utilities.eq(the_enum_type.value_type(), the_type_id)) {
        return the_enum_type;
      }
    }
    utilities.panic("Can't find a enum type with id " + the_type_id);
    return null;
  }
}
