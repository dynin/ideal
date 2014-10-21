; Copyright 2014 The Ideal Authors. All rights reserved.
;
; Use of this source code is governed by a BSD-style
; license that can be found in the LICENSE file or at
; https://developers.google.com/open-source/licenses/bsd

; Texts

(interface text)

(class text_string
  (implements text)
  (variable string value)
)

(class indented_text
  (implements text)
  (variable text inside)
)

(class text_list
  (implements text)
  (variable (list text) texts)
)

(interface describable
  (variable text description)
)

; Sources

(interface source
  ; The source of the source: we must go deeper...
  (variable (nullable source) the_source)
)

(datatype source_text
  (extends source describable)
  (variable string name)
  (variable (dont_describe) string content)
  (variable () (nullable source) the_source null)
)

(datatype text_position
  (extends source describable)
  (variable source_text the_source)
  (variable int character_index)
)

; Tokens

(enum token_type
  WHITESPACE
  COMMENT
  OPEN
  CLOSE
  IDENTIFIER
  LITERAL
  MODIFIER
)

(interface token
  (extends source)
  (variable token_type type)
)

(class simple_token
  (implements token describable)
  (variable (override) token_type type)
  (variable (override) source the_source)
)

; Constructs

(interface construct
  (extends source)
)

(class identifier
  (implements token construct describable)
  (variable string name)
  (variable (override) token_type type token_type.IDENTIFIER)
  (variable (override) source the_source)
)

(enum operator_type
  (variable string symbol)
  (DOT ".")
  (ASSIGN "=")
)

(class operator
  (implements construct describable)
  (variable operator_type the_operator_type)
  (variable (override) source the_source)
)

(class string_literal
  (implements token construct describable)
  (variable string value)
  (variable (nullable string) with_quotes)
  (variable (override) token_type type token_type.LITERAL)
  (variable (override) source the_source)
)

(enum grouping_type
  PARENS
  ANGLE_BRACKETS
  OPERATOR
)

(class parameter_construct
  (implements construct describable)
  (variable construct main)
  (variable (list construct) parameters)
  (variable (nullable grouping_type) grouping)
  (variable (override) source the_source)
)

(enum modifier_kind
  PUBLIC
  PRIVATE
  FINAL
  STATIC
  OVERRIDE
  DONT_DESCRIBE
  NULLABLE
)

(class modifier_construct
  (implements token construct describable)
  (variable modifier_kind the_modifier_kind)
  (variable (override) token_type type token_type.MODIFIER)
  (variable (override) source the_source)
)

(class s_expression
  (implements construct describable)
  (variable (list construct) parameters)
  (variable (override) source the_source)
)

(class block_construct
  (implements construct describable)
  (variable (list construct) statements)
  (variable (override) source the_source)
)

(class return_construct
  (implements construct describable)
  (variable (nullable construct) expression)
  (variable (override) source the_source)
)

(class variable_construct
  (implements construct describable)
  (variable (list modifier_construct) modifiers)
  (variable (nullable construct) type)
  (variable string name)
  (variable (nullable construct) initializer)
  (variable (override) source the_source)
)

(class procedure_construct
  (implements construct describable)
  (variable (list modifier_construct) modifiers)
  (variable (nullable construct) return_type)
  (variable string name)
  (variable (list variable_construct) parameters)
  (variable (nullable construct) body)
  (variable (override) source the_source)
)

(enum supertype_kind
  EXTENDS
  IMPLEMENTS
)

(class supertype_construct
  (implements construct describable)
  (variable supertype_kind the_supertype_kind)
  (variable (list construct) supertypes)
  (variable (override) source the_source)
)

(enum type_kind
  INTERFACE
  DATATYPE
  ENUM
  CLASS
)

(class type_construct
  (implements construct describable)
  (variable (list modifier_construct) modifiers)
  (variable type_kind the_type_kind)
  (variable string name)
  (variable (list construct) body)
  (variable (override) source the_source)
)

; Notifications

(enum notification_type
  (variable string message)
  (UNRECOGNIZED_CHARACTER "Unrecognized character")
  (EOF_IN_STRING_LITERAL "End of file in string literal")
  (NEWLINE_IN_STRING_LITERAL "Newline in string literal")
  (PARSE_ERROR "Parse error")
  (CLOSE_PAREN_NOT_FOUND "Close parenthesis not found")
  (MODIFIER_EXPECTED "Modifier expected")
)

(class notification
  (implements describable)
  (variable notification_type type)
  (variable source the_source)
)
