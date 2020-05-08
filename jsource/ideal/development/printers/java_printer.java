/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
import ideal.development.values.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;

import javax.annotation.Nullable;
public class java_printer extends base_printer {

  private static final base_string INSTANCEOF = new base_string("instanceof");

  public java_printer(printer_mode the_mode) {
    super(the_mode);
  }

  @Override
  public text_fragment print_infix(operator_construct c) {
    if (c.the_operator != operator.AS_OPERATOR) {
      return super.print_infix(c);
    } else {
      list<text_fragment> fragments = new base_list<text_fragment>();
      fragments.append(print_punctuation(punctuation.OPEN_PARENTHESIS));
      fragments.append(print(c.arguments.get(1)));
      fragments.append(print_punctuation(punctuation.CLOSE_PARENTHESIS));
      fragments.append(print_space());
      fragments.append(print(c.arguments.get(0)));
      return text_util.join(fragments);
    }
  }

  @Override
  public text_fragment print_operator_name(operator the_operator) {
    if (the_operator != operator.IS_OPERATOR) {
      return super.print_operator_name(the_operator);
    } else {
      return INSTANCEOF;
    }
  }

  @Override
  public text_fragment process_import(import_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    fragments.append(print_word(keyword.IMPORT));
    fragments.append(print_space());

    if (c.has_modifier(general_modifier.static_modifier)) {
      fragments.append(print_modifier_kind(general_modifier.static_modifier));
      fragments.append(print_space());
    }

    fragments.append(print(c.type));

    if (c.has_modifier(general_modifier.implicit_modifier)) {
      fragments.append(print_punctuation(punctuation.DOT));
      fragments.append(print_punctuation(punctuation.ASTERISK));
    }

    return text_util.join(fragments);
  }

  public boolean has_modifier(import_construct c, modifier_kind modifier) {
    // TODO: use list.has()...
    for (int i = 0; i < c.annotations.size(); ++i) {
      annotation_construct the_annotation = c.annotations.get(i);
      if (the_annotation instanceof modifier_construct) {
        if (((modifier_construct) the_annotation).the_kind == modifier) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  protected text_fragment print_type_parameters(list_construct parameters_list) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    readonly_list<construct> parameters = parameters_list.elements;

    for (int i = 0; i < parameters.size(); ++i) {
      construct c = parameters.get(i);
      if (c instanceof variable_construct) {
        fragments.append(print_type_parameter((variable_construct) c));
      } else {
        fragments.append(print(c));
      }

      if (i < parameters.size() - 1) {
        fragments.append(print_punctuation(punctuation.COMMA));
        fragments.append(print_space());
      }
    }
    return print_group(text_util.join(fragments), grouping_type.ANGLE_BRACKETS);
  }

  protected text_fragment print_type_parameter(variable_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(styles.wrap(styles.var_declaration_name_style, print_action_name(c.name)));

    if (c.type != null) {
      fragments.append(print_space());
      fragments.append(print_simple_name(subtype_tags.extends_tag.name()));
      fragments.append(print_space());
      fragments.append(print(c.type));
    }

    return text_util.join(fragments);
  }

  @Override
  protected text_fragment print_type_start(type_declaration_construct c) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    fragments.append(super.print_type_start(c));

    if (c.body != null) {
      for (int i = 0; i < c.body.size(); ++i) {
        construct maybe_super = c.body.get(i);
        if (maybe_super instanceof supertype_construct) {
          fragments.append(print_space());
          fragments.append(print(maybe_super));
        }
      }
    }

    return text_util.join(fragments);
  }

  @Override
  protected text_fragment print_type_body(readonly_list<construct> constructs) {
    // TODO: use filters.
    list<construct> enum_values = new base_list<construct>();
    list<construct> filtered_body = new base_list<construct>();
    for (int i = 0; i < constructs.size(); ++i) {
      construct the_construct = constructs.get(i);
      if (!(the_construct instanceof supertype_construct)) {
        if (enum_util.can_be_enum_value(the_construct)) {
          enum_values.append(the_construct);
        } else {
          filtered_body.append(the_construct);
        }
      }
    }
    @Nullable text_fragment body_statements;
    if (enum_values.is_empty() && filtered_body.is_empty()) {
      body_statements = null;
    } else {
      body_statements = print_statements(filtered_body);
      if (!enum_values.is_empty()) {
        body_statements = text_util.join(print_enum_values(enum_values), body_statements);
      }
    }
    return wrap_type_body(body_statements);
  }

  @Override
  protected boolean is_modifier_supported(modifier_kind the_kind) {
    return general_modifier.supported_by_java.contains(the_kind);
  }

  @Override
  public text_fragment process_parameter(parameter_construct c) {
    if (c.parameters.grouping != grouping_type.BRACES) {
      return super.process_parameter(c);
    } else {
      return text_util.join(print(c.main), print_block(c.parameters.elements, true, false));
    }
  }

  @Override
  protected text_fragment print_modifier_kind(modifier_kind the_kind) {
    if (general_modifier.java_annotations.contains(the_kind)) {
      return print_annotation_kind(the_kind);
    } else {
      return print_simple_name(the_kind.name());
    }
  }

  private text_fragment print_annotation_kind(modifier_kind the_kind) {
    String the_name = utilities.s(the_kind.name().to_string());
    assert !the_name.isEmpty();
    String processed_name;
    // TODO: have a set of non-uppercaseable annotations in general_modifiers
    if (the_kind != general_modifier.dont_display_modifier) {
      processed_name = "@" + Character.toUpperCase(the_name.charAt(0)) + the_name.substring(1);
    } else {
      processed_name = "@" + the_name;
    }
    return print_word(processed_name);
  }

  @Override
  public text_fragment process_flavor(flavor_construct c) {
    if (true) { // TODO: do flag as error.
      return super.process_flavor(c);
    }

    return process_error(c);
  }

  @Override
  public token_type init_token(variable_construct c) {
    return punctuation.EQUALS;
  }

  @Override
  public token_type enum_separator_token(boolean last_value) {
    return last_value ? punctuation.SEMICOLON : punctuation.COMMA;
  }
}
