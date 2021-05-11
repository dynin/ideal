/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
import ideal.development.constructs.constraint_pass;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.names.*;
import ideal.development.analyzers.*;
import ideal.development.extensions.*;

public class common_scanner extends base_scanner_config {

  public common_scanner() {
    add(new integer_token_element());

    add(new quoted_token_element(punctuation.SINGLE_QUOTE));
    add(new quoted_token_element(punctuation.DOUBLE_QUOTE));

    add(new line_comment(punctuation.MINUS_MINUS_MINUS, comment_type.LINE_DOC_COMMENT));
    add(new line_comment(punctuation.MINUS_MINUS, comment_type.LINE_COMMENT));
    add(new string_token_element<modifier_kind>(
        punctuation.ELLIPSIS, special_token_type.MODIFIER_KIND, general_modifier.varargs_modifier));

    add(new hash_element(punctuation.HASH, this));

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

    add_punctuation(punctuation.DOT_GREATER_THAN);
    add_punctuation(punctuation.EXCLAMATION_GREATER_THAN);

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

    add_keyword(keywords.OR);

    add_keyword(keywords.IS);
    add_keyword(keywords.IS_NOT);

    add_reserved(new base_string("as"), null);

    add_special(special_name.THIS, keywords.THIS);
    add_special(special_name.SUPER, keywords.SUPER);
    add_special(special_name.NEW, keywords.NEW);

    add_keyword(keywords.RETURN);

    add_keyword(keywords.IF);
    add_keyword(keywords.ELSE);

    add_keyword(keywords.LOOP);
    add_keyword(keywords.WHILE);
    add_keyword(keywords.FOR);

    add_keyword(keywords.IMPORT);

    add_keyword(keywords.USE);
    add_keyword(keywords.TARGET);

    add_keyword(keywords.PLEASE);

    add_jump(jump_type.BREAK_JUMP);
    add_jump(jump_type.CONTINUE_JUMP);

    add_constraint(constraint_pass.ASSERT_CONSTRAINT);
    add_constraint(constraint_pass.VERIFY_CONSTRAINT);

    add_kind(type_kinds.class_kind);
    add_kind(type_kinds.datatype_kind);
    add_kind(type_kinds.interface_kind);
    // add_kind(type_kinds.typedecl_kind);
    add_kind(type_kinds.singleton_kind);
    add_kind(type_kinds.package_kind);
    add_kind(type_kinds.module_kind);
    add_kind(type_kinds.concept_kind); // TODO: factor out bootstrap_scanner
    add_kind(type_kinds.enum_kind);
    add_kind(type_kinds.project_kind);
    add_kind(type_kinds.service_kind);
    add_kind(type_kinds.world_kind);
    add_kind(type_kinds.namespace_kind);
    add_kind(type_kinds.test_suite_kind);
    add_kind(type_kinds.reference_kind); // TODO: this should be a special keyword
    add_kind(type_kinds.procedure_kind); // TODO: this should be a special keyword
    add_kind(type_kinds.html_content_kind);

    add_subtype_tag(subtype_tags.subtypes_tag);
    add_subtype_tag(subtype_tags.extends_tag);
    add_subtype_tag(subtype_tags.implements_tag);
    add_subtype_tag(subtype_tags.refines_tag);
    add_subtype_tag(subtype_tags.aliases_tag);

    add_modifier(access_modifier.public_modifier);
    add_modifier(access_modifier.private_modifier);
    add_modifier(access_modifier.protected_modifier);

    add_modifier(general_modifier.static_modifier);
    add_modifier(general_modifier.abstract_modifier);
    add_modifier(general_modifier.final_modifier);
    add_modifier(general_modifier.implicit_modifier);
    add_modifier(general_modifier.explicit_modifier);

    add_modifier(general_modifier.var_modifier);
    add_modifier(general_modifier.pure_modifier);

    add_modifier(general_modifier.mutable_var_modifier);
    add_modifier(general_modifier.implement_modifier);
    add_modifier(general_modifier.override_modifier);
    add_modifier(general_modifier.overload_modifier);
    add_modifier(general_modifier.noreturn_modifier);
    add_modifier(general_modifier.dont_display_modifier);

    add_modifier(variance_modifier.invariant_modifier);
    add_modifier(variance_modifier.covariant_modifier);
    add_modifier(variance_modifier.contravariant_modifier);
    add_modifier(variance_modifier.combivariant_modifier);

    add_flavor(flavor.any_flavor);
    add_flavor(flavor.readonly_flavor);
    add_flavor(flavor.writeonly_flavor);
    add_flavor(flavor.mutable_flavor);
    add_flavor(flavor.immutable_flavor);
    add_flavor(flavor.deeply_immutable_flavor);
    add_flavor(flavor.raw_flavor);

    // TODO: process a list of extensions
    not_yet_implemented_extension.instance.register_syntax_extension(this);
    auto_constructor_extension.instance.register_syntax_extension(this);
    new cache_result_extension().register_syntax_extension(this);
    new supertype_of_string_extension().register_syntax_extension(this);
    meta_flags_extension.instance.register_syntax_extension(this);
    test_case_extension.instance.register_syntax_extension(this);
    meta_construct_extension.instance.register_syntax_extension(this);
  }
}
