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
import ideal.development.modifiers.*;
import ideal.development.declarations.*;

public class block_analyzer extends declaration_analyzer<origin>
    implements block_declaration {

  private static final special_name BLOCK_NAME =
      new special_name(new base_string("{"), new base_string("block_analyzer"));

  private readonly_list<annotation_construct> annotations;
  private final analyzable body;
  private principal_type inside;

  public block_analyzer(block_construct source) {
    super(source);
    annotations = source.annotations;
    body = new statement_list_analyzer(make_list(source.body), this);
  }

  // TODO: introduce an optional annotations parameter
  public block_analyzer(analyzable body, origin pos) {
    super(pos);
    this.body = body;
    annotations = new empty<annotation_construct>();
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
  public analyzable get_body() {
    return body;
  }

  @Override
  protected @Nullable error_signal do_multi_pass_analysis(analysis_pass pass) {
    if (pass != analysis_pass.BODY_CHECK) {
      return null;
    }

    assert annotations != null;
    process_annotations(annotations, access_modifier.private_modifier);

    if (inside == null) {
      inside = make_block(BLOCK_NAME, this);
    }

    if (find_error(body) != null) {
      return new error_signal(messages.error_in_block, body, this);
    }

    return null;
  }

  @Override
  protected analysis_result do_get_result() {
    return body.analyze();
  }
}
