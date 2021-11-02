-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Definition of value objects and operations on values.
package values {
  implicit import ideal.library.elements;
  implicit import ideal.library.reflections;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.reflections;
--  implicit import ideal.runtime.logs;
  implicit import ideal.development.elements;
  implicit import ideal.development.names;
  implicit import ideal.development.flavors;
  implicit import ideal.development.declarations;
  implicit import ideal.development.kinds;
  implicit import ideal.development.types;
  implicit import ideal.development.jumps;

  interface data_value;
  class base_data_value;
  class base_value_action;
  class data_value_action;
  class singleton_value;
  class enum_value;
  interface procedure_value;
--  interface base_composite_value;
--  interface base_constant_value;
--  interface base_procedure;
--  interface base_string_value;
--  interface bound_procedure;
--  interface common_values;
--  interface composite_wrapper;
--  interface integer_value;
--  interface list_context;
--  interface list_value;
--  interface procedure_with_this;
--  interface returned_value;
--  interface string_value;
}
