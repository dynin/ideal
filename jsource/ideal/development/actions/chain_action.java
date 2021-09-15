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

public class chain_action extends base_action {
  public final action first;
  public final action second;

  public chain_action(action first, action second, origin the_origin) {
    super(the_origin);
    assert first != null;
    assert second != null;
    this.first = first;
    this.second = second;
  }

  @Override
  public @Nullable declaration get_declaration() {
    declaration the_declaration = second.get_declaration();
    if (the_declaration == null) {
      the_declaration = first.get_declaration();
    }
    return the_declaration;
  }

  @Override
  public abstract_value result() {
    return second.result();
  }

  @Override
  public boolean has_side_effects() {
    return first.has_side_effects() || second.has_side_effects();
  }

  @Override
  public action bind_from(action from, origin the_origin) {
    action new_first = action_utilities.combine(from, first, the_origin);
    if (new_first != first || deeper_origin() != the_origin) {
      return new chain_action(new_first, second, the_origin);
    } else {
      return this;
    }
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    entity_wrapper first_entity = first.execute(from_entity, context);
    if (first_entity instanceof jump_wrapper) {
      return first_entity;
    }

    return second.execute(first_entity, context);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, new base_string(first + " . " + second));
  }
}
