/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.constructs.*;
import ideal.development.literals.*;
import ideal.development.values.*;
import ideal.development.modifiers.*;
import ideal.development.languages.javascript_language;

public class javascript_printer extends base_printer {

  // TODO: define token
  private static final string FUNCTION = new base_string("function");
  // TODO: make a parameter
  private static final boolean FUNCTIONS_IN_OBJECT = true;

  public javascript_printer(printer_mode the_mode) {
    super(the_mode);
  }

  @Override
  public text_fragment process_procedure(procedure_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    if (FUNCTIONS_IN_OBJECT) {
      fragments.append(print_quoted_literal(
          new quoted_literal(c.name.to_string(), punctuation.SINGLE_QUOTE)));
      fragments.append(print_space());
      fragments.append(print_word(punctuation.COLON));
      fragments.append(print_space());
      fragments.append(print_word(FUNCTION));
    } else {
      fragments.append(print_documentation(c.annotations, c));

      fragments.append(print_word(FUNCTION));
      fragments.append(print_space());
      fragments.append(styles.wrap(styles.procedure_declaration_name_style,
          print_action_name(c.name)));
    }

    fragments.append(print_params(c.parameters, grouping_type.PARENS));

    if (FUNCTIONS_IN_OBJECT) {
      fragments.append(print_block(((block_construct) c.body).body, true, false));
      fragments.append(print_word(punctuation.COMMA));
    } else {
      fragments.append(print_procedure_body(c.body));
    }

    return print_line(text_utilities.join(fragments));
  }

  @Override
  protected boolean print_variable_types() {
    return false;
  }

  @Override
  public token_type init_token(variable_construct c) {
    return punctuation.EQUALS;
  }

  @Override
  protected boolean is_modifier_supported(modifier_kind the_kind) {
    return javascript_language.supported_modifiers.contains(the_kind);
  }
}
