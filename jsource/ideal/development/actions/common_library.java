/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import static ideal.development.names.common_names.*;
import ideal.development.kinds.*;
import static ideal.development.kinds.type_kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import static ideal.development.flavors.flavor_profiles.*;
import ideal.development.declarations.*;

public class common_library implements value {
  private static common_library instance;

  private type_declaration_context context;

  private principal_type ideal_type;
  private principal_type library_type;
  private principal_type elements_type;
  private principal_type operators_type;

  private principal_type VOID_TYPE;
  private principal_type UNDEFINED_TYPE;
  private principal_type ENTITY_TYPE;
  private principal_type VALUE_TYPE;
  private principal_type DATA_TYPE;
  private principal_type INTEGER_TYPE;
  private principal_type NONNEGATIVE_TYPE;
  private principal_type BOOLEAN_TYPE;
  private principal_type CHARACTER_TYPE;
  private principal_type STRING_TYPE;
  private principal_type NULL_TYPE;
  private principal_type MISSING_TYPE;

  private master_type REFERENCE_TYPE;
  private master_type PROCEDURE_TYPE;
  private master_type FUNCTION_TYPE;
  private master_type STRINGABLE_TYPE;
  private master_type EQUALITY_COMPARABLE_TYPE;
  private master_type REFERENCE_EQUALITY_TYPE;
  private master_type LIST_TYPE;

  public common_library(type_declaration_context context) {
    this.context = context;
    elementary_types.set_context(context);

    ideal_type = make_namespace(ideal_name, elementary_types.root_type(), namespace_kind);
    library_type = make_namespace(library_name, ideal_type, namespace_kind);
    elements_type = make_namespace(elements_name, library_type, package_kind);
    operators_type = make_namespace(operators_name, library_type, package_kind);

    VOID_TYPE = get_type("void", singleton_kind, deeply_immutable_profile);
    UNDEFINED_TYPE = get_type("undefined", singleton_kind, deeply_immutable_profile);
    ENTITY_TYPE = get_type("entity", concept_kind, mutable_profile);
    VALUE_TYPE = get_type("value", concept_kind, mutable_profile);
    DATA_TYPE = get_type("data", concept_kind, mutable_profile);
    BOOLEAN_TYPE = get_type("boolean", enum_kind, deeply_immutable_profile);
    CHARACTER_TYPE = get_type("character", datatype_kind, deeply_immutable_profile);
    INTEGER_TYPE = get_type("integer", datatype_kind, deeply_immutable_profile);
    NONNEGATIVE_TYPE = get_type("nonnegative", datatype_kind, deeply_immutable_profile);
    STRING_TYPE = get_type("string", datatype_kind, deeply_immutable_profile);
    NULL_TYPE = get_type("null", interface_kind, deeply_immutable_profile);
    MISSING_TYPE = get_type("missing", singleton_kind, deeply_immutable_profile);

    REFERENCE_TYPE = get_type("reference", reference_kind, mutable_profile);
    REFERENCE_TYPE.make_parametrizable();
    PROCEDURE_TYPE = get_type(procedure_name, procedure_kind, immutable_profile);
    PROCEDURE_TYPE.make_parametrizable();
    FUNCTION_TYPE = get_type(function_name, procedure_kind, deeply_immutable_profile);
    FUNCTION_TYPE.make_parametrizable();
    STRINGABLE_TYPE = get_type("stringable", interface_kind, mutable_profile);
    EQUALITY_COMPARABLE_TYPE = get_type("equality_comparable", concept_kind, mutable_profile);
    REFERENCE_EQUALITY_TYPE = get_type("reference_equality", interface_kind, mutable_profile);
    LIST_TYPE = get_type("list", interface_kind, mutable_profile);
    LIST_TYPE.make_parametrizable();

    assert instance == null;
    instance = this;
  }

  public principal_type void_type() {
    return VOID_TYPE;
  }

  public principal_type undefined_type() {
    return UNDEFINED_TYPE;
  }

  public principal_type entity_type() {
    return ENTITY_TYPE;
  }

  public principal_type value_type() {
    return VALUE_TYPE;
  }

  public principal_type data_type() {
    return DATA_TYPE;
  }

  public principal_type boolean_type() {
    return BOOLEAN_TYPE;
  }

  public principal_type character_type() {
    return CHARACTER_TYPE;
  }

  public principal_type integer_type() {
    return INTEGER_TYPE;
  }

  public principal_type nonnegative_type() {
    return NONNEGATIVE_TYPE;
  }

  public type immutable_void_type() {
    return void_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  public type immutable_boolean_type() {
    return boolean_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  public type immutable_integer_type() {
    return integer_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  public type immutable_nonnegative_type() {
    return nonnegative_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  public type immutable_character_type() {
    return character_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  public principal_type string_type() {
    return STRING_TYPE;
  }

  public type immutable_string_type() {
    return string_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  public master_type stringable_type() {
    return STRINGABLE_TYPE;
  }

  public principal_type equality_comparable_type() {
    return EQUALITY_COMPARABLE_TYPE;
  }

  public principal_type reference_equality_type() {
    return REFERENCE_EQUALITY_TYPE;
  }

  public master_type list_type() {
    return LIST_TYPE;
  }

  private type_parameters make_parameters(abstract_value first) {
    return new type_parameters(new base_list<abstract_value>(first));
  }

  private type_parameters make_parameters(abstract_value first, abstract_value second) {
    return new type_parameters(new base_list<abstract_value>(first, second));
  }

  private type_parameters make_parameters(abstract_value first, abstract_value second,
      abstract_value third) {
    return new type_parameters(new base_list<abstract_value>(first, second, third));
  }

  public type list_type_of(type element_type) {
    return LIST_TYPE.bind_parameters(make_parameters(element_type));
  }

  public principal_type null_type() {
    return NULL_TYPE;
  }

  public type immutable_null_type() {
    return null_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  public principal_type missing_type() {
    return MISSING_TYPE;
  }

  public type immutable_missing_type() {
    return missing_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  public type get_reference(type_flavor flavor, type value_type) {
    return REFERENCE_TYPE.bind_parameters(make_parameters(value_type)).get_flavored(flavor);
  }

  public boolean is_reference_type(type the_type) {
    principal_type principal = the_type.principal();
    if (principal instanceof parametrized_type) {
      return ((parametrized_type) principal).get_master() == REFERENCE_TYPE;
    } else {
      return false;
    }
  }

  public type get_reference_parameter(type ref_type) {
    parametrized_type ptype = (parametrized_type) ref_type.principal();
    assert ptype.get_master() == REFERENCE_TYPE;
    return (type) ptype.get_parameters().the_list.first();
  }

  public master_type procedure_type() {
    return PROCEDURE_TYPE;
  }

  public master_type function_type() {
    return FUNCTION_TYPE;
  }

  public master_type master_procedure(boolean is_function) {
    return is_function ? FUNCTION_TYPE : PROCEDURE_TYPE;
  }

  // TODO: use list for arguments here
  public type make_procedure(boolean is_function, abstract_value return_value) {
    return master_procedure(is_function).bind_parameters(make_parameters(return_value)).
        get_flavored(flavor.immutable_flavor);
  }

  public type make_procedure(boolean is_function, abstract_value return_value,
      abstract_value first_argument) {
    return master_procedure(is_function).bind_parameters(
        make_parameters(return_value, first_argument)).
        get_flavored(flavor.immutable_flavor);
  }

  public type make_procedure(boolean is_function, abstract_value return_value,
      abstract_value first_argument, abstract_value second_argument) {
    return master_procedure(is_function).bind_parameters(
        make_parameters(return_value, first_argument, second_argument)).
        get_flavored(flavor.immutable_flavor);
  }

  public principal_type ideal_namespace() {
    return ideal_type;
  }

  public principal_type library_namespace() {
    return library_type;
  }

  public principal_type elements_package() {
    return elements_type;
  }

  public principal_type operators_package() {
    return operators_type;
  }

  private master_type get_type(String sname, kind the_kind, flavor_profile the_flavor_profile) {
    simple_name name = simple_name.make(new base_string(sname));
    return get_type(name, the_kind, the_flavor_profile);
  }

  private master_type get_type(action_name name, kind the_kind, flavor_profile the_flavor_profile) {
    return context.get_or_create_type(name, the_kind, elements_type, the_flavor_profile);
  }

  private principal_type make_namespace(action_name name, principal_type parent, kind the_kind) {
    master_type result = context.get_or_create_type(name, the_kind, parent,
        flavor_profiles.nameonly_profile);
    result.process_declaration(declaration_pass.METHODS_AND_VARIABLES);
    return result;
  }

  public type remove_null_type(type the_type) {
    assert type_utilities.is_union(the_type);
    type_flavor union_flavor = the_type.get_flavor();
    immutable_list<abstract_value> the_values = type_utilities.get_union_parameters(the_type);
    assert the_values.size() == 2;
    // TODO: handle when null is the first in union, as in "null or foo_type"
    type null_type = (type) the_values.get(1);
    assert null_type.principal() == null_type();
    type result = (type) the_values.first();
    if (union_flavor != flavor.nameonly_flavor) {
      result = result.get_flavored(union_flavor);
    }
    return result;
  }

  public boolean is_bootstrapped() {
    return VOID_TYPE.get_declaration() != null;
  }

  static boolean is_initialized() {
    return instance != null;
  }

  // TODO: fix this hack
  public static common_library get_instance() {
    assert instance != null;
    return instance;
  }

  public type_declaration_context get_context() {
    return context;
  }
}
