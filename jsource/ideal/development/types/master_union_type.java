/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class master_union_type extends master_type {

  public master_union_type(action_name short_name, kind the_kind) {
    super(short_name, the_kind);
  }

  public parametrized_type make_parametrized() {
    return new parametrized_union_type(this);
  }
}
