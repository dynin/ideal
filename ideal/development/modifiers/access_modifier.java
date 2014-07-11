/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.modifiers;

import ideal.development.elements.*;

public class access_modifier extends base_modifier_kind {
  public static access_modifier public_modifier = new access_modifier("public");
  public static access_modifier private_modifier = new access_modifier("private");
  public static access_modifier protected_modifier = new access_modifier("protected");
  public static access_modifier local_modifier = new access_modifier("local");

  private access_modifier(String name) {
    super(name);
  }
}
