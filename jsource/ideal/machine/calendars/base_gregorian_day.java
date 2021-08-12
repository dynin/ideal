/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.machine.calendars;

import ideal.library.elements.*;
import ideal.library.calendars.*;
import ideal.runtime.elements.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class base_gregorian_day implements gregorian_day {
  private final GregorianCalendar calendar;

  base_gregorian_day(GregorianCalendar calendar) {
    this.calendar = calendar;
  }

  @Override
  public Integer year() {
    return calendar.get(Calendar.YEAR);
  }

  @Override
  public gregorian_month month() {
    return calendar_utilities.month_of(calendar.get(Calendar.MONTH));
  }

  @Override
  public Integer day() {
    return calendar.get(Calendar.DAY_OF_MONTH);
  }

  @Override
  public gregorian_day add_days(Integer num_days) {
    GregorianCalendar new_calendar = (GregorianCalendar) calendar.clone();
    new_calendar.add(Calendar.DAY_OF_MONTH, num_days);
    return new base_gregorian_day(new_calendar);
  }

  @Override
  public string to_string() {
    return new base_string(year() + "/", month().to_string(), "/" + day());
  }
}
