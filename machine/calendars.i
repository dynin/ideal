-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Gregorian calendar implementation.
namespace calendars {
  implicit import ideal.library.elements;
  implicit import ideal.library.calendars;

  namespace calendar_utilities {
    gregorian_month month_of(nonnegative ordinal_base_0);
    gregorian_day day_of(nonnegative year, gregorian_month month, nonnegative day_of_month);
    -- Today in local timezone.
    gregorian_day today();

    -- This is used by Briefing.  Having it here is a hack.
    nonnegative hour_now();
    nonnegative minute_now();
  }
}
