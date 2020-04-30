/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.appengine;

import ideal.library.elements.*;
import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;
import ideal.runtime.channels.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.showcase.coach.reflections.*;
import ideal.showcase.coach.forms.*;
import ideal.showcase.coach.common.*;

/**
 * The boilerplate for an ideal framework request handler.
 */
public abstract class base_handler {

  protected final fluid_context request;
  protected final user_state state;

  public base_handler(user_state state, fluid_context request) {
    this.request = request;
    this.state = state;
  }

  protected abstract dictionary<string, procedure_id> get_handlers();

  public abstract text_content widget_to_html(widget content);

  public abstract text_content show_error_message(error_message message);

  protected datastore_state get_world() {
    return state.get_world_state();
  }

  protected datastore_schema get_schema() {
    return get_world().get_schema();
  }

  protected string get_app_version() {
    return get_schema().version;
  }

  public @Nullable procedure0 get_action() {
    @Nullable immutable_list<string> action = request.action;

    if (action == null) {
      return null;
    }

    assert action.size() > 0;

    procedure_id procedure = get_handlers().get(action.get(0));

    if (procedure == null) {
      return null;
    }

    if (procedure instanceof procedure0) {
      assert action.size() == 1;
      return (procedure0) procedure;
    }

    assert action.size() == 2;
    string second = action.get(1);
    assert second != null;

    if (procedure instanceof procedure1_data_type) {
      return ((procedure1_data_type) procedure).bind(get_schema().lookup_data_type(second));
    } else if (procedure instanceof procedure1_data_value) {
      return ((procedure1_data_value) procedure).bind(get_world().get_data_by_id(second));
    }

    throw new RuntimeException("Uknown procedure type " + procedure);
  }

  protected void update_world_state(datastore_state new_world) {
    assert new_world == get_world();
    state.set_world_state(new_world);
  }

  protected void replace_world_state(datastore_state new_world) {
    state.set_world_state(new_world);
  }

  protected void checkpoint_world_state(datastore_state new_world) {
    state.checkpoint_world_state(new_world);
  }

  protected translation_result translate_source(string new_source) {
    return state.translate_source(new_source);
  }
}
