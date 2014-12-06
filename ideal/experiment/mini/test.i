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
)

(enum test_enum
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
