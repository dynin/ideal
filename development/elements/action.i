-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.library.reflections;

--- An action encapsulates functionality of the machine--it corresponds to an interpreter
--- operation, or virtual machine instruction.
interface action {
  extends analysis_result, origin, readonly data;

  abstract_value result;
  declaration or null get_declaration;
  --- Check whether this action has logical side effects.
  boolean has_side_effects;
  action combine(action from, origin the_origin);
  entity_wrapper execute(entity_wrapper from_entity, execution_context context);
}
