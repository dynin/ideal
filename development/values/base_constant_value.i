-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

abstract class base_constant_value[deeply_immutable data value_type] {
  extends base_data_value;

  private value_type boxed;

  base_constant_value(value_type boxed, type bound) {
    super(bound);
    assert boxed != null;
    this.boxed = boxed;
  }

  override value_type unwrap => boxed;

  var abstract string constant_to_string();

  override string to_string => utilities.describe(this, constant_to_string);
}
