/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
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
public class element_reference extends debuggable implements reference_wrapper<any_value> {

  private final list_wrapper value;
  private final int index;
  private final type_id element_type;

  public element_reference(list_wrapper value, int index) {
    this.value = value;
    this.index = index;
    this.element_type = get_datastore().get_schema().get_element_type(value.type_bound());
  }

  public element_reference(value_wrapper<list<value_wrapper>> value, int index) {
    this((list_wrapper) value, index);
  }

  private datastore_state get_datastore() {
    return (datastore_state) value.zone();
  }

  @Override
  public type_id type_bound() {
    return get_datastore().get_schema().get_mutable_reference(value_type_bound());
  }

  @Override
  public type_id value_type_bound() {
    return element_type;
  }

  @Override
  public void init(value_wrapper new_value) {
    // TODO: check that the value wasn't initialized.
    set(new_value);
  }

  @Override
  public value_wrapper get() {
    list<value_wrapper> elements = value.unwrap();
    value_wrapper result = index < elements.size() ? elements.get(index) : null;
    if (result == null) {
      result = get_datastore().make_default_value(value_type_bound());
    }
    return result;
  }

  @Override
  public void set(value_wrapper new_value) {
    list<value_wrapper> elements = value.unwrap();
    if (new_value == null && index >= elements.size()) {
      // Do not extend the list for null values.
      return;
    }
    while (elements.size() < index) {
      elements.append(null);
    }
    if (elements.size() == index) {
      elements.append(new_value);
    } else {
      // the index'th element exists in elements.
      elements.at(index).set(new_value);
    }
    value.zone().mark_modified();
  }

  @Override
  public string to_string() {
    return utilities.describe(this, value);
  }
}
