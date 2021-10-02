-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.development.names.common_names;
implicit import ideal.development.kinds.type_kinds;
implicit import ideal.development.flavors.flavor_profiles;

class common_library {
  implements value;

  private static var common_library instance;

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

  common_library(type_declaration_context context) {
    this.context = context;
    elementary_types.set_context(context);

    ideal_type = make_namespace(ideal_name, elementary_types.root_type, namespace_kind);
    library_type = make_namespace(library_name, ideal_type, namespace_kind);
    elements_type = make_namespace(elements_name, library_type, package_kind);
    operators_type = make_namespace(operators_name, library_type, package_kind);

    VOID_TYPE = get_type(void_name, singleton_kind, deeply_immutable_profile);
    UNDEFINED_TYPE = get_type(undefined_name, singleton_kind, deeply_immutable_profile);
    ENTITY_TYPE = get_type(entity_name, concept_kind, mutable_profile);
    VALUE_TYPE = get_type(value_name, concept_kind, mutable_profile);
    DATA_TYPE = get_type(data_name, concept_kind, mutable_profile);
    BOOLEAN_TYPE = get_type(boolean_name, enum_kind, deeply_immutable_profile);
    CHARACTER_TYPE = get_type(character_name, datatype_kind, deeply_immutable_profile);
    INTEGER_TYPE = get_type(integer_name, datatype_kind, deeply_immutable_profile);
    NONNEGATIVE_TYPE = get_type(nonnegative_name, datatype_kind, deeply_immutable_profile);
    STRING_TYPE = get_type(string_name, datatype_kind, deeply_immutable_profile);
    NULL_TYPE = get_type(null_name, interface_kind, deeply_immutable_profile);
    MISSING_TYPE = get_type(missing_name, singleton_kind, deeply_immutable_profile);

    REFERENCE_TYPE = get_type(reference_name, #id:reference_kind, mutable_profile);
    REFERENCE_TYPE.make_parametrizable();
    PROCEDURE_TYPE = get_type(procedure_name, #id:procedure_kind, immutable_profile);
    PROCEDURE_TYPE.make_parametrizable();
    FUNCTION_TYPE = get_type(function_name, #id:procedure_kind, deeply_immutable_profile);
    FUNCTION_TYPE.make_parametrizable();
    STRINGABLE_TYPE = get_type(stringable_name, interface_kind, mutable_profile);
    EQUALITY_COMPARABLE_TYPE = get_type(equality_comparable_name, concept_kind, mutable_profile);
    REFERENCE_EQUALITY_TYPE = get_type(reference_equality_name, interface_kind, mutable_profile);
    LIST_TYPE = get_type(list_name, interface_kind, mutable_profile);
    LIST_TYPE.make_parametrizable();

    assert instance is null;
    instance = this;
  }

  principal_type void_type() {
    return VOID_TYPE;
  }

  principal_type undefined_type() {
    return UNDEFINED_TYPE;
  }

  principal_type entity_type() {
    return ENTITY_TYPE;
  }

  principal_type value_type() {
    return VALUE_TYPE;
  }

  principal_type data_type() {
    return DATA_TYPE;
  }

  principal_type boolean_type() {
    return BOOLEAN_TYPE;
  }

  principal_type character_type() {
    return CHARACTER_TYPE;
  }

  principal_type integer_type() {
    return INTEGER_TYPE;
  }

  principal_type nonnegative_type() {
    return NONNEGATIVE_TYPE;
  }

  type immutable_void_type() {
    return void_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  type immutable_boolean_type() {
    return boolean_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  type immutable_integer_type() {
    return integer_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  type immutable_nonnegative_type() {
    return nonnegative_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  type immutable_character_type() {
    return character_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  principal_type string_type() {
    return STRING_TYPE;
  }

  type immutable_string_type() {
    return string_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  master_type stringable_type() {
    return STRINGABLE_TYPE;
  }

  principal_type equality_comparable_type() {
    return EQUALITY_COMPARABLE_TYPE;
  }

  principal_type reference_equality_type() {
    return REFERENCE_EQUALITY_TYPE;
  }

  master_type list_type() {
    return LIST_TYPE;
  }

  private overload type_parameters make_parameters(abstract_value first) {
    return type_parameters.new(base_list[abstract_value].new(first));
  }

  private overload type_parameters make_parameters(abstract_value first, abstract_value second) {
    return type_parameters.new(base_list[abstract_value].new(first, second));
  }

  private overload type_parameters make_parameters(abstract_value first, abstract_value second,
      abstract_value third) {
    return type_parameters.new(base_list[abstract_value].new(first, second, third));
  }

  type list_type_of(type element_type) {
    return LIST_TYPE.bind_parameters(make_parameters(element_type));
  }

  principal_type null_type() {
    return NULL_TYPE;
  }

  type immutable_null_type() {
    return null_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  principal_type missing_type() {
    return MISSING_TYPE;
  }

  type immutable_missing_type() {
    return missing_type().get_flavored(flavor.deeply_immutable_flavor);
  }

  type get_reference(type_flavor flavor, type value_type) {
    return REFERENCE_TYPE.bind_parameters(make_parameters(value_type)).get_flavored(flavor);
  }

  boolean is_reference_type(type the_type) {
    principal : the_type.principal;
    if (principal is parametrized_type) {
      return principal.get_master == REFERENCE_TYPE;
    } else {
      return false;
    }
  }

  type get_reference_parameter(type ref_type) {
    the_parametrized_type : ref_type.principal !> parametrized_type;
    assert the_parametrized_type.get_master == REFERENCE_TYPE;
    return the_parametrized_type.get_parameters.the_list.first !> type;
  }

  master_type procedure_type() {
    return PROCEDURE_TYPE;
  }

  master_type function_type() {
    return FUNCTION_TYPE;
  }

  master_type master_procedure(boolean is_function) {
    return is_function ? FUNCTION_TYPE : PROCEDURE_TYPE;
  }

  -- TODO: use list for arguments here
  overload type make_procedure(boolean is_function, abstract_value return_value) {
    return master_procedure(is_function).bind_parameters(make_parameters(return_value)).
        get_flavored(flavor.immutable_flavor);
  }

  overload type make_procedure(boolean is_function, abstract_value return_value,
      abstract_value first_argument) {
    return master_procedure(is_function).bind_parameters(
        make_parameters(return_value, first_argument)).
        get_flavored(flavor.immutable_flavor);
  }

  overload type make_procedure(boolean is_function, abstract_value return_value,
      abstract_value first_argument, abstract_value second_argument) {
    return master_procedure(is_function).bind_parameters(
        make_parameters(return_value, first_argument, second_argument)).
        get_flavored(flavor.immutable_flavor);
  }

  principal_type ideal_namespace() {
    return ideal_type;
  }

  principal_type library_namespace() {
    return library_type;
  }

  principal_type elements_package() {
    return elements_type;
  }

  principal_type operators_package() {
    return operators_type;
  }

  private master_type get_type(action_name name, kind the_kind, flavor_profile the_flavor_profile) {
    return context.get_or_create_type(name, the_kind, elements_type, the_flavor_profile);
  }

  private principal_type make_namespace(action_name name, principal_type parent, kind the_kind) {
    result : context.get_or_create_type(name, the_kind, parent, flavor_profiles.nameonly_profile);
    result.process_declaration(declaration_pass.METHODS_AND_VARIABLES);
    return result;
  }

  type remove_null_type(type the_type) {
    assert type_utilities.is_union(the_type);
    union_flavor : the_type.get_flavor;
    the_values : type_utilities.get_union_parameters(the_type);
    assert the_values.size == 2;
    -- TODO: handle when null is the first in union, as in "null or foo_type"
    the_null_type : the_values[1] !> type;
    assert the_null_type.principal == null_type();
    var result : the_values[0] !> type;
    if (union_flavor != flavor.nameonly_flavor) {
      result = result.get_flavored(union_flavor);
    }
    return result;
  }

  boolean is_bootstrapped() {
    return VOID_TYPE.get_declaration is_not null;
  }

  static boolean is_initialized() {
    return instance is_not null;
  }

  -- TODO: fix this hack
  static common_library get_instance() {
    assert instance is_not null;
    return instance;
  }

  type_declaration_context get_context() {
    return context;
  }
}
