-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

enum base_gregorian_month {
  implements gregorian_month;

  JANUARY: new(31, 31);
  FEBRUARY: new(28, 29);
  MARCH: new(31, 31);
  APRIL: new(30, 30);
  MAY: new(31, 31);
  JUNE: new(30, 30);
  JULY: new(31, 31);
  AUGUST: new(31, 31);
  SEPTEMBER: new(30, 30);
  OCTOBER: new(31, 31);
  NOVEMBER: new(30, 30);
  DECEMBER: new(31, 31);

  nonnegative the_min_days;
  nonnegative the_max_days;

  private base_gregorian_month(nonnegative min_days, nonnegative max_days) {
    this.the_min_days = min_days;
    this.the_max_days = max_days;
  }

  override nonnegative index_base_1 => ordinal + 1;

  -- TODO: methods are redundant
  override nonnegative min_days => the_min_days;

  override nonnegative max_days => the_max_days;
}
