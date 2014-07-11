-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

-- Interoperability with Java(tm).

implicit import ideal.library.elements;
import ideal.library.channels.output;

namespace channels {

  class string_writer {
    implements output[character];

    -- TODO: use constructor.
    public string_writer();

    -- TODO: this should be a variable.
    nonnegative size();
    string elements() pure;
    string extract_elements();
  }

  namespace standard_channels {
    output[character] stdout;
  }
}
