-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Notifications used in the ideal development environment, including signals.
package notifications {
  implicit import ideal.library.elements;
  implicit import ideal.library.channels;
  implicit import ideal.library.texts;
  implicit import ideal.library.messages;
  implicit import ideal.library.reflections;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.texts;
  implicit import ideal.runtime.logs;
  implicit import ideal.development.elements;
  implicit import ideal.development.origins;
  implicit import ideal.development.types;
  import ideal.development.jumps.panic_value;

  enum notification_level;
  class base_notification;
  interface signal;
  class ok_signal;
  class error_signal;
  class error_action;
  namespace messages;
  class notification_output;
  namespace notification_context;
  namespace notification_utilities;
}
