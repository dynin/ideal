-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

test_suite test_calendars {
  implicit import ideal.machine.calendars.calendar_utilities;
  --implicit import ideal.runtime.calendars.base_gregorian_month;

  test_case test_months() {
    jan : month_of(0);
    assert jan.ordinal == 0;
    assert jan.index_base_1 == 1;
    assert jan.min_days == 31;
    assert jan.max_days == 31;
    assert jan.to_string == "JANUARY";
    assert jan == month_of(0);
    assert jan == JANUARY;

    feb : month_of(1);
    assert feb.ordinal == 1;
    assert feb.index_base_1 == 2;
    assert feb.min_days == 28;
    assert feb.max_days == 29;
    assert feb.to_string == "FEBRUARY";
    assert feb == month_of(1);
    assert feb == FEBRUARY;

    mar : month_of(2);
    assert mar.ordinal == 2;
    assert mar.index_base_1 == 3;
    assert mar.min_days == 31;
    assert mar.max_days == 31;
    assert mar.to_string == "MARCH";
    assert mar == month_of(2);
    assert mar == MARCH;

    nov : month_of(10);
    assert nov.ordinal == 10;
    assert nov.index_base_1 == 11;
    assert nov.min_days == 30;
    assert nov.max_days == 30;
    assert nov.to_string == "NOVEMBER";
    assert nov == month_of(10);
    assert nov == NOVEMBER;
  }

  test_case test_days() {
    aug11 : day_of(2021, AUGUST, 11);
    assert aug11.year == 2021;
    assert aug11.month == AUGUST;
    assert aug11.day == 11;
    assert aug11 == day_of(2021, AUGUST, 11);

    mar30 : day_of(1993, MARCH, 30);
    assert mar30.year == 1993;
    assert mar30.month == MARCH;
    assert mar30.day == 30;
    assert mar30 == day_of(1993, MARCH, 30);

    feb28 : day_of(2016, FEBRUARY, 28);
    assert feb28 == day_of(2016, FEBRUARY, 28);
    assert feb28.add_days(1) == day_of(2016, FEBRUARY, 29);
    assert feb28.add_days(2) == day_of(2016, MARCH, 1);

    jan1 : day_of(2000, JANUARY, 1);
    assert jan1 == day_of(2000, JANUARY, 1);
    assert jan1.add_days(-1) == day_of(1999, DECEMBER, 31);
    assert jan1.add_days(-365) == day_of(1999, JANUARY, 1);
    assert jan1.add_days(-365 * 2) == day_of(1998, JANUARY, 1);
  }
}
