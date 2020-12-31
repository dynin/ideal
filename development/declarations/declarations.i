-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Declarations (types, procedures, variables, etc.)
package declarations {
  implicit import ideal.library.elements;
  implicit import ideal.library.reflections;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.logs;
  implicit import ideal.development.elements;
  implicit import ideal.development.futures;
  implicit import ideal.development.names;
  implicit import ideal.development.comments;
  implicit import ideal.development.modifiers;

  enum declaration_pass;
  interface annotation_set;
  interface block_declaration;
  namespace declaration_util;
  enum procedure_category;
  interface procedure_declaration;
  interface supertype_declaration;
  interface type_announcement;
  interface type_declaration;
  interface type_parameter_declaration;
  enum variable_category;
  interface variable_declaration;
  interface import_declaration;
}
