/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.modifiers.*;

public class cache_extension extends declaration_extension {

  /**
   * The name of the extension, which is used as the modifier in the ideal source code.
   */
  public cache_extension() {
    super("cache");
  }

  @Override
  protected @Nullable error_signal process_procedure(procedure_analyzer the_procedure,
      analysis_pass pass) {
    return new error_signal(new base_string("Cache extension is a work in progress"), this);
  }
}
