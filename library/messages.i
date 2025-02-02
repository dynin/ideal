-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Declarations for logging and log messages.
package messages {
  implicit import ideal.library.elements;
  implicit import ideal.library.texts;

  interface message {
    implements deeply_immutable data;
    implements stringable;

    text_fragment to_text();
  }

  -- See http://en.wikipedia.org/wiki/Syslog#Severity_levels
  enum log_level {
    EMERGENCY;
    ALERT;
    CRITICAL;
    ERROR;
    WARNING;
    NOTICE;
    INFORMATIONAL;
    DEBUG;
    TRACE;
  }

  interface log_message {
    extends message;

    log_level level;
    -- TODO: add timestamp.
  }
}
