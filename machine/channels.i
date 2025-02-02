-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

-- Interoperability with Java(tm).

implicit import ideal.library.elements;
import ideal.library.channels.output;

namespace channels {

  class string_writer {
    implements output[character];

    -- TODO: use constructor.
    public string_writer();

    var nonnegative size();
    var string elements();

    clear();
  }

  namespace standard_channels {
    output[character] stdout;
  }
}
