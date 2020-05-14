/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.values.*;

public class list_initializer_action extends base_action {
  public final type element_type;
  public final readonly_list<action> parameter_actions;
  public final type result_type;

  public list_initializer_action(type element_type, readonly_list<action> parameter_actions,
      origin the_origin) {
    super(the_origin);
    this.element_type = element_type;
    this.parameter_actions = parameter_actions;

    type_parameters list_parameters = new type_parameters(
        new base_list<abstract_value>(element_type));
    result_type = common_library.get_instance().list_type().bind_parameters(list_parameters).
        get_flavored(flavors.immutable_flavor);
  }

  @Override
  public abstract_value result() {
    return result_type;
  }

  @Override
  public entity_wrapper execute(execution_context the_context) {
    // TODO: instantiate a typed list...
    utilities.panic("list_initializer_action not implemented");
    return null;
  }
}
