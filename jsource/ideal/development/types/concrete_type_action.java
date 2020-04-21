/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class concrete_type_action extends type_action {

  private final type the_type;

  concrete_type_action(type the_type, position source) {
    super(source);
    assert the_type != null;
    this.the_type = the_type;
    // This may happen if we are creating a concrete type action in the import.
    // TODO: cleaner way to handle this.
    if (false && the_type == core_types.error_type()) {
      utilities.panic("Error action that's not an error_signal");
    }
  }

  @Override
  public type get_type() {
    return the_type;
  }
}
