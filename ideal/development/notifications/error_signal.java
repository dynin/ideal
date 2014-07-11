/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.notifications;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.types.*;
import ideal.development.values.panic_value;
import javax.annotation.Nullable;

public class error_signal extends debuggable implements analysis_result, analyzable,
    deeply_immutable_data, convertible_to_string {

  public final notification cause;
  public final boolean is_cascading;

  public error_signal(notification cause, boolean is_cascading) {
    this.cause = cause;
    this.is_cascading = is_cascading;
  }

  public error_signal(string message, position pos) {
    this(new base_notification(message, pos), false);
  }

  /** Constructor for cascading errors. */
  public error_signal(string message, analyzable primary, position pos) {
    assert primary.analyze() instanceof error_signal;
    error_signal primary_error = (error_signal) primary.analyze();
    assert primary_error != null;
    this.cause = new base_notification(message, pos,
        new base_list<notification>(primary_error.cause));
    this.is_cascading = true;
  }

  @Override
  public position source_position() {
    return cause.position();
  }

  @Override
  public error_signal analyze() {
    return this;
  }

  @Override
  public analyzable specialize(specialization_context context, principal_type new_parent) {
    return this;
  }

  @Override
  public string to_string() {
    return cause.to_string();
  }
}
