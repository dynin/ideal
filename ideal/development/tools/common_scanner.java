/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tools;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.scanners.*;
import ideal.development.comments.*;
import ideal.development.constructs.jump_type;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.names.*;

public class common_scanner extends base_scanner_config {

  public common_scanner() {
    add(new integer_token_element());

    add(new quoted_token_element(punctuation.SINGLE_QUOTE));
    add(new quoted_token_element(punctuation.DOUBLE_QUOTE));

    add(new line_comment(punctuation.MINUS_MINUS_MINUS, comment_type.LINE_DOC_COMMENT));
    add(new line_comment(punctuation.MINUS_MINUS, comment_type.LINE_COMMENT));
    add(new string_token_element<modifier_kind>(
        punctuation.ELLIPSIS, special_token_type.MODIFIER_KIND, general_modifier.varargs_modifier));

    // TODO: special handling.
    add_punctuation(punctuation.HASH);

    add_punctuation(punctuation.DOT);
    add_punctuation(punctuation.OPEN_PARENTHESIS);
    add_punctuation(punctuation.CLOSE_PARENTHESIS);
    add_punctuation(punctuation.OPEN_BRACKET);
    add_punctuation(punctuation.CLOSE_BRACKET);
    add_punctuation(punctuation.OPEN_BRACE);
    add_punctuation(punctuation.CLOSE_BRACE);
    add_punctuation(punctuation.COMMA);
    add_punctuation(punctuation.COLON);
    add_punctuation(punctuation.SEMICOLON);
    add_punctuation(punctuation.EQUALS_GREATER_THAN);

    add_punctuation(punctuation.QUESTION_MARK);

    add_punctuation(punctuation.EXCLAMATION_MARK);

    add_punctuation(punctuation.ASTERISK);
    add_punctuation(punctuation.SLASH);
    add_punctuation(punctuation.PERCENT);

    add_punctuation(punctuation.PLUS);
    add_punctuation(punctuation.PLUS_PLUS);
    add_punctuation(punctuation.MINUS);

    add_punctuation(punctuation.EQUALS_EQUALS);
    add_punctuation(punctuation.EXCLAMATION_MARK_EQUALS);
    add_punctuation(punctuation.LESS_THAN);
    add_punctuation(punctuation.GREATER_THAN);
    add_punctuation(punctuation.LESS_THAN_EQUALS);
    add_punctuation(punctuation.GREATER_THAN_EQUALS);

    add_punctuation(punctuation.AMPERSAND);
    add_punctuation(punctuation.CARET);
    add_punctuation(punctuation.VERTICAL_BAR);
    add_punctuation(punctuation.AMPERSAND_AMPERSAND);
    add_punctuation(punctuation.VERTICAL_BAR_VERTICAL_BAR);

    add_punctuation(punctuation.EQUALS);
    add_punctuation(punctuation.PLUS_EQUALS);
    add_punctuation(punctuation.MINUS_EQUALS);
    add_punctuation(punctuation.ASTERISK_EQUALS);
    add_punctuation(punctuation.PLUS_PLUS_EQUALS);

    add_keyword(keyword.OR);

    add_keyword(keyword.IS);
    add_keyword(keyword.IS_NOT);
    add_keyword(keyword.AS);

    add_special(special_name.THIS, keyword.THIS);
    add_special(special_name.SUPER, keyword.SUPER);
    add_special(special_name.NEW, keyword.NEW);

    add_keyword(keyword.RETURN);
    add_keyword(keyword.ASSERT);

    add_keyword(keyword.IF);
    add_keyword(keyword.ELSE);

    add_keyword(keyword.LOOP);
    add_keyword(keyword.WHILE);
    add_keyword(keyword.FOR);

    add_keyword(keyword.IMPORT);

    add_keyword(keyword.USE);
    add_keyword(keyword.PLEASE);

    add_jump(jump_type.BREAK_JUMP);
    add_jump(jump_type.CONTINUE_JUMP);

    add_kind(type_kinds.class_kind);
    add_kind(type_kinds.datatype_kind);
    add_kind(type_kinds.interface_kind);
    // add_kind(type_kinds.typedecl_kind);
    add_kind(type_kinds.singleton_kind);
    add_kind(type_kinds.package_kind);
    add_kind(type_kinds.module_kind);
    add_kind(type_kinds.concept_kind); // TODO: factor out boostrap_scanner
    add_kind(type_kinds.enum_kind);
    add_kind(type_kinds.project_kind);
    add_kind(type_kinds.service_kind);
    add_kind(type_kinds.world_kind);
    add_kind(type_kinds.namespace_kind);
    add_kind(type_kinds.reference_kind); // TODO: this should be a special keyword
    add_kind(type_kinds.procedure_kind); // TODO: this should be a special keyword

    add_supertype_kind(supertype_kinds.extends_kind);
    add_supertype_kind(supertype_kinds.implements_kind);
    add_supertype_kind(supertype_kinds.refines_kind);
    add_supertype_kind(supertype_kinds.aliases_kind);

    add_modifier(access_modifier.public_modifier);
    add_modifier(access_modifier.private_modifier);
    add_modifier(access_modifier.protected_modifier);

    add_modifier(general_modifier.static_modifier);
    add_modifier(general_modifier.abstract_modifier);
    add_modifier(general_modifier.final_modifier);
    add_modifier(general_modifier.implicit_modifier);

    add_modifier(general_modifier.var_modifier);
    add_modifier(general_modifier.pure_modifier);

    // TODO: should this be in the common syntax?
    add_modifier(general_modifier.not_yet_implemented_modifier);

    add_modifier(general_modifier.implement_modifier);
    add_modifier(general_modifier.override_modifier);
    add_modifier(general_modifier.overload_modifier);
    add_modifier(general_modifier.noreturn_modifier);
    add_modifier(general_modifier.testcase_modifier);

    add_modifier(variance_modifier.invariant_modifier);
    add_modifier(variance_modifier.covariant_modifier);
    add_modifier(variance_modifier.contravariant_modifier);
    add_modifier(variance_modifier.combivariant_modifier);

    add_flavor(flavors.any_flavor);
    add_flavor(flavors.readonly_flavor);
    add_flavor(flavors.writeonly_flavor);
    add_flavor(flavors.mutable_flavor);
    add_flavor(flavors.immutable_flavor);
    add_flavor(flavors.deeply_immutable_flavor);
    add_flavor(flavors.raw_flavor);
  }
}
