-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

namespace messages {
  string quote_not_found : "Reached the end of file without finding the closing quote";

  string opening_quote : "Here's the opening quote";

  string number_format_error : "Can't understand the number format";

  string unrecognized_character : "Couldn't recognize this character";

  string variable_expected : "Variable declaration expected";

  string unexpected_modifier : "Didn't expect a modifier";

  string symbol_lookup_failed : "Symbol lookup failed";

  string symbol_lookup_suppress : "Symbol lookup failed, suppressing related notifications";

  string suppressed : "Suppressed notification";

  string name_ambiguous_access : "Ambiguous access of the name";

  string lookup_failed : "Lookup failed";

  string ambiguous_access : "Ambiguous access";

  string error_in_parametrizable : "Error in paramterizable expression";

  string error_in_list_initilizer : "Error in list initializer";

  string ambiguous_access_parametrizable : "Ambiguous access of the parametrizable expression";

  string expression_not_parametrizable : "Expression not parametrizable";

  string error_in_list : "Error in list";

  string error_in_fn_param : "Error in function parameter";

  string name_expected : "Name expected";

  string error_in_flavored_type : "Error in flavored type";

  string error_in_source : "Error in source expression";

  string identifier_expected : "Identifier expected";

  string error_in_supertype : "Error in supertype";

  string error_in_var_type : "Error in variable type";

  string var_type_expected : "Expected bound for type variable (try 'value')";

  string name_type_used : "Name of type already used";

  string wrong_kind : "Wrong kind";

  string var_type_missing : "Variable type missing";

  string type_expected : "Type expected";

  -- TODO: replace these with a single formatter.
  string duplicate_static : "Duplicate 'static' modifier";

  string duplicate_final : "Duplicate 'final' modifier";

  string duplicate_abstract : "Duplicate 'abstract' modifier";

  string duplicate_access : "Duplicate access modifier";

  string duplicate_variance : "Duplicate variance modifier";

  string wrong_arity : "Wrong arity in a parametrizable expression";

  string return_outside_proc : "Return outside of a procedure";

  string error_in_conditional : "Error in conditional";

  string error_in_block : "Error in block";

  string expected_boolean : "Expected boolean";

  string error_in_initializer : "Error in initializer";

  string jump_outside_loop : "Jump outside of a loop";

  string type_cycle : "There is a cycle in type dependencies";

  string reserved_word : "Reserved word";

  string original_declaration : "Original declaration";

  string shadowed_declaration  : "Shadowed declaration";

  string value_declaration : "Value declaration in non-enum type";

  string first_declaration : "First declaration";

  string primary_declaration : "This is the primary_declaration";
}
