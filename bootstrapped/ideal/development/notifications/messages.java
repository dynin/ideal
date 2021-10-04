// Autogenerated from development/notifications/messages.i

package ideal.development.notifications;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.library.messages.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.origins.*;

public class messages {
  public static final string quote_not_found = new base_string("Reached the end of file without finding the closing quote");
  public static final string opening_quote = new base_string("Here\'s the opening quote");
  public static final string number_format_error = new base_string("Can\'t understand the number format");
  public static final string unrecognized_character = new base_string("Couldn\'t recognize this character");
  public static final string variable_expected = new base_string("Variable declaration expected");
  public static final string unexpected_modifier = new base_string("Didn\'t expect a modifier");
  public static final string symbol_lookup_failed = new base_string("Symbol lookup failed");
  public static final string symbol_lookup_suppress = new base_string("Symbol lookup failed, suppressing related notifications");
  public static final string suppressed = new base_string("Suppressed notification");
  public static final string name_ambiguous_access = new base_string("Ambiguous access of the name");
  public static final string lookup_failed = new base_string("Lookup failed");
  public static final string ambiguous_access = new base_string("Ambiguous access");
  public static final string error_in_parametrizable = new base_string("Error in paramterizable expression");
  public static final string error_in_list_initilizer = new base_string("Error in list initializer");
  public static final string ambiguous_access_parametrizable = new base_string("Ambiguous access of the parametrizable expression");
  public static final string expression_not_parametrizable = new base_string("Expression not parametrizable");
  public static final string error_in_list = new base_string("Error in list");
  public static final string error_in_fn_param = new base_string("Error in function parameter");
  public static final string name_expected = new base_string("Name expected");
  public static final string error_in_flavored_type = new base_string("Error in flavored type");
  public static final string error_in_source = new base_string("Error in source expression");
  public static final string identifier_expected = new base_string("Identifier expected");
  public static final string error_in_supertype = new base_string("Error in supertype");
  public static final string error_in_var_type = new base_string("Error in variable type");
  public static final string var_type_expected = new base_string("Expected bound for type variable (try \'value\')");
  public static final string name_type_used = new base_string("Name of type already used");
  public static final string wrong_kind = new base_string("Wrong kind");
  public static final string var_type_missing = new base_string("Variable type missing");
  public static final string type_expected = new base_string("Type expected");
  public static final string duplicate_static = new base_string("Duplicate \'static\' modifier");
  public static final string duplicate_final = new base_string("Duplicate \'final\' modifier");
  public static final string duplicate_abstract = new base_string("Duplicate \'abstract\' modifier");
  public static final string duplicate_access = new base_string("Duplicate access modifier");
  public static final string duplicate_variance = new base_string("Duplicate variance modifier");
  public static final string wrong_arity = new base_string("Wrong arity in a parametrizable expression");
  public static final string return_outside_proc = new base_string("Return outside of a procedure");
  public static final string error_in_conditional = new base_string("Error in conditional");
  public static final string error_in_block = new base_string("Error in block");
  public static final string expected_boolean = new base_string("Expected boolean");
  public static final string error_in_initializer = new base_string("Error in initializer");
  public static final string jump_outside_loop = new base_string("Jump outside of a loop");
  public static final string type_cycle = new base_string("There is a cycle in type dependencies");
  public static final string reserved_word = new base_string("Reserved word");
  public static final string original_declaration = new base_string("Original declaration");
  public static final string shadowed_declaration = new base_string("Shadowed declaration");
  public static final string value_declaration = new base_string("Value declaration in non-enum type");
  public static final string first_declaration = new base_string("First declaration");
  public static final string primary_declaration = new base_string("This is the primary_declaration");
}