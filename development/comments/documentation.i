-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

interface documentation {
  extends readonly data;

  text_fragment or null section(documentation_section the_section);
}
