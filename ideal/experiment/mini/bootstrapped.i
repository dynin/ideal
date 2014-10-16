; Copyright 2014 The Ideal Authors. All rights reserved.
;
; Use of this source code is governed by a BSD-style
; license that can be found in the LICENSE file or at
; https://developers.google.com/open-source/licenses/bsd

(interface () source (
  ; The source of the source: we must go deeper...
  (variable (nullable source) the_source)
))

(datatype () source_text (
  (extends source)
  (variable string name)
  (variable string content)
  (variable () (nullable source) the_source null)
))

(datatype () text_position (
  (extends source)
  (variable source_text the_source)
  (variable int character_index)
))

(enum () token_type (
  WHITESPACE
  COMMENT
  OPEN
  CLOSE
  IDENTIFIER
  LITERAL
  MODIFIER
))
