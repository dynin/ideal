/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

grammar metagrammar;

grammar_declaration:
                'grammar' name '{' grammar_statements_opt '}'
        ;

grammar_statements_opt:
                grammar_statement *
        ;

grammar_statement:
                terminal_statement
        |       nonterminal_statement
        |       production_statement
        ;

terminal_statement:
                'terminal' expression name_list ';'
        ;

nonterminal_statement:
                'nonterminal' expression name_list ';'
        ;

name_list:
                term_name name_list_tail *
        ;

name_list_tail:
                ',' term_name
        ;

production_statement:
                term_name '::=' rule_lists ';'
        ;

term_name:
                SIMPLE_NAME
        ;

the_rule:
                term_list_opt action_opt
        ;

action_opt:
                '{}'
        |       /* epsilon */
        ;

rule_lists:
                the_rule rule_lists_tail *
        ;

rule_lists_tail:
                '|' the_rule
        ;

term:
                term_name variable_name_opt
        |       term_name variable_name_opt '*'
        |       term_name variable_name_opt '+'
        ;

variable_name_opt:
                ':' term_name
        |       /* epsilon */
        ;

term_list_opt: term *
        ;

expression:        name
        |       'immutable' name '[' name ']'
        ;

name:        SIMPLE_NAME
        ;

SIMPLE_NAME: [a-zA-Z_]+ ;
WHITESPACE: [ \t\r\n]+ -> skip ;
LINE_COMMENT : '--' .*? '\r'? '\n' -> skip ;
