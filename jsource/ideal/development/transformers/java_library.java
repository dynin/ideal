/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.transformers;

import javax.annotation.Nullable;
import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.actions.*;
import ideal.development.analyzers.*;
import ideal.development.declarations.*;
import ideal.development.modifiers.*;
import ideal.development.origins.*;

public class java_library implements value {

  public static final simple_name runtime_name = simple_name.make(new base_string("runtime"));
  public static final simple_name elements_name = simple_name.make(new base_string("elements"));
  public static final simple_name machine_name = simple_name.make(new base_string("machine"));
  public static final simple_name adapters_name = simple_name.make(new base_string("adapters"));

  private static java_library instance;

  private type_bootstrapper bootstrapper;
  private analysis_context context;

  private principal_type java_package;
  private principal_type builtins_package;
  private principal_type lang_package;

  private principal_type int_type;
  private principal_type boolean_type;
  private principal_type char_type;

  private principal_type object_type;
  private principal_type string_type;

  private principal_type javax_package;
  private principal_type annotation_package;
  private principal_type nullable_type;

  private principal_type runtime_namespace;
  private principal_type runtime_elements_namespace;
  private principal_type json_data_class;

  private principal_type machine_namespace;
  private principal_type machine_elements_namespace;
  private principal_type machine_adapters_namespace;
  private principal_type runtime_util_class;
  private principal_type array_class;

  private principal_type machine_annotations_namespace;
  private principal_type dont_display_type;

  private final dictionary<principal_type, principal_type> primitive_mapping;
  private final dictionary<principal_type, simple_name> wrapper_mapping;

  private java_library(type_bootstrapper bootstrapper, analysis_context the_analysis_context) {
    this.bootstrapper = bootstrapper;

    primitive_mapping = new list_dictionary<principal_type, principal_type>();
    wrapper_mapping = new list_dictionary<principal_type, simple_name>();

    context = the_analysis_context;

    runtime_namespace = get_namespace(runtime_name, common_types.ideal_namespace());
    runtime_elements_namespace = get_namespace(elements_name, runtime_namespace);

    machine_namespace = get_namespace(machine_name, common_types.ideal_namespace());
    machine_adapters_namespace = get_namespace(adapters_name, machine_namespace);
  }

  private void bootstrap_if_needed() {
    if (java_package != null) {
      return;
    }

    if (machine_adapters_namespace.get_declaration() == null) {
      bootstrapper.bootstrap_type(machine_adapters_namespace, analysis_pass.TYPE_DECL);
    }

    java_package = get_type(machine_adapters_namespace, "java");
    builtins_package = get_type(java_package, "builtins");
    lang_package = get_type(java_package, "lang");

    int_type = get_type(builtins_package, "int");
    boolean_type = get_type(builtins_package, "boolean");
    char_type = get_type(builtins_package, "char");

    object_type = get_type(lang_package, "Object");
    string_type = get_type(lang_package, "String");

    javax_package = get_type(machine_adapters_namespace, "javax");
    annotation_package = get_type(javax_package, "annotation");
    nullable_type = get_type(annotation_package, "Nullable");

    add_mapping(common_types.boolean_type(), boolean_type(), "Boolean");
    add_mapping(common_types.character_type(), char_type(), "Character");
    add_mapping(common_types.integer_type(), int_type(), "Integer");
    add_mapping(common_types.nonnegative_type(), int_type(), "Integer");
    add_mapping(common_types.void_type(), common_types.void_type(), "Void");
  }

  private void add_mapping(principal_type ideal_type, principal_type java_type,
      String java_wrapper_name) {
    primitive_mapping.put(ideal_type, java_type);
    wrapper_mapping.put(ideal_type, simple_name.make(java_wrapper_name));
  }

  public boolean is_mapped(principal_type the_type) {
    return primitive_mapping.contains_key(the_type);
  }

  public @Nullable principal_type map_to_primitive(principal_type the_type) {
    return primitive_mapping.get(the_type);
  }

  public @Nullable simple_name map_to_wrapper(principal_type the_type) {
    return wrapper_mapping.get(the_type);
  }

  public principal_type runtime_elements_namespace() {
    return runtime_elements_namespace;
  }

  public principal_type java_package() {
    return java_package;
  }

  public principal_type javax_package() {
    return javax_package;
  }

  public principal_type int_type() {
    return int_type;
  }

  public principal_type boolean_type() {
    return boolean_type;
  }

  public principal_type char_type() {
    return char_type;
  }

  public principal_type object_type() {
    return object_type;
  }

  public principal_type string_type() {
    return string_type;
  }

  public principal_type adapters_namespace() {
    return machine_adapters_namespace;
  }

  public principal_type builtins_package() {
    return builtins_package;
  }

  public principal_type lang_package() {
    return lang_package;
  }

  public principal_type nullable_type() {
    return nullable_type;
  }

  public principal_type dont_display_type() {
    if (dont_display_type == null) {
      machine_annotations_namespace = get_type(machine_namespace, "annotations");
      dont_display_type = get_type(machine_annotations_namespace, "dont_display");
    }

    return dont_display_type;
  }

  public principal_type machine_namespace() {
    return machine_namespace;
  }

  public principal_type machine_adapters_namespace() {
    return machine_adapters_namespace;
  }

  public principal_type machine_elements_namespace() {
    if (machine_elements_namespace == null) {
      machine_elements_namespace = get_type(machine_namespace, "elements");
    }
    return machine_elements_namespace;
  }

  public principal_type runtime_util_class() {
    if (runtime_util_class == null) {
      runtime_util_class = get_type(machine_elements_namespace(), "runtime_util");
    }
    return runtime_util_class;
  }

  public principal_type json_data_class() {
    if (json_data_class == null) {
       json_data_class = action_utilities.lookup_type(context,
           new base_string("ideal.library.formats.json_data"));
    }
    return json_data_class;
  }

  public principal_type array_class() {
    if (array_class == null) {
      array_class = get_type(machine_elements_namespace(), "array");
    }
    return array_class;
  }

  private principal_type get_namespace(simple_name name, principal_type parent) {
    readonly_list<type> types = action_utilities.lookup_types(context, parent, name);
    if (types.size() == 1) {
      return (principal_type) types.first();
    }
    master_type result = action_utilities.make_type(context, type_kinds.namespace_kind,
        flavor_profiles.nameonly_profile, name, parent, null, origin_utilities.builtin_origin);
    return result;
  }

  // TODO: this code is duplicated, factor out.
  private principal_type get_type(principal_type parent, String sname) {
    simple_name name = simple_name.make(new base_string(sname));
    analyzer_utilities.analyze_and_prepare(parent);
    readonly_list<type> types = action_utilities.lookup_types(context, parent, name);
    if (types.size() != 1) {
      utilities.panic("Got " + types.size() + " for " + sname);
      assert types.size() == 1;
    }
    principal_type result = (principal_type) types.first();
    type_utilities.prepare(result, declaration_pass.FLAVOR_PROFILE);
    return result;
  }

  public static void bootstrap_on_demand(type_bootstrapper bootstrapper,
      analysis_context the_analysis_context) {
    assert instance == null;
    instance = new java_library(bootstrapper, the_analysis_context);
  }

  public static java_library get_instance() {
    assert instance != null;
    instance.bootstrap_if_needed();

    return instance;
  }

  public static boolean is_java_type(type the_type) {
    assert instance != null;

    principal_type the_principal_type = the_type.principal();
    while (the_principal_type != null) {
      if (the_principal_type == instance.machine_adapters_namespace) {
        return true;
      }
      the_principal_type = the_principal_type.get_parent();
    }

    return false;
  }
}
