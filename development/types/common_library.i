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

  var principal_type void_type => VOID_TYPE;

  var principal_type undefined_type => UNDEFINED_TYPE;

  var principal_type entity_type => ENTITY_TYPE;

  var principal_type value_type => VALUE_TYPE;

  var principal_type data_type => DATA_TYPE;

  var principal_type boolean_type => BOOLEAN_TYPE;

  var principal_type character_type => CHARACTER_TYPE;

  var principal_type integer_type => INTEGER_TYPE;

  var principal_type nonnegative_type => NONNEGATIVE_TYPE;

  var type immutable_void_type => void_type.get_flavored(flavor.deeply_immutable_flavor);

  var type immutable_boolean_type => boolean_type.get_flavored(flavor.deeply_immutable_flavor);

  var type immutable_integer_type => integer_type.get_flavored(flavor.deeply_immutable_flavor);

  var type immutable_nonnegative_type =>
      nonnegative_type.get_flavored(flavor.deeply_immutable_flavor);

  var type immutable_character_type =>
      character_type.get_flavored(flavor.deeply_immutable_flavor);

  var principal_type string_type => STRING_TYPE;

  var type immutable_string_type => string_type.get_flavored(flavor.deeply_immutable_flavor);

  var master_type stringable_type => STRINGABLE_TYPE;

  var principal_type equality_comparable_type => EQUALITY_COMPARABLE_TYPE;

  var principal_type reference_equality_type => REFERENCE_EQUALITY_TYPE;

  var master_type list_type => LIST_TYPE;

  type list_type_of(type element_type) {
    -- TODO: cast is redundant, needed by to_java_transformer.
    return LIST_TYPE.bind_parameters(type_parameters.new([ element_type .> abstract_value, ]));
  }

  var principal_type null_type => NULL_TYPE;

  var type immutable_null_type => null_type.get_flavored(flavor.deeply_immutable_flavor);

  var principal_type missing_type => MISSING_TYPE;

  var type immutable_missing_type => missing_type.get_flavored(flavor.deeply_immutable_flavor);

  type get_reference(type_flavor flavor, type value_type) {
    -- TODO: cast is redundant, needed by to_java_transformer.
    return REFERENCE_TYPE.bind_parameters(type_parameters.new([ value_type .> abstract_value, ])).
        get_flavored(flavor);
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

  master_type procedure_type => PROCEDURE_TYPE;

  master_type function_type => FUNCTION_TYPE;

  master_type master_procedure(boolean is_function) {
    return is_function ? FUNCTION_TYPE : PROCEDURE_TYPE;
  }

  -- TODO: use list for arguments here
  overload type make_procedure(boolean is_function, abstract_value return_value) {
    return master_procedure(is_function).bind_parameters(
        type_parameters.new([ return_value, ])).
        get_flavored(flavor.immutable_flavor);
  }

  overload type make_procedure(boolean is_function, abstract_value return_value,
      abstract_value first_argument) {
    return master_procedure(is_function).bind_parameters(
        type_parameters.new([ return_value, first_argument ])).
        get_flavored(flavor.immutable_flavor);
  }

  overload type make_procedure(boolean is_function, abstract_value return_value,
      abstract_value first_argument, abstract_value second_argument) {
    return master_procedure(is_function).bind_parameters(
        type_parameters.new([ return_value, first_argument, second_argument ])).
        get_flavored(flavor.immutable_flavor);
  }

  var principal_type ideal_namespace => ideal_type;

  var principal_type library_namespace => library_type;

  var principal_type elements_package => elements_type;

  var principal_type operators_package => operators_type;

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
    assert the_null_type.principal == null_type;
    var result : the_values[0] !> type;
    if (union_flavor != flavor.nameonly_flavor) {
      result = result.get_flavored(union_flavor);
    }
    return result;
  }

  var boolean is_bootstrapped => VOID_TYPE.get_declaration is_not null;

  static var boolean is_initialized => instance is_not null;

  -- TODO: fix this hack
  static common_library get_instance() {
    assert instance is_not null;
    return instance;
  }

  var type_declaration_context get_context => context;
}
