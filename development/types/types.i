-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Definition of type objects and operations on types.
package types {
  implicit import ideal.library.elements;
  implicit import ideal.library.reflections;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.logs;
  implicit import ideal.development.elements;
  implicit import ideal.development.flavors;
  implicit import ideal.development.declarations;

--  class base_principal_type;
  class base_type;
--  interface base_value_printer;
  class concrete_type_action;
--  interface core_types;
--  interface flavored_type;
--  interface master_type;
--  interface parametrizable_state;
--  interface parametrized_type;
  class type_action;
  interface type_declaration_context;
  enum type_format;
--  interface type_parameters;
--  interface type_utilities;
  class typeinfo_value;
--  interface union_type;
--  interface value_printer;
}
