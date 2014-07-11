-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implementation of text entities.
class text_entity {
  extends debuggable;
  implements special_text;
  implements reference_equality;

  private text_namespace the_namespace;
  private string plain_text;
  private string markup;

  text_entity(text_namespace the_namespace, string plain_text, string markup) {
    this.the_namespace = the_namespace;
    this.plain_text = plain_text;
    -- TODO: make sure markup is in the form of &foo;
    this.markup = markup;
  }

  override string to_plain_text() {
    return plain_text;
  }

  override string to_markup() {
    return markup;
  }

  override string to_string() {
    return markup;
  }
}
