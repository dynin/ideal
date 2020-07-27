-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- An interface that tags a declaration, such as
--- |ideal.development.declarations.type_declaration|,
--- |ideal.development.declarations.variable_declaration|, and so on.
interface declaration {
  extends origin;

  -- The named parent type in which this declaration belongs.
  principal_type declared_in_type;

  -- If true, this declaration has errors and further processing should be skipped.
  boolean has_errors;

  -- TODO: expose this if we need to.
  --action_name short_name;
}
