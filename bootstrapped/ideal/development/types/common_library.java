// Autogenerated from development/types/common_library.i

package ideal.development.types;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.kinds.*;
import static ideal.development.names.common_names.*;
import static ideal.development.kinds.type_kinds.*;
import static ideal.development.flavors.flavor_profiles.*;

public class common_library implements value {
  private static common_library instance;
  private final type_declaration_context context;
  private final principal_type ideal_type;
  private final principal_type library_type;
  private final principal_type elements_type;
  private final principal_type operators_type;
  private final principal_type VOID_TYPE;
  private final principal_type UNDEFINED_TYPE;
  private final principal_type ENTITY_TYPE;
  private final principal_type VALUE_TYPE;
  private final principal_type DATA_TYPE;
  private final principal_type INTEGER_TYPE;
  private final principal_type NONNEGATIVE_TYPE;
  private final principal_type BOOLEAN_TYPE;
  private final principal_type CHARACTER_TYPE;
  private final principal_type STRING_TYPE;
  private final principal_type NULL_TYPE;
  private final principal_type MISSING_TYPE;
  private final master_type REFERENCE_TYPE;
  private final master_type PROCEDURE_TYPE;
  private final master_type FUNCTION_TYPE;
  private final master_type STRINGABLE_TYPE;
  private final master_type EQUALITY_COMPARABLE_TYPE;
  private final master_type REFERENCE_EQUALITY_TYPE;
  private final master_type LIST_TYPE;
  public common_library(final type_declaration_context context) {
    this.context = context;
    elementary_types.set_context(context);
    this.ideal_type = this.make_namespace(common_names.ideal_name, elementary_types.root_type(), type_kinds.namespace_kind);
    this.library_type = this.make_namespace(common_names.library_name, this.ideal_type, type_kinds.namespace_kind);
    this.elements_type = this.make_namespace(common_names.elements_name, this.library_type, type_kinds.package_kind);
    this.operators_type = this.make_namespace(common_names.operators_name, this.library_type, type_kinds.package_kind);
    this.VOID_TYPE = this.get_type(common_names.void_name, type_kinds.singleton_kind, flavor_profiles.deeply_immutable_profile);
    this.UNDEFINED_TYPE = this.get_type(common_names.undefined_name, type_kinds.singleton_kind, flavor_profiles.deeply_immutable_profile);
    this.ENTITY_TYPE = this.get_type(common_names.entity_name, type_kinds.concept_kind, flavor_profiles.mutable_profile);
    this.VALUE_TYPE = this.get_type(common_names.value_name, type_kinds.concept_kind, flavor_profiles.mutable_profile);
    this.DATA_TYPE = this.get_type(common_names.data_name, type_kinds.concept_kind, flavor_profiles.mutable_profile);
    this.BOOLEAN_TYPE = this.get_type(common_names.boolean_name, type_kinds.enum_kind, flavor_profiles.deeply_immutable_profile);
    this.CHARACTER_TYPE = this.get_type(common_names.character_name, type_kinds.datatype_kind, flavor_profiles.deeply_immutable_profile);
    this.INTEGER_TYPE = this.get_type(common_names.integer_name, type_kinds.datatype_kind, flavor_profiles.deeply_immutable_profile);
    this.NONNEGATIVE_TYPE = this.get_type(common_names.nonnegative_name, type_kinds.datatype_kind, flavor_profiles.deeply_immutable_profile);
    this.STRING_TYPE = this.get_type(common_names.string_name, type_kinds.datatype_kind, flavor_profiles.deeply_immutable_profile);
    this.NULL_TYPE = this.get_type(common_names.null_name, type_kinds.interface_kind, flavor_profiles.deeply_immutable_profile);
    this.MISSING_TYPE = this.get_type(common_names.missing_name, type_kinds.singleton_kind, flavor_profiles.deeply_immutable_profile);
    this.REFERENCE_TYPE = this.get_type(common_names.reference_name, type_kinds.reference_kind, flavor_profiles.mutable_profile);
    this.REFERENCE_TYPE.make_parametrizable();
    this.PROCEDURE_TYPE = this.get_type(common_names.procedure_name, type_kinds.procedure_kind, flavor_profiles.immutable_profile);
    this.PROCEDURE_TYPE.make_parametrizable();
    this.FUNCTION_TYPE = this.get_type(common_names.function_name, type_kinds.procedure_kind, flavor_profiles.deeply_immutable_profile);
    this.FUNCTION_TYPE.make_parametrizable();
    this.STRINGABLE_TYPE = this.get_type(common_names.stringable_name, type_kinds.interface_kind, flavor_profiles.mutable_profile);
    this.EQUALITY_COMPARABLE_TYPE = this.get_type(common_names.equality_comparable_name, type_kinds.concept_kind, flavor_profiles.mutable_profile);
    this.REFERENCE_EQUALITY_TYPE = this.get_type(common_names.reference_equality_name, type_kinds.interface_kind, flavor_profiles.mutable_profile);
    this.LIST_TYPE = this.get_type(common_names.list_name, type_kinds.interface_kind, flavor_profiles.mutable_profile);
    this.LIST_TYPE.make_parametrizable();
    assert common_library.instance == null;
    common_library.instance = this;
  }
  public principal_type void_type() {
    return this.VOID_TYPE;
  }
  public principal_type undefined_type() {
    return this.UNDEFINED_TYPE;
  }
  public principal_type entity_type() {
    return this.ENTITY_TYPE;
  }
  public principal_type value_type() {
    return this.VALUE_TYPE;
  }
  public principal_type data_type() {
    return this.DATA_TYPE;
  }
  public principal_type boolean_type() {
    return this.BOOLEAN_TYPE;
  }
  public principal_type character_type() {
    return this.CHARACTER_TYPE;
  }
  public principal_type integer_type() {
    return this.INTEGER_TYPE;
  }
  public principal_type nonnegative_type() {
    return this.NONNEGATIVE_TYPE;
  }
  public type immutable_void_type() {
    return this.void_type().get_flavored(flavor.deeply_immutable_flavor);
  }
  public type immutable_boolean_type() {
    return this.boolean_type().get_flavored(flavor.deeply_immutable_flavor);
  }
  public type immutable_integer_type() {
    return this.integer_type().get_flavored(flavor.deeply_immutable_flavor);
  }
  public type immutable_nonnegative_type() {
    return this.nonnegative_type().get_flavored(flavor.deeply_immutable_flavor);
  }
  public type immutable_character_type() {
    return this.character_type().get_flavored(flavor.deeply_immutable_flavor);
  }
  public principal_type string_type() {
    return this.STRING_TYPE;
  }
  public type immutable_string_type() {
    return this.string_type().get_flavored(flavor.deeply_immutable_flavor);
  }
  public master_type stringable_type() {
    return this.STRINGABLE_TYPE;
  }
  public principal_type equality_comparable_type() {
    return this.EQUALITY_COMPARABLE_TYPE;
  }
  public principal_type reference_equality_type() {
    return this.REFERENCE_EQUALITY_TYPE;
  }
  public master_type list_type() {
    return this.LIST_TYPE;
  }
  private type_parameters make_parameters(final abstract_value first) {
    return new type_parameters(new base_list<abstract_value>(first));
  }
  private type_parameters make_parameters(final abstract_value first, final abstract_value second) {
    return new type_parameters(new base_list<abstract_value>(first, second));
  }
  private type_parameters make_parameters(final abstract_value first, final abstract_value second, final abstract_value third) {
    return new type_parameters(new base_list<abstract_value>(first, second, third));
  }
  public type list_type_of(final type element_type) {
    return this.LIST_TYPE.bind_parameters(this.make_parameters(element_type));
  }
  public principal_type null_type() {
    return this.NULL_TYPE;
  }
  public type immutable_null_type() {
    return this.null_type().get_flavored(flavor.deeply_immutable_flavor);
  }
  public principal_type missing_type() {
    return this.MISSING_TYPE;
  }
  public type immutable_missing_type() {
    return this.missing_type().get_flavored(flavor.deeply_immutable_flavor);
  }
  public type get_reference(final type_flavor flavor, final type value_type) {
    return this.REFERENCE_TYPE.bind_parameters(this.make_parameters(value_type)).get_flavored(flavor);
  }
  public boolean is_reference_type(final type the_type) {
    final principal_type principal = the_type.principal();
    if (principal instanceof parametrized_type) {
      return ((parametrized_type) principal).get_master() == this.REFERENCE_TYPE;
    } else {
      return false;
    }
  }
  public type get_reference_parameter(final type ref_type) {
    final parametrized_type the_parametrized_type = (parametrized_type) ref_type.principal();
    assert the_parametrized_type.get_master() == this.REFERENCE_TYPE;
    return (type) the_parametrized_type.get_parameters().the_list.first();
  }
  public master_type procedure_type() {
    return this.PROCEDURE_TYPE;
  }
  public master_type function_type() {
    return this.FUNCTION_TYPE;
  }
  public master_type master_procedure(final boolean is_function) {
    return is_function ? this.FUNCTION_TYPE : this.PROCEDURE_TYPE;
  }
  public type make_procedure(final boolean is_function, final abstract_value return_value) {
    return this.master_procedure(is_function).bind_parameters(this.make_parameters(return_value)).get_flavored(flavor.immutable_flavor);
  }
  public type make_procedure(final boolean is_function, final abstract_value return_value, final abstract_value first_argument) {
    return this.master_procedure(is_function).bind_parameters(this.make_parameters(return_value, first_argument)).get_flavored(flavor.immutable_flavor);
  }
  public type make_procedure(final boolean is_function, final abstract_value return_value, final abstract_value first_argument, final abstract_value second_argument) {
    return this.master_procedure(is_function).bind_parameters(this.make_parameters(return_value, first_argument, second_argument)).get_flavored(flavor.immutable_flavor);
  }
  public principal_type ideal_namespace() {
    return this.ideal_type;
  }
  public principal_type library_namespace() {
    return this.library_type;
  }
  public principal_type elements_package() {
    return this.elements_type;
  }
  public principal_type operators_package() {
    return this.operators_type;
  }
  private master_type get_type(final action_name name, final kind the_kind, final flavor_profile the_flavor_profile) {
    return this.context.get_or_create_type(name, the_kind, this.elements_type, the_flavor_profile);
  }
  private principal_type make_namespace(final action_name name, final principal_type parent, final kind the_kind) {
    final master_type result = this.context.get_or_create_type(name, the_kind, parent, flavor_profiles.nameonly_profile);
    result.process_declaration(declaration_pass.METHODS_AND_VARIABLES);
    return result;
  }
  public type remove_null_type(final type the_type) {
    assert type_utilities.is_union(the_type);
    final type_flavor union_flavor = the_type.get_flavor();
    final immutable_list<abstract_value> the_values = type_utilities.get_union_parameters(the_type);
    assert ideal.machine.elements.runtime_util.values_equal(the_values.size(), 2);
    final type the_null_type = (type) the_values.get(1);
    assert the_null_type.principal() == this.null_type();
    type result = (type) the_values.get(0);
    if (union_flavor != flavor.nameonly_flavor) {
      result = result.get_flavored(union_flavor);
    }
    return result;
  }
  public boolean is_bootstrapped() {
    return this.VOID_TYPE.get_declaration() != null;
  }
  public static boolean is_initialized() {
    return common_library.instance != null;
  }
  public static common_library get_instance() {
    assert common_library.instance != null;
    return common_library.instance;
  }
  public type_declaration_context get_context() {
    return this.context;
  }
}
