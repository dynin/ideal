/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
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

public class while_construct extends extension_construct {
  public final construct condition;
  public final construct body;

  public while_construct(construct condition, construct body, origin pos) {
    super(pos);
    this.condition = condition;
    this.body = body;
  }

  @Override
  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    result.append(condition);
    result.append(body);

    return result;
  }

  @Override
  public extension_analyzer to_analyzable() {
    return new while_analyzer(
        base_analyzer.make(condition),
        base_analyzer.make(body),
        this);
  }

  @Override
  public text_fragment print(printer p) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(p.print_word(keywords.WHILE));
    fragments.append(p.print_space());
    fragments.append(p.print_grouping_in_statement(p.print(condition)));
    fragments.append(p.print_indented_statement(body));

    return text_utilities.join(fragments);
  }

  @Override
  public boolean is_terminated() {
    return true;
  }

  @Override
  public construct transform(transformer the_transformer) {
    return new while_construct(the_transformer.transform(condition),
        the_transformer.transform(body), this);
  }
}
