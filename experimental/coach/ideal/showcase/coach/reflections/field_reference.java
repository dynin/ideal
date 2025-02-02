/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.reflections;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.scanners.*;
import ideal.development.types.*;
import ideal.development.actions.*;
import ideal.development.analyzers.*;
import ideal.development.flavors.*;
import ideal.development.kinds.*;
import ideal.development.values.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;
public class field_reference extends debuggable
    implements reference_wrapper {

  private final composite_data_value the_value;
  private final variable_id the_field;

  public field_reference(composite_data_value the_value, variable_id the_field) {
    this.the_value = the_value;
    this.the_field = the_field;
  }

  public identifier short_name() {
    return the_field.short_name();
  }

  @Override
  public type_id type_bound() {
    return the_field.reference_type();
  }

  @Override
  public type_id value_type_bound() {
    return the_field.value_type();
  }

  @Override
  public void init(value_wrapper new_value) {
    // TODO: check that field is not initialized.
    the_value.put_var(the_field, new_value);
  }

  @Override
  public value_wrapper get() {
    value_wrapper result = the_value.get_var(the_field);
    if (result == null) {
      result = the_value.get_world().make_default_value(value_type_bound());
    }
    return result;
  }

  @Override
  public void set(value_wrapper new_value) {
    the_value.put_var(the_field, new_value);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_field);
  }
}
