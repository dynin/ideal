-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace common_values {
  private var action_context context;

  private var singleton_value VOID_INSTANCE;
  private var singleton_value MISSING_INSTANCE;
  private var singleton_value UNDEFINED_INSTANCE;

  private var enum_value or null FALSE_VALUE;
  private var enum_value or null TRUE_VALUE;

  common_values(action_context context) {
    this.context = context;

    VOID_INSTANCE = singleton_value.new(common_types.void_type);
    MISSING_INSTANCE = singleton_value.new(common_types.missing_type);
    UNDEFINED_INSTANCE = singleton_value.new(common_types.undefined_type);
  }

  var enum_value true_value() {
    if (TRUE_VALUE is null) {
      TRUE_VALUE = get_boolean_value(simple_name.make("true"));
    }
    return TRUE_VALUE;
  }

  var enum_value false_value() {
    if (FALSE_VALUE is null) {
      FALSE_VALUE = get_boolean_value(simple_name.make("false"));
    }
    return FALSE_VALUE;
  }

  enum_value to_boolean_value(boolean the_value) {
    return the_value ? true_value : false_value;
  }

  var singleton_value void_instance => VOID_INSTANCE;

  var singleton_value missing_instance => MISSING_INSTANCE;

  var singleton_value undefined_instance => UNDEFINED_INSTANCE;

  action nothing(the origin) => VOID_INSTANCE.to_action(the_origin);

  boolean is_nothing(action the_action) {
    return the_action is base_value_action &&
           the_action.result.type_bound.principal == common_types.void_type;
  }

  private enum_value get_boolean_value(simple_name the_name) {
    actions : context.lookup(common_types.boolean_type, the_name);
    assert actions.size == 1;
    the_value : actions.first.result;
    assert the_value is enum_value;
    return the_value;
  }
}
