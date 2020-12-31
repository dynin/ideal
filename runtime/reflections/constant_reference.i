-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A readonly reference that allows access to the value initialized at construction time.
-- TODO: this should implement immutable_reference_wrapper,
-- and statically detect invalid write access.
class constant_reference[any value value_type] {
    implements reference_wrapper[value_type];
    extends debuggable;

  private value_wrapper[value_type] the_value;
  private type_id the_reference_type;

  constant_reference(value_wrapper[value_type] the_value, type_id the_reference_type) {
    this.the_value = the_value;
    this.the_reference_type = the_reference_type;
  }

  override type_id type_bound() {
    return the_reference_type;
  }

  override type_id value_type_bound() {
    return the_value.type_bound;
  }

  override value_wrapper[value_type] get() {
    return the_value;
  }

  override void init(value_wrapper[value_type] new_value) {
    utilities.panic("Can't init a constant_reference");
  }

  override void set(value_wrapper[value_type] new_value) {
    utilities.panic("Can't set a constant_reference");
  }

  override string to_string() {
    return utilities.describe(this, the_value);
  }
}
