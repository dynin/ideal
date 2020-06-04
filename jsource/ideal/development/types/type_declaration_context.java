/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;

public interface type_declaration_context extends value {
  graph<principal_type, origin> type_graph();
  void declare_type(principal_type the_type, declaration_pass pass);
  boolean is_subtype_of(abstract_value the_value, type the_type);
}
