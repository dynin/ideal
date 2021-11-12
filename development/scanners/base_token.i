-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class base_token[deeply_immutable data payload_type] {
  extends debuggable;
  implements token;

  private token_type the_type;
  private payload_type the_payload;
  private the origin;

  base_token(token_type the_type, payload_type the_payload, the origin) {
    verify the_payload is_not null;
    this.the_type = the_type;
    this.the_payload = the_payload;
    this.the_origin = the_origin;
  }

  override origin deeper_origin => the_origin;

  override token_type type => the_type;

  override payload_type payload => the_payload;

  override string to_string() {
    if (the_origin is text_origin) {
      return the_type ++ ":\"" ++ the_origin.image;
    } else {
      return "[" ++ the_type ++ "]";
    }
  }
}
