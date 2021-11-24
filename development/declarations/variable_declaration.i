-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- All information encapsulated in a variable declaration.
interface variable_declaration {
  extends named_declaration, variable_id;

  variable_category get_category;
  annotation_set annotations;
  override action_name short_name;
  override principal_type declared_in_type;
  --- Get (flavored) variable type.
  type_flavor get_flavor;
  override type value_type;
  override type reference_type;
  boolean declared_as_reference;
  readonly list[declaration] get_overriden;
  action or null init_action;
  variable_declaration specialize(specialization_context context, principal_type new_parent);
}
