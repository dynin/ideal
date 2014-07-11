/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.kinds;

import ideal.development.elements.*;

public class supertype_kinds {

  public static final supertype_kind extends_kind = new base_supertype_kind("extends");

  public static final supertype_kind implements_kind = new base_supertype_kind("implements");

  public static final supertype_kind refines_kind = new base_supertype_kind("refines");

  public static final supertype_kind aliases_kind = new base_supertype_kind("aliases");
}
