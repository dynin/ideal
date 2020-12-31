/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;

public class entity_action extends base_action implements entity_wrapper {
  private final entity_wrapper the_entity;

  public entity_action(entity_wrapper the_entity, origin the_origin) {
    super(the_origin);
    this.the_entity = the_entity;
  }

  @Override
  public abstract_value result() {
    return action_utilities.to_type(the_entity.type_bound());
  }

  @Override
  public entity_wrapper execute(execution_context context) {
    return the_entity;
  }

  @Override
  public type_id type_bound() {
    return the_entity.type_bound();
  }
}
