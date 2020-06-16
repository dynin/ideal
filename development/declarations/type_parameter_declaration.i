-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Declaration of a type parameter for an enclosing type.
interface type_parameter_declaration {
  extends type_declaration;

  type variable_type;
  analyzable or null get_type_analyzable;
}
