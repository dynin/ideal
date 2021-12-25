-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class base_notification {
  implements notification;

  private string the_message;
  private origin the_origin;
  public the notification_level;
  private readonly list[notification] or null the_secondary;

  overload base_notification(string the_message, the origin,
      readonly list[notification] or null the_secondary, the notification_level) {
    verify the_message is_not null;
    verify the_origin is_not null;
    verify the_notification_level is_not null;
    this.the_message = the_message;
    this.the_origin = the_origin;
    this.the_secondary = (the_secondary is_not null && the_secondary.is_not_empty) ?
        the_secondary : missing.instance;
    this.the_notification_level = the_notification_level;
  }

  overload base_notification(string the_message, origin the_origin,
      readonly list[notification] or null the_secondary) {
    this(the_message, the_origin, the_secondary, notification_level.ERROR);
  }

  overload base_notification(string the_message, origin the_origin) {
    this(the_message, the_origin, missing.instance, notification_level.ERROR);
  }

  override string message => the_message;

  override origin origin => the_origin;

  override readonly list[notification] or null secondary => the_secondary;

  override log_level level => the_notification_level.the_log_level;

  override string to_string => the_message;

  override text_fragment to_text => render_text(true);

  override report => notification_context.get().write(this);

  override text_fragment render_text(boolean prefix_with_source) {
    var string full_message;
    if (prefix_with_source) {
      full_message = origin_utilities.get_source_prefix(the_origin) ++ the_message;
    } else {
      full_message = the_message;
    }

    -- TODO: do not hardcode style.
    MESSAGE_CLASS : "message";
    -- TODO: casts are redundant.
    primary : text_utilities.join(
        base_element.new(text_library.DIV, text_library.CLASS, MESSAGE_CLASS, full_message),
        origin_printer.show_origin(the_origin));

    if (the_secondary is_not null) {
      var text_fragment secondaries_text : text_utilities.EMPTY_FRAGMENT;
      for (secondary_element : the_secondary) {
        secondaries_text = text_utilities.join(secondaries_text,
            secondary_element.render_text(prefix_with_source));
      }
      return text_utilities.join(primary, base_element.new(text_library.INDENT, secondaries_text));
    } else {
      return primary;
    }
  }
}
