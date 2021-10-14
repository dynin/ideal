/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class common_values {
  private static singleton_value VOID_INSTANCE;
  private static singleton_value MISSING_INSTANCE;
  private static singleton_value UNDEFINED_INSTANCE;

  private static enum_value FALSE_VALUE;
  private static enum_value TRUE_VALUE;

  public static boolean is_initialized() {
    return VOID_INSTANCE != null;
  }

  public static void initialize() {
    VOID_INSTANCE = new singleton_value(common_types.void_type());
    MISSING_INSTANCE = new singleton_value(common_types.missing_type());
    UNDEFINED_INSTANCE = new singleton_value(common_types.undefined_type());
  }

  public static enum_value true_value() {
    if (TRUE_VALUE == null) {
      TRUE_VALUE = get_boolean_value("true");
    }
    return TRUE_VALUE;
  }

  public static enum_value false_value() {
    if (FALSE_VALUE == null) {
      FALSE_VALUE = get_boolean_value("false");
    }
    return FALSE_VALUE;
  }

  public static enum_value to_boolean_value(boolean the_value) {
    return the_value ? true_value() : false_value();
  }

  public static singleton_value void_instance() {
    return VOID_INSTANCE;
  }

  public static singleton_value missing_instance() {
    return MISSING_INSTANCE;
  }

  public static singleton_value undefined_instance() {
    return UNDEFINED_INSTANCE;
  }

  public static action nothing(origin the_origin) {
    return VOID_INSTANCE.to_action(the_origin);
  }

  public static boolean is_nothing(action the_action) {
    return the_action instanceof base_value_action &&
           the_action.result().type_bound().principal() == common_types.void_type();
  }

  private static enum_value get_boolean_value(String sname) {
    simple_name the_name = simple_name.make(new base_string(sname));
    readonly_list<action> actions = ((action_context) common_types.get_context()).lookup(
        common_types.boolean_type(), the_name);
    assert actions.size() == 1;
    abstract_value the_value = actions.first().result();
    assert the_value instanceof enum_value;
    return (enum_value) the_value;
  }
}
