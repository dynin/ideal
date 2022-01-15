-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Definition of value objects and operations on values.
package values {
  implicit import ideal.library.elements;
  implicit import ideal.library.reflections;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.reflections;
  implicit import ideal.development.elements;
  implicit import ideal.development.origins;
  implicit import ideal.development.names;
  implicit import ideal.development.flavors;
  implicit import ideal.development.declarations;
  implicit import ideal.development.kinds;
  implicit import ideal.development.types;
  implicit import ideal.development.jumps;
  implicit import ideal.development.notifications;
  implicit import ideal.development.flags;

  interface data_value;
  class base_data_value;
  class base_value_action;
  class data_value_action;
  class singleton_value;
  class enum_value;
  interface procedure_value;
  class base_constant_value;
  class integer_value;
  interface string_value;
  class base_string_value;
  interface composite_wrapper;
  class base_composite_value;
  class list_context;
  class list_value;
  class base_procedure;
  class bound_procedure;
  class procedure_with_this;
  class ok_signal;
  namespace common_values;
}
