/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;

public class block_analyzer extends single_pass_analyzer implements declaration {

  private static final special_name BLOCK_NAME =
      new special_name(new base_string("{"), new base_string("block_analyzer"));

  private final analyzable body;
  private principal_type inside;

  public block_analyzer(analyzable body, origin pos) {
    super(pos);
    this.body = body;
  }

  public block_analyzer(block_construct source) {
    super(source);
    body = new statement_list_analyzer(make_list(source.body), this);
  }

  @Override
  public principal_type inner_type() {
    return inside;
  }

  //@Override
  public action_name short_name() {
    return BLOCK_NAME;
  }

  @Override
  protected analysis_result do_single_pass_analysis() {
    if (inside == null) {
      inside = make_block(BLOCK_NAME, this);
    }

    if (find_error(body) != null) {
      return new error_signal(messages.error_in_block, body, this);
    }
    return body.analyze();
  }
}
