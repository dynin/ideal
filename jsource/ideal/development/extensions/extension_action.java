/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.extensions;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.actions.*;

public class extension_action extends base_action {
  public final action extended_action;

  public extension_action(action extended_action, extension_analyzer the_analyzer) {
    super(the_analyzer);
    this.extended_action = extended_action;
  }

  public extension_analyzer get_extension() {
    return (extension_analyzer) deeper_origin();
  }

  @Override
  public abstract_value result() {
    return extended_action.result();
  }

  @Override
  public boolean has_side_effects() {
    return extended_action.has_side_effects();
  }

  @Override
  public entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    return extended_action.execute(from_entity, context);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, extended_action);
  }
}
