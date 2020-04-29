-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;

public interface type extends abstract_value, data, type_id, stringable,
    reference_equality {
  boolean is_subtype_of(type the_supertype);
  principal_type principal();
  type_flavor get_flavor();
  type get_flavored(type_flavor flavored);
}
