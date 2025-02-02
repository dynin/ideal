-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Print the value of an abstract value or a type.
-- TODO: implement printer that outputs text.
-- TODO: rename value_printer, use a function instead.
interface value_printer {
  extends data;

  string print_value(abstract_value the_value);
}
