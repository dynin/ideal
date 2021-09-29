-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class error_signal {
  extends debuggable;
  implements signal, mutable analyzable;

  notification cause;
  boolean is_cascading;

  overload error_signal(notification cause, boolean is_cascading) {
    this.cause = cause;
    this.is_cascading = is_cascading;
  }

  overload error_signal(string message, origin the_origin) {
    this(base_notification.new(message, the_origin), false);
  }

  --- Constructor for cascading errors.
  overload error_signal(string message, analyzable primary, origin the_origin) {
    analyzed : primary.analyze();
    assert analyzed is error_signal;
    this.cause = base_notification.new(message, the_origin, [ analyzed.cause, ]);
    this.is_cascading = true;
  }

  override origin deeper_origin => cause.origin;

  override boolean has_errors => true;

  override error_signal analyze => this;

  override readonly list[analyzable] children => empty[analyzable].new();

  void report_not_cascading() {
    if (!is_cascading) {
      cause.report();
    }
  }

  override analyzable specialize(specialization_context context,
      principal_type new_parent) => this;

  override string to_string => cause.to_string;
}
