-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Handle years/month/days.  Currently only Gregorian calendar is supported.
package calendars {
  implicit import ideal.library.elements;

  --- Gregorian year/month/day.
  interface gregorian_day {
    extends deeply_immutable data, equality_comparable, stringable;

    nonnegative year;
    gregorian_month month;
    nonnegative day;

    gregorian_day previous();
    gregorian_day next();

    -- TODO: add week info.
  }

  --- Gregorian month.
  interface gregorian_month {
    extends deeply_immutable data, equality_comparable, stringable;

    --- Index base 0 (January is 0, February is 1, and so on.)
    nonnegative ordinal;
    --- Index base 1 (January is 1, February is 2, and so on.)
    nonnegative index_base_1;
    --- Minimum number of days in the month (31 in January, 28 in February, etc.)
    nonnegative min_days;
    --- Maximum number of days in the month (31 in January, 29 in February, etc.)
    --- The only difference between |min_days| and |max_days| os for February.
    nonnegative max_days;
    string name;
  }
}
