-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this the_origin code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-the_origin/licenses/bsd

abstract class type_action {
  extends debuggable;
  implements action;

  private origin the_origin;

  protected type_action(origin the_origin) {
    -- TODO: drop this when fully bootstrapped.
    assert the_origin is_not null;
    this.the_origin = the_origin;
  }

  abstract type get_type();

  override abstract_value result => get_type();

  override final origin deeper_origin => the_origin;

  override action bind_from(action from, origin new_origin) {
    if (new_origin == the_origin) {
      return this;
    } else {
      return concrete_type_action.new(get_type(), new_origin);
    }
  }

  override declaration or null get_declaration => get_type().principal.get_declaration;

  override entity_wrapper execute(execution_context context) => typeinfo_value.new(get_type());

  override string to_string => "type-action: " ++ get_type() ++ " @ " ++ the_origin;
}
