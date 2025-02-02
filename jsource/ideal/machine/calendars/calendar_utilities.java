/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.machine.calendars;

import ideal.library.elements.*;
import ideal.library.calendars.*;
import ideal.runtime.calendars.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class calendar_utilities {
  public static gregorian_month month_of(Integer ordinal_base_0) {
    assert ordinal_base_0 >= 0 && ordinal_base_0 < 12;
    return base_gregorian_month.values()[ordinal_base_0];
  }

  public static gregorian_day day_of(Integer year, gregorian_month month, Integer day_of_month) {
    return new base_gregorian_day(new GregorianCalendar(year, month.ordinal(), day_of_month));
  }

  public static gregorian_day today() {
    return new base_gregorian_day(new GregorianCalendar());
  }

  public static Integer hour_now() {
    return new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
  }

  public static Integer minute_now() {
    return new GregorianCalendar().get(Calendar.MINUTE);
  }
}
