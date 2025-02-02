-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class integer_literal {
  extends debuggable;
  implements literal[integer];

  private integer integer_value;
  private string image;
  integer radix;

  overload integer_literal(integer integer_value, string image, integer radix) {
    this.integer_value = integer_value;
    this.image = image;
    this.radix = radix;
  }

  overload integer_literal(integer integer_value) {
    this(integer_value, utilities.string_of(integer_value), radixes.DEFAULT_RADIX);
  }

  override integer the_value => integer_value;

  override string to_string => image;
}
