-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Information encapsulated in an import declaration.
interface import_declaration {
  extends declaration;

  annotation_set annotations;
  type get_type;
  boolean is_implicit;
}
