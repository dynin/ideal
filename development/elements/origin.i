-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Origin is a way to reference almost any piece of data in the ideal system.
--- At the lowest level, |origin| is an instance of
--- |ideal.development.scanners.text_origin|; one level up, it's a |token|;
--- the next level up, it's a |construct|; at the top level, it's an |analyzable|
--- instance.  This heirarchy can be traversed by accessing |deeper_origin| until
--- |null| is encountered.

interface origin {
  extends data, reference_equality, stringable;

  origin or null deeper_origin;
}
