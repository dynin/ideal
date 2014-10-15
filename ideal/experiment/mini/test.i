(hello + world "!")
(hi (hello world))
(datatype () source (
  (variable () (nullable source) deeper)
))
; comment...
(interface () source_text (
  (extends source)
  (variable () string name)
  (variable () string content)
  (variable (override) (nullable source) deeper null)
))

(datatype () source_text (
  (extends source)
  (variable () string name)
  (variable () string content)
  (variable (override) (nullable source) deeper null)
))
