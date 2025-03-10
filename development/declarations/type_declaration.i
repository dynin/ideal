-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A declaration of a type that includes all related declarations in a type signature.
interface type_declaration {
  extends named_declaration;

  kind get_kind;
  override action_name short_name;
  annotation_set annotations;
  principal_type get_declared_type;
  override principal_type declared_in_type;
  type_declaration master_declaration;
  readonly list[type_parameter_declaration] or null get_parameters;
  readonly list[declaration] get_signature;
  -- TODO: this may be a misleading name for this method, rename.
  process_declaration(declaration_pass pass);
}
