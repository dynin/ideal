/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.annotations.dont_display;
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

  public single_pass_analyzer(position source) {
    super(source);
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
      maybe_report_error((error_signal) saved_result);
    }
  }

  @Override
  protected void handle_error(error_signal signal) {
    this.saved_result = signal;
    maybe_report_error(signal);
  }

  @Override
  public final analysis_result analyze() {
    if (saved_result == null) {
      set_saved_result(do_single_pass_analysis());
    }
    return saved_result;
  }
}
