(hello + world "!")
(hi (hello world))
(interface () source (
  (variable () (nullable source) deeper)
))
; comment...

(datatype () source_text (
  (extends source)
  (variable () string name)
  (variable () string content)
  (variable (override) (nullable source) the_source null)
))
