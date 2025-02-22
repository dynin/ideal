; (hello world "!")
; (hi (hello world))
(interface text)

(interface describable
  (variable text description)
)

(interface origin
  (variable (nullable origin) deeper)
)
; comment...

(datatype source_text
  (extends origin describable)
  (variable string name)
  (variable (dont_describe) string content)
  (variable (nullable string) optional)
  (variable (override) (nullable origin) the_origin null)
  (variable (list string) list_test)
  (variable (override) string foo (. operator_type DOT))
)

(datatype source_text_too
  (extends origin describable)
  (variable origin the_origin)
  (variable (override) (nullable origin) deeper (. the_origin deeper))
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

(dispatch origin_dispatch origin)

(procedure (public) string test (
  (variable string foo)
))

(interface analysis_context_x
  (variable (nullable string) foo)
  (procedure (nullable string) add_action (
    (variable (nullable string) name)
  ))
)
