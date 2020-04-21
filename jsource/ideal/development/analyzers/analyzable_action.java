/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.declarations.*;

public class analyzable_action implements analyzable {
  private final action the_action;
  private final position pos;

  public analyzable_action(action the_action, position pos) {
    if (the_action instanceof analyzable) {
      // TODO: may be do a static method that enforces this?
      utilities.panic("Don't wrap " + the_action);
    }
    this.the_action = the_action;
    this.pos = pos;
  }

  public static analyzable_action from_value(abstract_value value, position pos) {
    return new analyzable_action(value.to_action(pos), pos);
  }

  @Override
  public action analyze() {
    return the_action;
  }

  @Override
  public analyzable specialize(specialization_context context, principal_type new_parent) {
    return this;
  }

  @Override
  public position source_position() {
    return pos;
  }

  public static analyzable_action nothing(position pos) {
    return from_value(common_library.get_instance().void_instance(), pos);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_action);
  }
}
