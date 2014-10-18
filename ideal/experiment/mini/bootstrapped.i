; Copyright 2014 The Ideal Authors. All rights reserved.
;
; Use of this source code is governed by a BSD-style
; license that can be found in the LICENSE file or at
; https://developers.google.com/open-source/licenses/bsd

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
  (variable token_type type);
)

(class simple_token
  (implements token describable)
  (variable (override) token_type type);
  (variable (override) source the_source)
)

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
  (variable notification_type type)
  (variable source the_source)
)
