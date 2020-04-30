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
import ideal.runtime.reflections.*;

/**
 * Simple identifier for use in fluid_servlet.
 */
public class simple_variable_id implements variable_id {
  private String id;
  private type_id value_type;
  private type_id reference_type;

  public simple_variable_id(String id, type_id value_type, type_id reference_type) {
    this.id = id;
    this.value_type = value_type;
    this.reference_type = reference_type;
  }

  @Override
  public identifier short_name() {
    return new name(id);
  }

  @Override
  public type_id value_type() {
    return value_type;
  }

  @Override
  public type_id reference_type() {
    return reference_type;
  }

  @Override
  public string to_string() {
    return new base_string(id);
  }

  @Override
  public String toString() {
    return id;
  }
}
