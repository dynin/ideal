; Copyright 2014-2025 The Ideal Authors. All rights reserved.
;
; Use of this source code is governed by a BSD-style
; license that can be found in the LICENSE file or at
; https://theideal.org/license/

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

(interface origin
  (extends describable)
  ; The origin of the origin: we must go deeper...
  (variable (nullable origin) the_origin)
)

(datatype source_text
  (extends origin)
  (variable string name)
  (variable (dont_describe) string content)
  (variable (override) (nullable origin) the_origin null)
)

(datatype text_position
  (extends origin)
  (variable source_text the_origin)
  (variable integer character_index)
)

(singleton builtin_origin
  (implements origin)
  (variable (override) (nullable origin) the_origin null)
)

; Tokens

(interface token_type
  (extends describable)
)

(enum core_token_type
  (implements token_type)
  IDENTIFIER
  LITERAL
  MODIFIER
  OPERATOR
  WHITESPACE
  COMMENT
)

(enum punctuation
  (implements token_type)
  (variable string symbol)
  (OPEN_PARENTHESIS "(")
  (CLOSE_PARENTHESIS ")")
  (DOT ".")
)

(interface token
  (extends origin)
  (variable token_type the_token_type)
)

(class simple_token
  (implements token)
  (variable (override) token_type the_token_type)
  (variable (override) origin the_origin)
)

; Constructs

(interface construct
  (extends origin)
)

(class identifier
  (implements token construct)
  (variable string name)
  (variable (override) token_type the_token_type (. core_token_type IDENTIFIER))
  (variable (override) origin the_origin)
)

(enum operator_type
  (variable string symbol)
  (DOT ".")
  (ASSIGN "=")
  (NEW "new")
  (IS "is")
  (AS "as")
)

(class operator
  (implements token construct)
  (variable operator_type the_operator_type)
  (variable (override) token_type the_token_type (. core_token_type OPERATOR))
  (variable (override) origin the_origin)
)

(class string_literal
  (implements token construct value_action)
  (variable string value)
  (variable (nullable string) with_quotes)
  (variable (override) token_type the_token_type (. core_token_type LITERAL))
  (variable (override) type result (. core_type STRING))
  (variable (override) origin the_origin)
)

(enum grouping_type
  PARENS
  ANGLE_BRACKETS
  OPERATOR
)

(class parameter_construct
  (implements construct)
  (variable construct main)
  (variable (list construct) parameters)
  (variable (nullable grouping_type) grouping)
  (variable (override) origin the_origin)
)

(enum modifier_kind
  PUBLIC
  PRIVATE
  FINAL
  STATIC
  ABSTRACT
  OVERRIDE
  DONT_DESCRIBE
  NULLABLE
)

(class modifier_construct
  (implements token construct)
  (variable modifier_kind the_modifier_kind)
  (variable (override) token_type the_token_type (. core_token_type MODIFIER))
  (variable (override) origin the_origin)
)

(class s_expression
  (implements construct)
  (variable (list construct) parameters)
  (variable (override) origin the_origin)
)

(class block_construct
  (implements construct)
  (variable (list construct) statements)
  (variable (override) origin the_origin)
)

(class conditional_construct
  (implements construct)
  (variable construct conditional)
  (variable construct then_branch)
  (variable (nullable construct) else_branch)
  (variable (override) origin the_origin)
)

(class return_construct
  (implements construct)
  (variable (nullable construct) expression)
  (variable (override) origin the_origin)
)

(class variable_construct
  (implements construct)
  (variable (list modifier_construct) modifiers)
  (variable (nullable construct) type)
  (variable string name)
  (variable (nullable construct) initializer)
  (variable (override) origin the_origin)
)

(class procedure_construct
  (implements construct)
  (variable (list modifier_construct) modifiers)
  (variable (nullable construct) return_type)
  (variable string name)
  (variable (list variable_construct) parameters)
  (variable (nullable construct) body)
  (variable (override) origin the_origin)
)

(class dispatch_construct
  (implements construct)
  (variable string name)
  (variable construct the_type)
  (variable (override) origin the_origin)
)

(enum supertype_kind
  EXTENDS
  IMPLEMENTS
)

(class supertype_construct
  (implements construct)
  (variable supertype_kind the_supertype_kind)
  (variable (list construct) supertypes)
  (variable (override) origin the_origin)
)

(enum type_kind
  NAMESPACE
  BLOCK
  INTERFACE
  DATATYPE
  ENUM
  CLASS
  SINGLETON
)

(class type_construct
  (implements construct)
  (variable (list modifier_construct) modifiers)
  (variable type_kind the_type_kind)
  (variable string name)
  (variable (nullable (list construct)) parameters)
  (variable (list construct) body)
  (variable (override) origin the_origin)
)

(dispatch construct_dispatch construct)

; Actions and analysis

(interface type
  (extends describable)
  (variable string name)
)

(interface analysis_result
  (extends origin)
)

(interface action
  (extends analysis_result)
  (variable type result)
)

(datatype type_action
  (extends action)
  (variable type the_type)
  (variable origin the_origin)
  (variable (override) type result the_type)
)

(datatype value_action
  (extends action)
  (variable type result)
  (variable origin the_origin)
)

(datatype notification_message
  (variable notification_type type)
  (variable string text)
)

(class enum_literal
  (implements value_action)
  (variable string name)
  (variable integer ordinal)
  (variable (override) principal_type result)
  (variable (override) origin the_origin)
)

(class singleton_literal
  (implements value_action)
  (variable (override) principal_type result)
  (variable (override) origin the_origin)
)

(class error_signal
  (implements action)
  (variable notification_message message)
  (variable origin the_origin)
  (variable (override) type result (. core_type ERROR))
)

(datatype principal_type
  (extends type)
  (variable string name)
  (variable type_kind the_type_kind)
  (variable (nullable principal_type) parent)
)

(singleton top_type
  (implements principal_type)
  (variable (override) string name "<top>")
  (variable (override) type_kind the_type_kind (. type_kind NAMESPACE))
  (variable (override) (nullable principal_type) parent null)
)

(enum core_type
  (implements principal_type)
  (variable (override) type_kind the_type_kind (. type_kind CLASS))
  (variable (override) principal_type parent (. top_type instance))
  VOID
  INTEGER
  STRING
  LIST
  SET
  NULL
  NULLABLE
  UNREACHABLE
  ERROR
)

(class parametrized_type
  (implements principal_type)
  (variable principal_type main)
  (variable (list type) parameters)
  ; TODO: use DOT operator
  (variable (override) string name (. main name))
  (variable (override) type_kind the_type_kind (. main the_type_kind))
  (variable (override) principal_type parent (. main parent))
)

(interface declaration
  (extends analysis_result origin)
  (variable principal_type declared_in_type)
)

(class type_declaration
  (implements declaration)
  (variable principal_type declared_type)
  (variable type_kind the_type_kind)
  (variable (override) principal_type declared_in_type)
  (variable (override) origin the_origin)
)

(class variable_declaration
  (implements declaration)
  (variable type value_type)
  (variable string name)
  (variable (override) principal_type declared_in_type)
  (variable (override) origin the_origin)
)

(datatype variable_action
  (extends action)
  (variable variable_declaration the_declaration)
  (variable (override) origin the_origin the_declaration)
  (variable (override) type result (. the_declaration value_type))
)

(class procedure_declaration
  (implements declaration)
  (variable type return_type)
  (variable string name)
  (variable (list variable_declaration) parameters)
  (variable (override) principal_type declared_in_type)
  (variable (override) origin the_origin)
)

(enum analysis_pass
  TYPE_PASS
  MEMBER_PASS
  BODY_PASS
)

(interface analysis_context
  ; action table——the equivalent of symbol table
  (procedure void add_action ((the type) (variable string name) (the action)))
  (procedure (nullable action) get_action ((the type) (variable string name)))
  ; type relationships
  (procedure void add_supertype ((variable type subtype) (variable type supertype)))
  (procedure (set type) get_all_supertypes ((the type)))
  (procedure (set type) get_direct_subtypes ((the type)))
  ; analysis binding
  (procedure void add_binding ((the construct) (the analysis_result)))
  (procedure (nullable analysis_result) get_binding ((the construct)))
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
  (VARIABLE_EXPECTED "Variable expected")
  (ANALYSIS_ERROR "Analysis error")
  (SYMBOL_LOOKUP_FAILED "Symbol lookup failed")
  (TYPE_EXPECTED "Type expected")
  (NOT_PARAMETRIZABLE "Not parametrizable")
  (WRONG_ARITY "Wrong arity")
  (IDENTIFIER_EXPECTED "Identifier expected")
)
