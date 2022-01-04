/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
 */
public class supertype_of_extension extends declaration_extension {

  public supertype_of_extension() {
    super("supertype_of");
  }

  @Override
  public boolean supports_parameters() {
    return true;
  }

  @Override
  protected signal process_type_declaration(type_declaration_analyzer the_type_declaration,
      analysis_pass pass) {

    if (pass == analysis_pass.IMPORT_AND_TYPE_VAR_DECL) {
      if (analyzable_parameters.size() != 1) {
        return new error_signal(new base_string("Exactly one parameter supported"), this);
      }
      analyzable type_analyzable = analyzable_parameters.first();
      add_dependence(type_analyzable, the_type_declaration.declared_in_type(),
          declaration_pass.FLAVOR_PROFILE);

      if (has_analysis_errors(type_analyzable)) {
        return new error_signal(messages.error_in_parametrizable, type_analyzable, this);
      }

      action the_type_action = action_not_error(type_analyzable);
      if (!(the_type_action instanceof type_action)) {
        return new error_signal(messages.type_expected, this);
      }

      principal_type the_type = ((type_action) the_type_action).get_type().principal();
      type_declaration_analyzer the_declaration =
          (type_declaration_analyzer) the_type.get_declaration();

      origin the_origin = this;
      supertype_analyzer the_supertype = new supertype_analyzer(
          new base_list<annotation_construct>(
              new modifier_construct(access_modifier.public_modifier, the_origin)), null,
          subtype_tags.subtypes_tag, the_type_declaration.get_declared_type(), the_origin);

      the_declaration.append_to_body(the_supertype);
    }

    return ok_signal.instance;
  }
}
