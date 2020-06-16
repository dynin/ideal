-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A declaration that contains type name but loading the body is delayed.
interface type_announcement {
  extends type_declaration;

  type_declaration get_type_declaration;
  void load_type();
}