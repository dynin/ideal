/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.marshallers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.showcase.coach.reflections.name;

import javax.annotation.Nullable;

import com.google.gson.*;

/**
 * Methods for marshalling and unmarshalling state.
 */
public class json_data {

  private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

  final JsonObject the_object;

  public json_data() {
    this.the_object = new JsonObject();
  }

  json_data(JsonObject the_object) {
    this.the_object = the_object;
  }

  public string get(name property) {
    if (the_object.has(property.s())) {
      return new base_string(the_object.getAsJsonPrimitive(property.s()).getAsString());
    } else {
      return null;
    }
  }

  public void add(name property, String value) {
    the_object.add(property.s(), new JsonPrimitive(value));
  }

  public void add(name property, string value) {
    the_object.add(property.s(), new JsonPrimitive(utilities.s(value)));
  }

  public void add(name property, json_data value) {
    the_object.add(property.s(), value.the_object);
  }

  public string stringify() {
    return new base_string(gson.toJson(the_object));
  }

  public static @Nullable json_data parse(string json) {
    if (json != null) {
      return parse(utilities.s(json));
    } else {
      return null;
    }
  }

  public static @Nullable json_data parse(String json) {
    if (json != null) {
      JsonElement element;
      try {
        element = gson.fromJson(json, JsonElement.class);
      } catch (Exception e) {
        return null;
      }
      if (element instanceof JsonObject) {
        return new json_data((JsonObject) element);
      }
    }
    return null;
  }
}
