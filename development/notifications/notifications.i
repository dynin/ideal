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
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.texts;
  implicit import ideal.runtime.logs;
  implicit import ideal.development.elements;
  implicit import ideal.development.origins;

  class base_notification;
  class error_signal;
  --interface messages;
  namespace notification_context;
  --interface notification_util;
  --interface ok_signal;
  interface signal;
}
