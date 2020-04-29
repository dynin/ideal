-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;

/**
 * An action encapsulates functionality of the machine--it corresponds to an interpreter
 * operation, or virtual machine instruction.
 */
public interface action extends analysis_result, position, readonly_data {
  abstract_value result();
  action bind_from(action from, position pos);
  @Nullable declaration get_declaration();
  entity_wrapper execute(execution_context context);
}
