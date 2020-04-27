-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

package elements {

  implicit import ideal.library.elements;

  --- Position is a way to reference almost any piece of data in the ideal system.
  interface position {
    extends readonly data, reference_equality, stringable;

    position or null source_position;
  }
}
