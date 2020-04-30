/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.modifiers;

import ideal.development.elements.*;

public class variance_modifier extends base_modifier_kind {

  public static variance_modifier invariant_modifier = new variance_modifier("invariant");
  public static variance_modifier covariant_modifier = new variance_modifier("covariant");
  public static variance_modifier contravariant_modifier = new variance_modifier("contravariant");
  public static variance_modifier combivariant_modifier = new variance_modifier("combivariant");

  private variance_modifier(String name) {
    super(name);
  }
}
