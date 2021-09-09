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
  implicit import ideal.development.names;
  implicit import ideal.development.flavors;
  implicit import ideal.development.declarations;
  implicit import ideal.development.kinds;

  class base_principal_type;
  class base_type;
  class base_value_printer;
  class concrete_type_action;
  namespace core_types;
  class flavored_type;
  class master_type;
  class parametrizable_state;
  class parametrized_type;
  class type_action;
  interface type_declaration_context;
  enum type_format;
  class type_parameters;
  namespace type_utilities;
  class typeinfo_value;
  class union_type;
  class null_wrapper;
  interface value_printer;
}
