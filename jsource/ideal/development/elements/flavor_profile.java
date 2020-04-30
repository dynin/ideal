/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.elements;

import ideal.library.elements.*;

/**
 * Specifies what flavors of the types can and can't be used.
 * For example, for namespaces, everything is mapped to |flavors.nameonly_flavor|.
 * For enums, |flavors.mutable_flavor| is mapped to |flavors.deeply_immutable_flavor|.
 */
public interface flavor_profile extends readonly_data {
  type_flavor default_flavor();
  boolean supports(type_flavor flavor);
  type_flavor map(type_flavor from);
  immutable_list<type_flavor> supported_flavors();
}
