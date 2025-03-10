-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class base_value_action[readonly entity_wrapper value_type] {
  implements action;
  extends debuggable;

  private origin the_origin;
  value_type the_value;

  base_value_action(value_type the_value, origin the_origin) {
    -- TODO: redundant assert
    assert the_origin is_not null;
    this.the_origin = the_origin;
    this.the_value = the_value;
  }

  implement final origin deeper_origin => the_origin;

  implement abstract_value result() {
    -- TODO: the variable is redundant
    readonly entity_wrapper v : the_value;
    return v.type_bound !> type;
  }

  implement action to_action => this;

  implement boolean has_side_effects => false;

  implement final action combine(action from, origin the_origin) {
    if (the_value is procedure_value) {
      return the_value.bind_this_action(from, the_origin);
    } else {
      return this;
    }
  }

  implement entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    if (from_entity is jump_wrapper) {
      return from_entity;
    }

    -- TODO: the cast is redundant
    return the_value !> entity_wrapper;
  }

  implement declaration or null get_declaration => missing.instance;

  implement string to_string => utilities.describe(this, the_value);
}
