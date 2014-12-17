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
  (extends describable)
  ; The source of the source: we must go deeper...
  (variable (nullable source) the_source)
)

(datatype source_text
  (extends source describable)
  (variable string name)
  (variable (dont_describe) string content)
  (variable (override) (nullable source) the_source null)
)

(datatype text_position
  (extends source describable)
  (variable source_text the_source)
  (variable integer character_index)
)

(singleton builtin_source
  (implements source describable)
  (variable (override) (nullable source) the_source null)
)

; Tokens

(interface token_type
  (extends describable)
)

(enum core_token_type
  (implements token_type describable)
  IDENTIFIER
  LITERAL
  MODIFIER
  WHITESPACE
  COMMENT
)

(enum punctuation
  (implements token_type describable)
  (variable string symbol)
  (OPEN_PARENTHESIS "(")
  (CLOSE_PARENTHESIS ")")
  (DOT ".")
)

(interface token
  (extends source)
  (variable token_type the_token_type)
)

(class simple_token
  (implements token describable)
  (variable (override) token_type the_token_type)
  (variable (override) source the_source)
)

; Constructs

(interface construct
  (extends source)
)

(class identifier
  (implements token construct describable)
  (variable string name)
  (variable (override) token_type the_token_type core_token_type.IDENTIFIER)
  (variable (override) source the_source)
)

(enum operator_type
  (variable string symbol)
  (DOT ".")
  (ASSIGN "=")
  (NEW "new")
)

(class operator
  (implements construct describable)
  (variable operator_type the_operator_type)
  (variable (override) source the_source)
)

(class string_literal
  (implements token construct value_action describable)
  (variable string value)
  (variable (nullable string) with_quotes)
  (variable (override) token_type the_token_type core_token_type.LITERAL)
  (variable (override) type result core_type.STRING)
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
  (variable (override) token_type the_token_type core_token_type.MODIFIER)
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
  SINGLETON
)

(class type_construct
  (implements construct describable)
  (variable (list modifier_construct) modifiers)
  (variable type_kind the_type_kind)
  (variable string name)
  (variable (list construct) body)
  (variable (override) source the_source)
)

; Actions and analysis

(interface type
  (variable string name)
)

(interface action
  (extends source)
  (variable type result)
)

(datatype type_action
  (extends action describable)
  (variable type result)
  (variable source the_source)
)

(datatype value_action
  (extends action describable)
  (variable type result)
  (variable source the_source)
)

(datatype notification_message
  (variable notification_type type)
  (variable string text)
)

(class error_signal
  (implements action describable)
  (variable notification_message message)
  (variable source the_source)
  (variable (override) type result core_type.ERROR)
)

(datatype principal_type
  (extends type describable)
  (variable string name)
  (variable (nullable principal_type) parent)
)

(singleton top_type
  (implements principal_type describable)
  (variable (override) string name "<top>")
  (variable (override) (nullable principal_type) parent null)
)

(enum core_type
  (implements principal_type describable)
  (variable (override) principal_type parent top_type.instance)
  VOID
  INTEGER
  STRING
  LIST
  NULL
  NULLABLE
  UNREACHABLE
  ERROR
)

(class parametrized_type
  (implements principal_type describable)
  (variable principal_type main)
  (variable (list type) parameters)
  ; TODO: use DOT operator
  (variable (override) string name (main.name))
  (variable (override) principal_type parent (main.parent))
)

(class type_declaration
  (implements type_action describable)
  (variable principal_type declared_type)
  (variable source the_source)
  (variable (override) type result declared_type)
)

(class variable_declaration
  (implements action describable)
  (variable type value_type)
  (variable string name)
  (variable principal_type declared_in_type)
  (variable source the_source)
  (variable (override) type result value_type)
)

; Notifications

(enum notification_type
  (implements notification_message)
  (variable string text)
  (variable (override) notification_type type this)
  (UNRECOGNIZED_CHARACTER "Unrecognized character")
  (EOF_IN_STRING_LITERAL "End of file in string literal")
  (NEWLINE_IN_STRING_LITERAL "Newline in string literal")
  (PARSE_ERROR "Parse error")
  (CLOSE_PAREN_NOT_FOUND "Close parenthesis not found")
  (MODIFIER_EXPECTED "Modifier expected")
  (ANALYSIS_ERROR "Analysis error")
  (SYMBOL_LOOKUP_FAILED "Symbol lookup failed")
  (TYPE_EXPECTED "Type expected")
  (NOT_PARAMETRIZABLE "Not parametrizable")
  (WRONG_ARITY "Wrong arity")
)
