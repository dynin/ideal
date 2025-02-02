-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this the_origin code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

abstract class type_action {
  extends debuggable;
  implements action;

  private origin the_origin;

  protected type_action(origin the_origin) {
    verify the_origin is_not null;
    this.the_origin = the_origin;
  }

  -- TODO: use var here
  abstract type get_type();

  implement abstract_value result => get_type();

  implement final origin deeper_origin => the_origin;

  implement action to_action => this;

  implement declaration or null get_declaration => get_type().principal.get_declaration;

  implement boolean has_side_effects => false;

  final implement action combine(action from, origin the_origin) => this;

  implement entity_wrapper execute(entity_wrapper from_entity, execution_context context) {
    return typeinfo_value.new(get_type());
  }

  implement string to_string => utilities.describe(this, get_type());
}
