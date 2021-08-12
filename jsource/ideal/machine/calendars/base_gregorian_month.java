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

//import javax.annotation.Nullable;

public class base_gregorian_month implements gregorian_month, deeply_immutable_reference_equality {
  private final int ordinal;
  private final string name;
  private final int min_days;
  private final int max_days;

  static gregorian_month[] ALL = new gregorian_month[] {
      new base_gregorian_month(0, new base_string("January"), 31, 31),
      new base_gregorian_month(1, new base_string("February"), 28, 29),
      new base_gregorian_month(2, new base_string("March"), 31, 31),
      new base_gregorian_month(3, new base_string("April"), 30, 30),
      new base_gregorian_month(4, new base_string("May"), 31, 31),
      new base_gregorian_month(5, new base_string("June"), 30, 30),
      new base_gregorian_month(6, new base_string("July"), 31, 31),
      new base_gregorian_month(7, new base_string("August"), 31, 31),
      new base_gregorian_month(8, new base_string("September"), 30, 30),
      new base_gregorian_month(9, new base_string("October"), 31, 31),
      new base_gregorian_month(10, new base_string("November"), 30, 30),
      new base_gregorian_month(11, new base_string("December"), 31, 31)
  };

  private base_gregorian_month(int ordinal, string name, int min_days, int max_days) {
    this.ordinal = ordinal;
    this.name = name;
    this.min_days = min_days;
    this.max_days = max_days;
  }

  @Override
  public Integer ordinal() {
    return ordinal;
  }

  @Override
  public Integer index_base_1() {
    return ordinal + 1;
  }

  @Override
  public Integer min_days() {
    return min_days;
  }

  @Override
  public Integer max_days() {
    return max_days;
  }

  @Override
  public string to_string() {
    return name;
  }

  @Override
  public string name() {
    return name;
  }
}
