/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
import ideal.development.constructs.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.declarations.*;

public class block_action extends base_action {

  public block_action(block_declaration the_block) {
    super(the_block);
  }

  @Override
  public block_declaration get_declaration() {
    return (block_declaration) deeper_origin();
  }

  @Override
  public abstract_value result() {
    return get_declaration().get_body_action().result();
  }

  @Override
  public boolean has_side_effects() {
    return get_declaration().get_body_action().has_side_effects();
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context exec_context) {
    return get_declaration().get_body_action().execute(from_entity, exec_context);
  }
}
