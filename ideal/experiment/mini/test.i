; (hello world "!")
; (hi (hello world))
(interface text)

(interface describable
  (variable text description)
)

(interface source
  (variable (nullable source) deeper)
)
; comment...

(datatype source_text
  (extends source describable)
  (variable string name)
  (variable (dont_describe) string content)
  (variable (nullable string) optional)
  (variable (override) (nullable source) the_source null)
  (variable (list string) list_test)
  (variable (override) string foo (. operator_type DOT))
)

(datatype source_text_too
  (extends source describable)
  (variable source the_source)
  (variable (override) (nullable source) deeper (. the_source deeper))
)

(enum test_enum
  (implements describable)
  foo
  bar
)

(singleton empty
  (implements text)
)

(enum operator_type
  (DOT ".")
  (ASSIGN "=")

  (variable string symbol)
)
