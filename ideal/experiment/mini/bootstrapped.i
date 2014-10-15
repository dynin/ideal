(interface () source (
  ; The source of the source: we must go deeper...
  (variable () (nullable source) the_source)
))

(datatype () source_text (
  (extends source)
  (variable () string name)
  (variable () string content)
))
