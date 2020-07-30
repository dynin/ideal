/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.extensions;

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
import ideal.development.analyzers.*;

/**
 * Marks a procedure or a variable as "not yet implemented", meaning it's excluded
 * from the generated code.
 */
public class not_yet_implemented_extension extends declaration_extension {

  public not_yet_implemented_extension() {
    super("not_yet_implemented");
  }

  private @Nullable error_signal skip_declaration(analysis_pass pass) {
    if (pass == analysis_pass.TARGET_DECL) {
      set_expanded(null);
    }

    return null;
  }

  @Override
  protected @Nullable error_signal process_procedure(procedure_analyzer the_procedure,
      analysis_pass pass) {
    return skip_declaration(pass);
  }

  @Override
  protected @Nullable error_signal process_variable(variable_analyzer the_variable,
      analysis_pass pass) {
    return skip_declaration(pass);
  }

  @Override
  protected @Nullable error_signal process_type_declaration(
      type_declaration_analyzer the_type_declaration, analysis_pass pass) {
    return skip_declaration(pass);
  }
}
