/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.futures.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.flags.*;
import ideal.development.values.*;
import static ideal.development.declarations.annotation_library.*;

public class grammar_analyzer extends declaration_analyzer<grammar_construct>
    implements declaration {

  private principal_type grammar_parent;

  public grammar_analyzer(grammar_construct the_grammar_construct) {
    super(the_grammar_construct);
  }

  public action_name short_name() {
    return source.name;
  }

  @Override
  public readonly_list<analyzable> children() {
    list<analyzable> result = new base_list<analyzable>();

    result.append(annotations());

    return result;
  }

  @Override
  protected signal do_multi_pass_analysis(analysis_pass pass) {
    if (pass == analysis_pass.METHOD_AND_VARIABLE_DECL) {
      origin the_origin = this;
      process_annotations(source.annotations, access_modifier.public_modifier);
      grammar_parent = action_utilities.make_type(get_context(), type_kinds.block_kind, null,
          short_name(), declared_in_type(), this, the_origin);
    }

    return ok_signal.instance;
  }

  @Override
  protected action do_get_result() {
    origin the_origin = this;
    return grammar_parent.to_action(this);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}
