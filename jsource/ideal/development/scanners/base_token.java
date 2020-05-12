/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class base_token<P extends deeply_immutable_data> extends debuggable implements token {

  private final token_type type;
  private final P the_payload;
  private final origin pos;

  public base_token(token_type type, P the_payload, origin pos) {
    assert the_payload != null;
    this.type = type;
    this.the_payload = the_payload;
    this.pos = pos;
  }

  @Override
  public origin deeper_origin() {
    return pos;
  }

  @Override
  public token_type type() {
    return type;
  }

  @Override
  public P payload() {
    return the_payload;
  }

  @Override
  public string to_string() {
    // TODO: display origin? "@" pos.to_string().s());
    if (pos instanceof text_position) {
      return new base_string(type.to_string(), ":\"", ((text_position) pos).image());
    } else {
      return new base_string("[", type.to_string(), "]");
    }
  }
}
