/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.machine.annotations.dont_display;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;

import javax.annotation.Nullable;

public abstract class single_pass_analyzer extends base_analyzer {

  @dont_display
  private @Nullable analysis_result saved_result;

  public single_pass_analyzer(origin the_origin) {
    super(the_origin);
  }

  protected single_pass_analyzer(origin the_origin, @Nullable principal_type parent,
      @Nullable analysis_context context) {
    super(the_origin, parent, context);
  }

  public principal_type declared_in_type() {
    return parent();
  }

  protected abstract analysis_result do_single_pass_analysis();

  protected boolean has_saved_result() {
    return saved_result != null;
  }

  protected void set_saved_result(analysis_result saved_result) {
    assert this.saved_result == null;
    assert saved_result != null;

    this.saved_result = saved_result;

    if (saved_result instanceof error_signal) {
      ((error_signal) saved_result).report_not_cascading();
    }
  }

  @Override
  protected void handle_error(error_signal the_error_signal) {
    super.handle_error(the_error_signal);
    if (saved_result == null) {
      saved_result = the_error_signal;
    }
  }

  @Override
  public final analysis_result analyze() {
    if (saved_result == null) {
      set_saved_result(do_single_pass_analysis());
    }
    return saved_result;
  }

  @Override
  public boolean has_errors() {
    return saved_result instanceof error_signal;
  }
}
