/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.kinds;

import ideal.development.elements.*;

public class subtype_tags {

  /// Generic subtyping tag, used in ideal library.
  public static final subtype_tag subtypes_tag = new base_subtype_tag("subtypes");

  public static final subtype_tag extends_tag = new base_subtype_tag("extends");

  public static final subtype_tag implements_tag = new base_subtype_tag("implements");

  public static final subtype_tag refines_tag = new base_subtype_tag("refines");

  public static final subtype_tag aliases_tag = new base_subtype_tag("aliases");
}
