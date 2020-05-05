-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.library.reflections;

--- An action encapsulates functionality of the machine--it corresponds to an interpreter
--- operation, or virtual machine instruction.
interface action {
  extends analysis_result, position, readonly data;

  abstract_value result;
  action bind_from(action from, position pos);
  declaration or null get_declaration;
  entity_wrapper execute(execution_context context);
}
