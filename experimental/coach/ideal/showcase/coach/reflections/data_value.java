/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
public class data_value extends debuggable implements composite_wrapper {
  private final data_type type;
  private final datastore_state world;
  private final base_composite_value value;
  private @Nullable String cached_name;

  data_value(data_type type, datastore_state world) {
    this.type = type;
    this.world = world;
    this.value = new base_composite_value(type.value_type());
    this.cached_name = null;

    readonly_list<variable_id> fields = type.get_fields();
    for (int i = 0; i < fields.size(); ++i) {
      variable_id the_field = fields.get(i);
      put_var(the_field, world.make_default_value(the_field.value_type()));
    }
  }

  public void mark_modified() {
    world.mark_modified();
    this.cached_name = null;
  }

  public any_composite_value unwrap() {
    return value;
  }

  public datastore_state get_world() {
    return world;
  }

  @Override
  public type type_bound() {
    return type.value_type();
  }

  public data_type get_type() {
    return type;
  }

  @Override
  public string to_string() {
    return new base_string(get_name());
  }

  @Override
  public void put_var(variable_id key, value_wrapper value) {
    this.value.put_var(key, value);
    // TODO: verify that the value actually changed...
    mark_modified();
  }

  @Override
  public value_wrapper get_var(variable_id key) {
    return this.value.get_var(key);
  }

  public string get_data_id() {
    return ((string_value) value.get_var(world.get_schema().data_id_field())).unwrap();
  }

  public String get_name() {
    if (cached_name == null) {
      cached_name = utilities.s(type.render_name(this));
    }
    return cached_name;
  }

  public readonly_list<field_reference> get_fields() {
    readonly_list<variable_id> fields = type.get_fields();
    list<field_reference> result = new base_list<field_reference>();
    for (int i = 0; i < fields.size(); ++i) {
      result.append(new field_reference(this, fields.get(i)));
    }
    return result;
  }
}
