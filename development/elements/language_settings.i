-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

interface language_settings {
  access_modifier get_default_type_access(kind the_kind);

  access_modifier get_default_variable_access(kind the_kind);

  access_modifier get_default_procedure_access(kind the_kind);
}
