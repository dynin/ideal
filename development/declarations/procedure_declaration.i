-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- All information encapsulated in a procedure declaration.
interface procedure_declaration {
  extends declaration;

  simple_name original_name;
  action_name short_name;
  annotation_set annotations;
  procedure_category get_category;
  type_flavor get_flavor;
  type get_return_type;
  override principal_type declared_in_type;
  readonly list[type] get_argument_types;
  type get_procedure_type;
  readonly list[variable_declaration] get_parameter_variables;
  procedure_declaration master_declaration;
  boolean overrides_variable;
  readonly list[declaration] get_overriden;
  action or null procedure_action;
  boolean has_body;
  action or null get_body_action;
  variable_declaration or null get_this_declaration;
  procedure_declaration specialize(specialization_context context, principal_type new_parent);
}
