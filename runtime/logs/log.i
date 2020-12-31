-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A log service and helper methods.
namespace log {
  implicit import ideal.library.channels;
  implicit import ideal.runtime.channels;

  import ideal.machine.adapters.java.lang.String;
  import ideal.machine.channels.standard_channels;

  private text_fragment to_text(log_message the_message) pure {
    return the_message.to_text();
  }

  final output[log_message] log_output :
      auto_sync_output[log_message].new(
          output_transformer[log_message, text_fragment].new(to_text,
              plain_formatter.new(standard_channels.stdout)));

  -- TODO: do not use Java String.
  void debug(String the_string) {
    log_output.write(simple_message.new(log_level.DEBUG, base_string.new(the_string)));
  }

  void info(string the_string) {
    log_output.write(simple_message.new(log_level.INFORMATIONAL, the_string));
  }

  -- TODO: do not use Java String.
  void error(String the_string) {
    log_output.write(simple_message.new(log_level.ERROR, base_string.new(the_string)));
  }
}
