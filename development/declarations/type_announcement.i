-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- A declaration that contains type name but loading the body is delayed.
interface type_announcement {
  extends declaration;

  action_name short_name;
  kind get_kind;
  annotation_set annotations;
  principal_type get_declared_type;

  type_declaration get_type_declaration;
  readonly list[declaration] external_declarations;
  --- Load resource associated with the type (typically a type_declaration,
  --- but can also be HTML content.)
  load_resource();
}
