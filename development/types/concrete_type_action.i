-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class concrete_type_action {
  extends type_action;

  private type the_type;

  concrete_type_action(type the_type, origin the_origin) {
    super(the_origin);
    verify the_type is_not null;
    this.the_type = the_type;
  }

  implement type get_type() => the_type;
}
