/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tools;

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

public abstract class type_processor_target extends target_value {

  public type_processor_target(simple_name the_name) {
    super(the_name);
  }

  protected abstract void setup(create_manager the_manager, analysis_context the_context);

  protected abstract void process_type(principal_type the_type);

  @Override
  public void process(action_parameters parameters, create_manager the_manager,
      analysis_context the_context) {

    setup(the_manager, the_context);

    immutable_list<action> actions = parameters.params();

    for (int i = 0; i < actions.size(); ++i) {
      abstract_value type_value = actions.get(i).result();
      if (! (type_value instanceof principal_type)) {
        // TODO: use notification with a source display...
	log.error("Expected type, found " + type_value);
	return;
      }
      process_type((principal_type) type_value);
    }
  }
}
