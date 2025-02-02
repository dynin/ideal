-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Constructs, also known as annotated syntax trees (ASTs).
package constructs {
  implicit import ideal.library.elements;
  implicit import ideal.runtime.elements;
  implicit import ideal.development.elements;
  implicit import ideal.development.names;

  interface annotation_construct;
  class base_construct;
  class block_construct;
  class comment_construct;
  class conditional_construct;
  enum constraint_category;
  class constraint_construct;
  class construct_visitor;
  class empty_construct;
  namespace enum_util;
  class extension_construct;
  class flavor_construct;
  class grouping_type;
  class import_construct;
  class jump_construct;
  class list_construct;
  class literal_construct;
  class loop_construct;
  class modifier_construct;
  class name_construct;
  class operator_construct;
  class parameter_construct;
  class procedure_construct;
  class resolve_construct;
  class return_construct;
  class supertype_construct;
  class type_announcement_construct;
  class type_declaration_construct;
  class variable_construct;
  class switch_construct;
  class case_clause_construct;
  class grammar_construct;
}
