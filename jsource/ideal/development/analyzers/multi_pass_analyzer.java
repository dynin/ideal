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

public abstract class multi_pass_analyzer<C extends origin> extends base_analyzer<C> {

  protected analysis_pass last_pass;
  private @Nullable error_signal last_error;
  protected boolean in_progress;

  protected multi_pass_analyzer(C source, @Nullable principal_type parent,
        @Nullable analysis_context context) {
    super(source, parent, context);

    last_pass = analysis_pass.BEFORE_EVALUATION;
    last_error = null;

    if (trace_analysis()) {
      log.debug("Init " + utilities.describe(this));
    }
  }

  protected multi_pass_analyzer(C source) {
    this(source, null, null);
  }

  protected abstract @Nullable error_signal do_multi_pass_analysis(analysis_pass pass);

  protected abstract analysis_result do_get_result();

  protected boolean has_processed(analysis_pass pass) {
    return pass.is_before(last_pass) || pass == last_pass;
  }

  public final @Nullable error_signal multi_pass_analysis(analysis_pass pass) {
    if (last_pass.is_before(pass)) {
      if (in_progress) {
        utilities.panic("Analysys in progress " + this + ": last " + last_pass +
            ", requested " + pass);
      }
      if (trace_analysis()) {
        log.debug("Enter " + this + " @ " + pass);
      }
      in_progress = true;
      int start = last_pass.ordinal() + 1;
      for (int pass_index = start; pass_index <= pass.ordinal(); ++pass_index) {
        analysis_pass current_pass = analysis_pass.values()[pass_index];

        @Nullable error_signal current_error = do_multi_pass_analysis(current_pass);
        assert last_pass != current_pass;
        last_pass = current_pass;

        if (current_error != null) {
          last_error = current_error;
          maybe_report_error(current_error);
        }
      }
      if (trace_analysis()) {
        log.debug("Exit " + this + " @ " + pass);
      }
      in_progress = false;
    }

    return last_error;
  }

  private boolean trace_analysis() {
    return false && (
             this instanceof type_declaration_analyzer ||
             this instanceof procedure_analyzer ||
             this instanceof variable_analyzer
           );
  }

  protected void analyze_and_ignore_errors(analyzable a, analysis_pass pass) {
    do_analyze(a, pass);
  }

  protected boolean has_errors(analyzable a, analysis_pass pass) {
    return do_analyze(a, pass) != null;
  }

  private @Nullable error_signal do_analyze(analyzable a, analysis_pass pass) {
    if (a instanceof base_analyzer) {
      init_context(a);
      if (a instanceof multi_pass_analyzer) {
        return ((multi_pass_analyzer) a).multi_pass_analysis(pass);
      } else if (pass.is_before(analysis_pass.BODY_CHECK)) {
        return null;
      }
    }

    return find_error(a);
  }

  @Override
  public final analysis_result analyze() {
    multi_pass_analysis(analysis_pass.last());
    if (last_error != null) {
      return last_error;
    }

    return do_get_result();
  }

  // TODO: move this to annotation so that declaration interface can be simplified.
  // Right now, procedure_declaration expects this.
  // (Also: name clashes with has_errors(analyzable a, analysis_pass pass) above.)
  public boolean has_errors() {
    return last_error != null;
  }
}
