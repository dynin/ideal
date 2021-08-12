-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

test_suite test_calendars {
  implicit import ideal.machine.calendars.calendar_utilities;

  test_case test_months() {
    jan : month_of(0);
    assert jan.ordinal == 0;
    assert jan.index_base_1 == 1;
    assert jan.min_days == 31;
    assert jan.max_days == 31;
    assert jan.name == "January";
    assert jan.to_string == "January";
    assert jan == month_of(0);

    feb : month_of(1);
    assert feb.ordinal == 1;
    assert feb.index_base_1 == 2;
    assert feb.min_days == 28;
    assert feb.max_days == 29;
    assert feb.name == "February";
    assert feb.to_string == "February";
    assert feb == month_of(1);

    mar : month_of(2);
    assert mar.ordinal == 2;
    assert mar.index_base_1 == 3;
    assert mar.min_days == 31;
    assert mar.max_days == 31;
    assert mar.name == "March";
    assert mar == month_of(2);

    nov : month_of(10);
    assert nov.ordinal == 10;
    assert nov.index_base_1 == 11;
    assert nov.min_days == 30;
    assert nov.max_days == 30;
    assert nov.name == "November";
    assert nov.to_string == "November";
  }

  test_case test_days() {
    aug : month_of(7);
    aug11 : day_of(2021, aug, 11);
    assert aug11.year == 2021;
    assert aug11.month == aug;
    assert aug11.day == 11;
    assert aug11 == day_of(2021, aug, 11);

    mar : month_of(2);
    mar30 : day_of(1993, mar, 30);
    assert mar30.year == 1993;
    assert mar30.month == mar;
    assert mar30.day == 30;
    assert mar30 == day_of(1993, mar, 30);

    feb : month_of(1);
    feb28 : day_of(2016, feb, 28);
    assert feb28 == day_of(2016, feb, 28);
    assert feb28.add_days(1) == day_of(2016, feb, 29);
    assert feb28.add_days(2) == day_of(2016, mar, 1);

    jan : month_of(0);
    jan1 : day_of(2000, jan, 1);
    assert jan1 == day_of(2000, jan, 1);
    dec : month_of(11);
    assert jan1.add_days(-1) == day_of(1999, dec, 31);
    assert jan1.add_days(-365) == day_of(1999, jan, 1);
    assert jan1.add_days(-365 * 2) == day_of(1998, jan, 1);
  }
}
