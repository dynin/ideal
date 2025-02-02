-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class error_signal {
  extends debuggable;
  implements deeply_immutable data;
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

  implement origin deeper_origin => cause.origin;

  implement boolean has_errors => true;

  implement error_signal analyze => this;

  implement readonly list[analyzable] children => empty[analyzable].new();

  implement action to_action => error_action.new(this);

  report_not_cascading() {
    if (!is_cascading) {
      cause.report();
    }
  }

  implement analyzable specialize(specialization_context context,
      principal_type new_parent) => this;

  implement string to_string => cause.to_string;
}
