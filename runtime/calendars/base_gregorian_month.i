-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

enum base_gregorian_month {
  implements gregorian_month, reference_equality;

  JANUARY(31, 31);
  FEBRUARY(28, 29);
  MARCH(31, 31);
  APRIL(30, 30);
  MAY(31, 31);
  JUNE(30, 30);
  JULY(31, 31);
  AUGUST(31, 31);
  SEPTEMBER(30, 30);
  OCTOBER(31, 31);
  NOVEMBER(30, 30);
  DECEMBER(31, 31);

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
