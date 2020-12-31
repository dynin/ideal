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
public class datastore_state extends debuggable {
  private final datastore_schema schema;
  private int next_id;
  private final dictionary<string, data_value> data;
  private @Nullable string source_version;
  private @Nullable string version_id;
  private static int instance_id;

  public datastore_state(datastore_schema schema, @Nullable string source_version,
      @Nullable string version_id) {
    this.schema = schema;
    this.next_id = 0;
    this.data = new hash_dictionary<string, data_value>();
    this.source_version = source_version;
    this.version_id = version_id;
  }

  public readonly_collection<data_value> get_data() {
    return data.values();
  }

  public string get_version_id() {
    if (version_id == null) {
      version_id = new base_string(schema.version + "/" + System.currentTimeMillis() + "/" +
          (instance_id++));
    }
    return version_id;
  }

  public string get_source_version() {
    if (source_version == null) {
      source_version = schema.version;
    }
    return source_version;
  }

  public void reset_source_version() {
    source_version = schema.version;
  }

  // The version id can be reset after the world is unmarshalled.
  public void set_version_id(@Nullable string version_id) {
    this.version_id = version_id;
  }

  public void mark_modified() {
    version_id = null;
  }

  // Internal use only.
  public void do_add_data(data_value d) {
    assert d != null;
    string id = d.get_data_id();
    assert id != null;
    data.put(id, d);
    mark_modified();
  }

  public void add_data(data_value d) {
    assert d != null;
    string id = get_next_id();
    d.put_var(schema.data_id_field(), schema.new_string(id));
    data.put(id, d);
    mark_modified();
  }

  public list<data_value> of_type(data_type dt) {
    immutable_list<data_value> all_data = get_data().elements();
    list<data_value> result = new base_list<data_value>();
    for (int i = 0; i < all_data.size(); ++i) {
      if (all_data.get(i).get_type() == dt) {
        result.append(all_data.get(i));
      }
    }
    return result;
  }

  private string get_next_id() {
    string string_id;

    do {
      int id = next_id++;
      string_id = new base_string("id:" + id);
    } while (data.contains_key(string_id));

    return string_id;
  }

  public datastore_schema get_schema() {
    return schema;
  }

  public data_value get_data_by_id(string s) {
    return data.get(s);
  }

  public value_wrapper make_default_value(type_id the_type_id) {
    if (the_type_id == schema.immutable_string_type()) {
      return schema.new_string(new base_string(""));
    } else if (schema.is_data_type(the_type_id)) {
      return null;
    } else if (schema.is_enum_type(the_type_id)) {
      return schema.get_enum_type(the_type_id).get_values().get(0);
    } else if (schema.is_list_type(the_type_id)) {
      return new list_wrapper(new base_list<value_wrapper>(), (type) the_type_id, this);
    }

    utilities.panic("Unknown type: " + the_type_id);
    return null;
  }

  @Override
  public string to_string() {
    return new base_string("world value of ", schema.short_name().to_string());
  }
}
