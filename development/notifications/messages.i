-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public interface messages {
  string quote_not_found = new base_string(
      "Reached the end of file without finding the closing quote");

  string opening_quote = new base_string("Here's the opening quote");

  string number_format_error = new base_string(
      "Can't understand the number format");

  string unrecognized_character = new base_string(
      "Couldn't recognize this character");

  string variable_expected = new base_string("Variable declaration expected");

  string unexpected_modifier = new base_string("Didn't expect a modifier");

  string symbol_lookup_failed = new base_string("Symbol lookup failed");

  string symbol_lookup_suppress = new base_string(
      "Symbol lookup failed, suppressing related notifications");

  string suppressed = new base_string("Suppressed notification");

  string name_ambiguous_access = new base_string("Ambiguous access of the name");

  string lookup_failed = new base_string("Lookup failed");

  string ambiguous_access = new base_string("Ambiguous access");

  string error_in_parametrizable =
      new base_string("Error in paramterizable expression");

  string error_in_list_initilizer = new base_string("Error in list initializer");

  string ambiguous_access_parametrizable =
      new base_string("Ambiguous access of the parametrizable expression");

  string expression_not_parametrizable =
      new base_string("Expression not parametrizable");

  string error_in_list = new base_string("Error in list");

  string error_in_fn_param =
      new base_string("Error in function parameter");

  string name_expected = new base_string("Name expected");

  string error_in_flavored_type =
      new base_string("Error in flavored type");

  string error_in_source =
      new base_string("Error in source expression");

  string identifier_expected =
      new base_string("Identifier expected");

  string error_in_supertype =
      new base_string("Error in supertype");

  string error_in_var_type = new base_string("Error in variable type");

  string var_type_expected = new base_string("Expected bound for type variable (try 'value')");

  string name_type_used = new base_string("Name of type already used");

  string wrong_kind = new base_string("Wrong kind");

  string var_type_missing = new base_string("Variable type missing");

  string type_expected = new base_string("Type expected");

  // TODO: replace these with a single formatter.
  string duplicate_static = new base_string("Duplicate 'static' modifier");

  string duplicate_final = new base_string("Duplicate 'final' modifier");

  string duplicate_abstract = new base_string("Duplicate 'abstract' modifier");

  string duplicate_access = new base_string("Duplicate access modifier");

  string duplicate_variance = new base_string("Duplicate variance modifier");

  string wrong_arity = new base_string("Wrong arity in a parametrizable expression");

  string return_outside_proc = new base_string("Return outside of a procedure");

  string error_in_conditional = new base_string("Error in conditional");

  string error_in_block = new base_string("Error in block");

  string expected_boolean = new base_string("Expected boolean");

  string error_in_initializer = new base_string("Error in initializer");

  string jump_outside_loop = new base_string("Jump outside of a loop");

  string type_cycle = new base_string("There is a cycle in type dependencies");

  string reserved_word = new base_string("Reserved word");

  string original_declaration = new base_string("Original declaration");

  string shadowed_declaration  = new base_string("Shadowed declaration");

  string value_declaration = new base_string("Value declaration in non-enum type");

  string first_declaration = new base_string("First declaration");

  string primary_declaration = new base_string("This is the primary_declaration");
}
