/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.extensions;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.modifiers.*;
import ideal.development.kinds.*;
import ideal.development.analyzers.*;

/**
 * Added to |string_text_node| to mark it as a supertype of string.
 * TODO: use arbitrary type.
 */
public class supertype_of_string_extension extends declaration_extension {

  public supertype_of_string_extension() {
    super("supertype_of_string");
  }

  @Override
  protected signal process_type_declaration(type_declaration_analyzer the_type_declaration,
      analysis_pass pass) {

    //type_declaration_analyzer string_declaration2 =
    //      (type_declaration_analyzer) library().string_type().get_declaration();
    //System.out.println("P " + pass + " SPASS " + string_declaration2.get_pass());

    if (pass == analysis_pass.TYPE_DECL) {
      set_expanded(the_type_declaration);
    }

    if (pass == analysis_pass.IMPORT_AND_TYPE_VAR_DECL) {
      origin the_origin = this;
      principal_type string_type = library().string_type();
      type_declaration_analyzer string_declaration =
          (type_declaration_analyzer) string_type.get_declaration();
    // System.out.println("PASS " + string_declaration.get_pass());
      supertype_analyzer the_supertype = new supertype_analyzer(
          new base_list<annotation_construct>(
              new modifier_construct(access_modifier.public_modifier, the_origin)), null,
          subtype_tags.subtypes_tag, the_type_declaration.get_declared_type(), the_origin);

      string_declaration.add_to_body(the_supertype);
    }

    return ok_signal.instance;
  }
}
