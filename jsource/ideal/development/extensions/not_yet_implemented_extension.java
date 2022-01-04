/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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

  public static final not_yet_implemented_extension instance = new not_yet_implemented_extension();

  public not_yet_implemented_extension() {
    super("not_yet_implemented");
  }

  private signal skip_declaration(analysis_pass pass) {
    if (pass == analysis_pass.TARGET_DECL) {
      set_expanded(new empty<declaration>());
    }

    return ok_signal.instance;
  }

  @Override
  protected signal process_procedure(procedure_analyzer the_procedure, analysis_pass pass) {
    return skip_declaration(pass);
  }

  @Override
  protected signal process_variable(variable_analyzer the_variable, analysis_pass pass) {
    return skip_declaration(pass);
  }

  @Override
  protected signal process_type_declaration(type_declaration_analyzer the_type_declaration,
      analysis_pass pass) {
    return skip_declaration(pass);
  }
}
