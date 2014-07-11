/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;

/**
 * Analyze a sequence (list) of actions.  Unlike |block_analyzer|, no frame is created.
 */
public class declaration_list_analyzer extends multi_pass_analyzer {
  private readonly_list<analyzable> the_elements;

  public declaration_list_analyzer(readonly_list<analyzable> the_elements, position pos) {
    super(pos);
    this.the_elements = the_elements;
  }

  public declaration_list_analyzer(readonly_list<construct> constructs, principal_type parent,
      analysis_context context, position pos) {
    super(pos, parent, context);
    assert constructs != null;
    the_elements = make_list(constructs);
  }

  @Override
  protected @Nullable error_signal do_multi_pass_analysis(analysis_pass pass) {
    error_signal error = null;

    for (int i = 0; i < the_elements.size(); ++i) {
      if (has_errors(the_elements.get(i), pass) && error == null) {
        error = new error_signal(messages.error_in_list, the_elements.get(i), this);
      }
    }

    return error;
  }

  @Override
  protected action do_get_result() {
    list<action> actions = new base_list<action>();
    for (int i = 0; i < the_elements.size(); ++i) {
      analyzable element = the_elements.get(i);
      if (!has_errors(element)) {
        actions.append(action_not_error(element));
      }
    }
    return new list_action(actions, this);
  }
}
