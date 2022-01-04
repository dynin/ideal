-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

meta_construct class modifier_construct {
  implements annotation_construct;

  modifier_kind the_kind;
  list_construct or null parameters;

  overload modifier_construct(modifier_kind the_kind, list_construct or null parameters,
      origin the_origin) {
    super(the_origin);
    this.the_kind = the_kind;
    this.parameters = parameters;
  }

  overload modifier_construct(modifier_kind the_kind, origin the_origin) {
    this(the_kind, missing.instance, the_origin);
  }

  override string to_string => utilities.describe(this, the_kind);
}
