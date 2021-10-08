/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.targets;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.reflections.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.transformers.*;
import ideal.development.analyzers.*;
import ideal.development.notifications.*;

public abstract class type_processor_target extends target_value {

  protected final target_manager the_manager;

  public type_processor_target(simple_name the_name, target_manager the_manager) {
    super(the_name);
    this.the_manager = the_manager;
  }

  public abstract void setup(action_context the_context);

  public abstract void process_type(principal_type the_type);

  public abstract void finish_processing();

  @Override
  public void process(action_parameters parameters, action_context the_context) {

    setup(the_context);

    immutable_list<action> actions = parameters.params();
    list<principal_type> types = new base_list<principal_type>();

    for (int i = 0; i < actions.size(); ++i) {
      abstract_value type_value = actions.get(i).result();
      if (! (type_value instanceof principal_type)) {
        // TODO: use notification with a source display...
        log.error("Expected type, found " + type_value);
        return;
      }

      principal_type the_type = type_value.type_bound().principal();
      analyzer_utilities.analyze_and_prepare(the_type);
      types.append(the_type);
    }

    if (the_manager.has_errors()) {
      return;
    }

    for (int i = 0; i < types.size(); ++i) {
      process_type(types.get(i));
    }

    finish_processing();
  }
}
