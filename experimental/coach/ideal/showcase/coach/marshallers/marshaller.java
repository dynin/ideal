/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.marshallers;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.values.*;
import ideal.showcase.coach.reflections.*;
import ideal.showcase.coach.common.sorter;

import javax.annotation.Nullable;

import java.util.Comparator;
import com.google.gson.*;

/**
 * Methods for marshalling and unmarshalling state.
 */
public class marshaller {

  public static final name SOURCE_VERSION = new name("source_version");
  public static final name VERSION_ID = new name("version_id");
  public static final name ORIGINAL_VERSION_ID = new name("original_version_id");

  private static final String DATA_TYPE = "data_type";
  private static final String DATA = "data";
  private static final String WORLD_TYPE = "world_type";
  private static final String DATA_ID = datastore_schema.DATA_ID;

  private static final String NAME = "name";
  private static final String FIELDS = "fields";
  private static final String VALUES = "values";
  private static final String VERSION = "version";
  private static final String DATA_TYPES = "data_types";
  private static final String ENUM_TYPES = "enum_types";

  private final datastore_schema the_schema;

  public marshaller(datastore_schema the_schema) {
    this.the_schema = the_schema;
  }

  public json_data marshal_schema() {
    JsonObject obj = new JsonObject();
    obj.add(VERSION, to_json_string(the_schema.version));
    obj.add(DATA_TYPES, make_data_types());
    obj.add(ENUM_TYPES, make_enum_types());
    return new json_data(obj);
  }

  public json_data marshal_state(datastore_state world) {
    assert world.get_schema() == the_schema;

    JsonObject obj = new JsonObject();
    obj.add(WORLD_TYPE, to_json_string(the_schema.short_name().to_string()));
    obj.add(SOURCE_VERSION.s(), to_json_string(the_schema.version));
    obj.add(VERSION_ID.s(), to_json_string(world.get_version_id()));

    readonly_list<composite_data_value> composite_data_values = world.get_data().elements();
    composite_data_values = sorter.sort(composite_data_values, data_ordering);

    JsonArray values = new JsonArray();
    for (int i = 0; i < composite_data_values.size(); ++i) {
      values.add(to_json_full_state(composite_data_values.get(i)));
    }
    obj.add(DATA, values);

    return new json_data(obj);
  }

  public @Nullable datastore_state unmarshal_state(json_data the_data) {
    if (the_data == null) {
      return null;
    }

    JsonObject obj = the_data.the_object;

    String source_version = null;
    if (obj.has(SOURCE_VERSION.s())) {
      source_version = obj.getAsJsonPrimitive(SOURCE_VERSION.s()).getAsString();
    }

    string version_id;
    if (obj.has(VERSION_ID.s())) {
      version_id = new base_string(obj.getAsJsonPrimitive(VERSION_ID.s()).getAsString());
    } else {
      // Compatibility with old data format
      version_id = new base_string(the_schema.version, "/null");
    }

    datastore_state world = the_schema.new_value(new base_string(source_version), version_id);

    JsonArray values = obj.getAsJsonArray(DATA);

    // We need to do this in two passes to handle forward references correctly.
    // First pass: create data values with IDs only
    for (JsonElement val : values) {
      if (val instanceof JsonObject) {
        JsonObject val_object = (JsonObject) val;
        String val_type = val_object.get(DATA_TYPE).getAsString();
        data_type dt = the_schema.lookup_data_type(new base_string(val_type));
        if (dt == null) {
          continue;
        }
        composite_data_value dv = dt.new_value(world);
        string_value id = from_json_string(val_object.get(DATA_ID));
        dv.put_var(the_schema.data_id_field(), id);
        world.do_add_data(dv);
      }
    }

    // Second pass: parse data values
    for (JsonElement val : values) {
      if (val instanceof JsonObject) {
        JsonObject val_object = (JsonObject) val;
        string_value id = from_json_string(val_object.get(DATA_ID));
        composite_data_value dv = world.get_data_by_id(id.unwrap());
        if (dv != null) {
          read_from_json(dv, val_object, world);
        }
      }
    }

    // put_field()s might have reset the version_id, so we set it again.
    world.set_version_id(version_id);

    return world;
  }

  private JsonElement to_json_value(value_wrapper v) {
    if (v instanceof composite_data_value) {
      return to_json_data((composite_data_value) v);
    } else if (v instanceof enum_value) {
      return to_json_enum((enum_value) v);
    } else if (v instanceof list_wrapper) {
      return to_json_list((list_wrapper) v);
    } else if (v instanceof string_value) {
      return to_json_string(((string_value) v).unwrap());
    } else if (v == null) {
      return JsonNull.INSTANCE;
    }

    throw new RuntimeException();
  }

  private JsonPrimitive to_json_string(string s) {
    return new JsonPrimitive(utilities.s(s));
  }

  private JsonPrimitive to_json_data(composite_data_value dv) {
    return new JsonPrimitive(utilities.s(dv.get_data_id()));
  }

  private String field_name(field_reference field) {
    return utilities.s(field.short_name().to_string());
  }

  private JsonObject to_json_full_state(composite_data_value dv) {
    JsonObject obj = new JsonObject();
    obj.add(DATA_TYPE, to_json_string(dv.get_type().short_name().to_string()));
    obj.add(DATA_ID, new JsonPrimitive(utilities.s(dv.get_data_id())));
    readonly_list<field_reference> all_fields = dv.get_fields();
    for (int i = 0; i < all_fields.size(); ++i) {
      field_reference field = all_fields.get(i);
      value_wrapper val = field.get();
      if (val != null) {
        obj.add(field_name(field), to_json_value(val));
      }
    }
    return obj;
  }

  private static final Comparator<composite_data_value> data_ordering = new Comparator<composite_data_value>() {
    @Override
    public int compare(composite_data_value d1, composite_data_value d2) {
      // First order by type
      String t1 = utilities.s(d1.get_type().short_name().to_string());
      String t2 = utilities.s(d2.get_type().short_name().to_string());
      int comp = t1.compareTo(t2);

      if (comp == 0) {
        // Then order by id
        comp = utilities.s(d1.get_data_id()).compareTo(utilities.s(d2.get_data_id()));
      }

      return comp;
    }
  };

  private JsonPrimitive to_json_enum(enum_value ev) {
    return new JsonPrimitive(utilities.s(ev.short_name().to_string()));
  }

  private JsonArray to_json_list(list_wrapper the_list) {
    JsonArray result = new JsonArray();
    list<value_wrapper> elements = the_list.unwrap();
    for (int i  = 0; i < elements.size(); ++i) {
      result.add(to_json_value(elements.get(i)));
    }
    return result;
  }

  private value_wrapper from_json(type_id the_type_id, JsonElement element,
      datastore_state world) {

    if (the_type_id == the_schema.immutable_string_type()) {
      return from_json_string(element);
    } else if (the_schema.is_data_type(the_type_id)) {
      return from_json_ref(the_schema.get_data_type(the_type_id), element, world);
    } else if (the_schema.is_enum_type(the_type_id)) {
      return enum_from_string(the_schema.get_enum_type(the_type_id),
          ((JsonPrimitive) element).getAsString());
    } else if (the_schema.is_list_type(the_type_id)) {
      return list_from_json(the_type_id, element, world);
    } else {
      utilities.panic("Unknown type: " + the_type_id);
      return null;
    }
  }

  private string_value from_json_string(JsonElement element) {
    return the_schema.new_string(new base_string(((JsonPrimitive) element).getAsString()));
  }

  private composite_data_value from_json_ref(data_type dt, JsonElement element, datastore_state world) {
    if (element instanceof JsonNull) {
      return null;
    } else {
      composite_data_value result = world.get_data_by_id(new base_string(element.getAsString()));
      assert result != null;
      return result;
    }
  }

  private enum_value enum_from_string(enum_type the_type, String value) {
    string string_value = new base_string(value);
    readonly_list<enum_value> the_values = the_type.get_values();
    for (int i = 0; i < the_values.size(); ++i) {
      enum_value the_enum_value = the_values.get(i);
      if (utilities.eq(string_value, the_enum_value.short_name().to_string())) {
        return the_enum_value;
      }
    }

    // This shouldn't really happen...
    // TODO: log error.
    return the_type.get_values().get(0);
  }

  private void read_from_json(composite_data_value dv, JsonObject obj, datastore_state world) {
    readonly_list<field_reference> all_fields = dv.get_fields();
    for (int i = 0; i < all_fields.size(); ++i) {
      field_reference field = all_fields.get(i);
      JsonElement field_value = obj.get(field_name(field));
      if (field_value != null) {
        field.set(from_json(field.value_type_bound(), field_value, world));
      }
    }
  }

  private list_wrapper list_from_json(type_id the_type_id, JsonElement element,
      datastore_state world) {
    type_id element_type = the_schema.get_element_type(the_type_id);
    list<value_wrapper> elements = new base_list<value_wrapper>();
    if (element.isJsonArray()) {
      for (JsonElement arrayElement : element.getAsJsonArray()) {
        elements.append(from_json(element_type, arrayElement, world));
      }
    }
    return new list_wrapper(elements, (type) the_type_id, world);
  }

  private String make_string_name(type_id the_type_id) {
    if (the_type_id == the_schema.immutable_string_type()) {
      return "string";
    } else if (the_schema.is_enum_type(the_type_id) || the_schema.is_data_type(the_type_id)) {
      return utilities.s(the_type_id.short_name().to_string());
    } else if (the_schema.is_list_type(the_type_id)) {
      return "list/" + make_string_name(the_schema.get_element_type(the_type_id));
    } else {
      utilities.panic("Unknown type: " + the_type_id);
      return null;
    }
  }

  private JsonPrimitive make_json_name(type_id the_type_id) {
    return new JsonPrimitive(make_string_name(the_type_id));
  }

  private JsonElement make_id(identifier id) {
    return new JsonPrimitive(utilities.s(id.to_string()));
  }

  private JsonElement make_data_type(data_type dt) {
    JsonObject obj = new JsonObject();
    obj.add(NAME, make_json_name(dt.value_type()));
    JsonArray fields = new JsonArray();
    readonly_list<variable_id> all_fields = dt.get_fields();
    for (int i = 0; i < all_fields.size(); ++i) {
      variable_id the_field = all_fields.get(i);
      JsonArray field_info = new JsonArray();
      field_info.add(make_id(the_field.short_name()));
      field_info.add(make_json_name(the_field.value_type()));
      fields.add(field_info);
    }
    obj.add(FIELDS, fields);
    return obj;
  }

  private JsonElement make_data_types() {
    JsonArray result = new JsonArray();
    readonly_list<data_type> all_data_types = the_schema.all_data_types();
    for (int i = 0; i < all_data_types.size(); ++i) {
      result.add(make_data_type(all_data_types.get(i)));
    }
    return result;
  }

  private JsonElement make_enum_type(enum_type et) {
    JsonObject obj = new JsonObject();
    obj.add(NAME, make_json_name(et.value_type()));
    JsonArray values = new JsonArray();
    readonly_list<enum_value> the_values = et.get_values();
    for (int i = 0; i < the_values.size(); ++i) {
      enum_value the_enum_value = the_values.get(i);
      values.add(make_id(the_enum_value.short_name()));
    }
    obj.add(VALUES, values);
    return obj;
  }

  private JsonElement make_enum_types() {
    JsonArray result = new JsonArray();
    readonly_list<enum_type> all_enum_types = the_schema.all_enum_types();
    for (int i = 0; i < all_enum_types.size(); ++i) {
      result.add(make_enum_type(all_enum_types.get(i)));
    }
    return result;
  }
}
