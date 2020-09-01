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

  protected abstract signal do_multi_pass_analysis(analysis_pass pass);

  protected abstract analysis_result do_get_result();

  protected boolean has_processed(analysis_pass pass) {
    return pass.is_before(last_pass) || pass == last_pass;
  }

  public final signal multi_pass_analysis(analysis_pass pass) {
    if (last_pass.is_before(pass)) {
      if (in_progress) {
        utilities.panic("Analysis in progress " + this + ": last " + last_pass +
            ", requested " + pass);
      }
      if (trace_analysis()) {
        log.debug("Enter " + this + " @ " + pass);
      }
      in_progress = true;
      int start = last_pass.ordinal() + 1;
      for (int pass_index = start; pass_index <= pass.ordinal(); ++pass_index) {
        analysis_pass current_pass = analysis_pass.values()[pass_index];

        signal current_signal = do_multi_pass_analysis(current_pass);
        assert current_signal != null;

        assert last_pass != current_pass;
        last_pass = current_pass;

        if (current_signal instanceof error_signal) {
          last_error = (error_signal) current_signal;
          maybe_report_error(last_error);
        }
      }
      if (trace_analysis()) {
        log.debug("Exit " + this + " @ " + pass);
      }
      in_progress = false;
    }

    if (last_error != null) {
      return last_error;
    } else {
      return ok_signal.instance;
    }
  }

  private boolean trace_analysis() {
    return false && (
             this instanceof type_declaration_analyzer ||
             this instanceof procedure_analyzer ||
             this instanceof variable_analyzer
           );
  }

  protected void analyze_and_ignore_errors(analyzable a, analysis_pass pass) {
    analyze(a, pass);
  }

  protected boolean has_errors(analyzable a, analysis_pass pass) {
    return analyze(a, pass) instanceof error_signal;
  }

  protected @Nullable error_signal find_error(analyzable a, analysis_pass pass) {
    signal result = analyze(a, pass);
    if (result instanceof error_signal) {
      return (error_signal) result;
    } else {
      return null;
    }
  }

  public signal analyze(analyzable the_analyzable, analysis_pass pass) {
    if (the_analyzable instanceof base_analyzer) {
      init_context(the_analyzable);
      if (the_analyzable instanceof multi_pass_analyzer) {
        return ((multi_pass_analyzer) the_analyzable).multi_pass_analysis(pass);
      } else if (pass.is_before(analysis_pass.BODY_CHECK)) {
        return ok_signal.instance;
      }
    }

    analysis_result the_analysis_result = the_analyzable.analyze();
    if (the_analysis_result instanceof error_signal) {
      return (error_signal) the_analysis_result;
    } else {
      return ok_signal.instance;
    }
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
