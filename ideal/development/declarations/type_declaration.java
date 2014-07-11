/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.declarations;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
// TODO: remove this dependency.
import ideal.development.types.declaration_pass;

public interface type_declaration extends declaration {
  kind get_kind();
  action_name short_name();
  annotation_set annotations();
  principal_type get_declared_type();
  principal_type declared_in_type();
  // TODO: this may be a misleading name for this method, rename.
  void process_declaration(declaration_pass pass);
  readonly_list<declaration> get_signature();
}
