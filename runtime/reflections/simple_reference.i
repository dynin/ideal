-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A simple reference that stores the value and exposes methods to read and update it.
class simple_reference[any value value_type] {
    implements reference_wrapper[value_type];
    extends debuggable;

  private type_id the_value_type;
  private type_id the_reference_type;
  private var value_wrapper[value_type] or null the_value;

  simple_reference(type_id the_value_type, type_id the_reference_type,
      value_wrapper[value_type] or null the_value) {
    this.the_value_type = the_value_type;
    this.the_reference_type = the_reference_type;
    this.the_value = the_value;
  }

  override type_id type_bound() {
    return the_reference_type;
  }

  override type_id value_type_bound() {
    return the_value_type;
  }

  override void init(value_wrapper[value_type] new_value) {
    -- TODO: enforce that the type of new_value is a subtype of the_value_type.
    assert the_value is null;
    the_value = new_value;
  }

  override value_wrapper[value_type] get() {
    -- TODO: update null handling here; the_value should never be null.
    return the_value as value_wrapper[value_type];
  }

  override void set(value_wrapper[value_type] new_value) {
    -- TODO: enforce that the type of new_value is a subtype of the_value_type.
    the_value = new_value;
  }

  override string to_string() {
    return utilities.describe(this, the_value_type);
  }
}
