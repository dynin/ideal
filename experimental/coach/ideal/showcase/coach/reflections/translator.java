/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.reflections;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.channels.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.scanners.*;
import ideal.development.actions.*;
import ideal.development.analyzers.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.tools.*;
import ideal.development.declarations.*;
import ideal.development.origins.*;

import javax.annotation.Nullable;
public class translator {

  public final create_manager creator;

  public translator(resource_catalog resources) {
    creator = new create_manager(resources);
    if (!creator.is_bootstrapped()) {
      creator.process_bootstrap(true);
    }
  }

  public translation_result translate_source(source_content source, string name,
      @Nullable string version_suffix) {
    analysis_context context = creator.get_analysis_context();
    principal_type parent = creator.new_block(name, context);
    list<notification> notifications = new base_list<notification>();
    creator.set_notification_handler(new appender<notification>(notifications));
    type_declaration_analyzer world_declaration =
        reflect_util.get_world(creator, source, parent, context);

    boolean compile_ok = notifications.is_empty() && world_declaration != null;

    if (compile_ok) {
      execution_context exec_context = creator.new_execution_context();
      analyzer_utilities.to_action(world_declaration).execute(null_wrapper.instance, exec_context);
      datastore_schema new_world_schema =
          new datastore_schema(world_declaration, source, exec_context);
      if (version_suffix != null) {
        new_world_schema.version = new base_string(new_world_schema.version, version_suffix);
      }
      return new translation_result(new_world_schema);
    } else {
      text_fragment error_messages = text_utilities.EMPTY_FRAGMENT;
      for (int i = 0; i < notifications.size(); ++i) {
        error_messages = text_utilities.join(error_messages,
            notifications.get(i).render_text(false));
      }
      return new translation_result(error_messages);
    }
  }
}
