-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.development.declarations;
implicit import ideal.development.types;

-- TODO: move to i.d.analyzers.mismatch_reporter.
namespace notification_utilities {
  readonly list[notification] to_notifications(readonly list[action] sources,
      value_printer printer) {

    notifications : base_list[notification].new();
    for (var nonnegative i : 0; i < sources.size; i += 1) {
      the_declaration : declaration_util.get_declaration(sources[i]);
      assert the_declaration is_not null;
      result_type : printer.print_value(sources[i].result);
      the_notification : base_notification.new("(declaration #" ++ i ++ ": " ++ result_type ++ ")",
          the_declaration);
      notifications.append(the_notification);
    }

    -- TODO: fix this by introducing a stable order
    return notifications;
  }

  error_signal cant_promote(abstract_value from, type target_type, origin the_origin) {
    printer : base_value_printer.instance;
    return error_signal.new("Can't promote " ++ printer.print_value(from) ++
        " to " ++ printer.print_value(target_type), the_origin);
  }
}
