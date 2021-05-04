/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
 * Mark a procedure as a test case.
 */
public class test_case_extension extends declaration_extension {

  public static final test_case_extension instance = new test_case_extension();

  /**
   * The name of the extension, which is used as the modifier in the ideal source code.
   */
  public test_case_extension() {
    super("test_case");
  }

  @Override
  protected signal process_procedure(procedure_analyzer the_procedure, analysis_pass pass) {

    if (pass.is_before(analysis_pass.PREPARE_METHOD_AND_VARIABLE)) {
      return analyze(the_procedure, pass);
    }

    if (pass.is_after(analysis_pass.PREPARE_METHOD_AND_VARIABLE)) {
      return ok_signal.instance;
    }

    assert pass == analysis_pass.PREPARE_METHOD_AND_VARIABLE;
    origin the_origin = this;

    if (!the_procedure.has_return()) {
      the_procedure.set_return(common_library.get_instance().void_type());
    }

    set_expanded(the_procedure);

    return ok_signal.instance;
  }
}
