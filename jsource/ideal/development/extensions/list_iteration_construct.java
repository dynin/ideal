/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.extensions;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.components.*;
import ideal.development.names.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;

public class list_iteration_construct extends extension_construct {
  public final variable_construct var_decl;
  public final construct body;

  public list_iteration_construct(variable_construct var_decl, construct body, origin pos) {
    super(pos);
    this.var_decl = var_decl;
    this.body = body;
  }

  @Override
  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    result.append(var_decl);
    result.append(body);

    return result;
  }

  @Override
  public extension_analyzer to_analyzable() {
    return new list_iteration_analyzer(this);
  }

  @Override
  public text_fragment print(printer p) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(p.print_word(keywords.FOR));
    fragments.append(p.print_space());
    fragments.append(p.print_punctuation(punctuation.OPEN_PARENTHESIS));
    fragments.append(p.print(var_decl));
    fragments.append(p.print_punctuation(punctuation.CLOSE_PARENTHESIS));
    fragments.append(p.print_indented_statement(body));

    return text_utilities.join(fragments);
  }

  @Override
  public boolean is_terminated() {
    return true;
  }

  @Override
  public construct transform(transformer the_transformer) {
    return new list_iteration_construct((variable_construct) the_transformer.transform(var_decl),
        the_transformer.transform(body), this);
  }
}
