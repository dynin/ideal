-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Utility functions related to declarations.
namespace declaration_util {

  declaration or null get_declaration(origin or null the_origin) {
    var current_origin : the_origin;

    while (current_origin is_not null) {
      if (current_origin is declaration) {
        return current_origin;
      }

      if (current_origin is action) {
        declaration or null the_declaration : current_origin.get_declaration;
        if (the_declaration is_not null) {
          return the_declaration;
        }
      }

      current_origin = current_origin.deeper_origin;
    }

    return missing.instance;
  }

  --- Convert an |origin| into a master type declaration, or return |null| on failure.
  type_declaration or null to_type_declaration(origin or null the_origin) {
    if (the_origin is type_declaration) {
      return the_origin.master_declaration;
    } else if (the_origin is type_announcement) {
      return the_origin.get_type_declaration.master_declaration;
    } else {
      return missing.instance;
    }
  }

  --- Get a master type declaration associated with this type.
  type_declaration or null get_type_declaration(type the_type) {
    return to_type_declaration(the_type.principal.get_declaration);
  }

  -- TODO: use list.filter(), here and below.
  readonly list[type_declaration] get_declared_types(type_declaration the_type_declaration) {

    result : base_list[type_declaration].new();

    for (the_declaration : the_type_declaration.get_signature) {
      if (the_declaration is type_declaration) {
        result.append(the_declaration);
      }
    }

    return result;
  }

  readonly list[supertype_declaration] get_declared_supertypes(
      type_declaration the_type_declaration) {

    result : base_list[supertype_declaration].new();

    for (the_declaration : the_type_declaration.get_signature) {
      if (the_declaration is supertype_declaration) {
        result.append(the_declaration);
      }
    }

    return result;
  }

  readonly list[variable_declaration] get_declared_variables(
      type_declaration the_type_declaration) {

    result : base_list[variable_declaration].new();

    for (the_declaration : the_type_declaration.get_signature) {
      if (the_declaration is variable_declaration) {
        result.append(the_declaration);
      }
    }

    return result;
  }

  readonly list[procedure_declaration] get_declared_procedures(
      type_declaration the_type_declaration) {

    result : base_list[procedure_declaration].new();

    for (the_declaration : the_type_declaration.get_signature) {
      if (the_declaration is procedure_declaration) {
        result.append(the_declaration);
      }
    }

    return result;
  }
}
