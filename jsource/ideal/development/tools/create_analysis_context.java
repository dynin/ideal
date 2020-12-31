/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tools;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.declarations.*;

public class create_analysis_context extends base_analysis_context {

  private final create_manager manager;

  create_analysis_context(create_manager manager, base_semantics language) {
    super(language);
    this.manager = manager;
  }

  @Override
  public @Nullable readonly_list<construct> load_type_body(
      type_announcement_construct the_declaration) {
    return manager.load_type_body(the_declaration);
  }
}
