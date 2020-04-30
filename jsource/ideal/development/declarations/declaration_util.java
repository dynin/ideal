/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.declarations;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.logs.*;
import ideal.development.elements.*;

public class declaration_util {

  public static @Nullable declaration get_declaration(@Nullable position source) {
    @Nullable position pos = source;
    while (pos != null) {
      if (pos instanceof declaration) {
        return (declaration) pos;
      }
      if (pos instanceof action) {
        @Nullable declaration decl = ((action) pos).get_declaration();
        if (decl != null) {
          return decl;
        }
      }
      pos = pos.source_position();
    }
    return null;
  }

  public static @Nullable type_declaration get_type_declaration(type the_type) {
    @Nullable position the_declaration = the_type.principal().get_declaration();
    if (the_declaration instanceof type_declaration) {
      return (type_declaration) the_declaration;
    } else {
      return null;
    }
  }

  // TODO: use list.filter()
  public static readonly_list<type_declaration> get_declared_types(
      type_declaration the_type_declaration) {

    readonly_list<declaration> signature = the_type_declaration.get_signature();
    list<type_declaration> result = new base_list<type_declaration>();

    for (int i = 0; i < signature.size(); ++i) {
      declaration the_declaration = signature.get(i);
      if (the_declaration instanceof type_declaration) {
        result.append((type_declaration) the_declaration);
      }
    }

    return result;
  }

  public static readonly_list<supertype_declaration> get_declared_supertypes(
      type_declaration the_type_declaration) {

    readonly_list<declaration> signature = the_type_declaration.get_signature();
    list<supertype_declaration> result = new base_list<supertype_declaration>();

    for (int i = 0; i < signature.size(); ++i) {
      declaration the_declaration = signature.get(i);
      if (the_declaration instanceof supertype_declaration) {
        result.append((supertype_declaration) the_declaration);
      }
    }

    return result;
  }

  public static readonly_list<variable_declaration> get_declared_variables(
      type_declaration the_type_declaration) {

    readonly_list<declaration> signature = the_type_declaration.get_signature();
    list<variable_declaration> result = new base_list<variable_declaration>();

    for (int i = 0; i < signature.size(); ++i) {
      declaration the_declaration = signature.get(i);
      if (the_declaration instanceof variable_declaration) {
        result.append((variable_declaration) the_declaration);
      }
    }

    return result;
  }

  public static readonly_list<procedure_declaration> get_declared_procedures(
      type_declaration the_type_declaration) {

    readonly_list<declaration> signature = the_type_declaration.get_signature();
    list<procedure_declaration> result = new base_list<procedure_declaration>();

    for (int i = 0; i < signature.size(); ++i) {
      declaration the_declaration = signature.get(i);
      if (the_declaration instanceof procedure_declaration) {
        result.append((procedure_declaration) the_declaration);
      }
    }

    return result;
  }

  private declaration_util() { }
}
