/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.declarations;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.comments.*;
import ideal.development.modifiers.*;

import javax.annotation.Nullable;

public interface annotation_set extends immutable_data {
  access_modifier access_level();
  boolean has(modifier_kind the_kind);
  @Nullable documentation the_documentation();
}
