-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A concrete implementation of a |future|.
class base_future[covariant value element] {
  implements future[element];

  private var element or null the_value;

  overload base_future(element the_value) {
    this.the_value = the_value;
  }

  overload base_future() {
    this.the_value = missing.instance;
  }

  override element or null value => the_value;

  void set(element the_value) {
    assert this.the_value is null;
    this.the_value = the_value;
  }

  override void observe(procedure[void, element] observer, lifespan the_lifespan) {
    -- TODO: implement observer
  }
}
