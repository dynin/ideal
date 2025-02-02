-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

abstract class base_construct {
  extends debuggable;
  implements construct;

  private dont_display origin the_origin;
  dont_display var analyzable or null the_analyzable;

  --public abstract readonly_list<construct> children();

  base_construct(origin the_origin) {
    assert the_origin is_not null;
    this.the_origin = the_origin;
  }

  override origin deeper_origin() => the_origin;
}
