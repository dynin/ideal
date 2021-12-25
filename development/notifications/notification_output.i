-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Output channel adapter that detects whether an error notification has been written.
class notification_output {
  implements output[notification];

  private output[notification] the_output;
  private var boolean error_flag;

  notification_output(output[notification] the_output) {
    this.the_output = the_output;
    this.error_flag = false;
  }

  var boolean has_errors => error_flag;

  private set_error(the notification) {
    if (!error_flag) {
      if (the_notification is base_notification) {
        error_flag = the_notification.the_notification_level == notification_level.ERROR;
      }
    }
  }

  override write(notification value) {
    set_error(value);
    the_output.write(value);
  }

  override write_all(readonly list[notification] values) {
    for (the_notification : values) {
      set_error(the_notification);
    }
    the_output.write_all(values);
  }

  -- TODO: factor out so this can be reused.
  override sync() {
    the_output.sync();
  }

  -- TODO: factor out so this can be reused.
  override close() {
    the_output.close();
  }
}
