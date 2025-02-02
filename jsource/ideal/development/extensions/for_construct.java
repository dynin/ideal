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

public class for_construct extends extension_construct {
  public construct init;
  public construct condition;
  public construct update;
  public construct body;

  public for_construct(construct init, construct condition, construct update, construct body,
      origin pos) {
    super(pos);
    this.init = init;
    this.condition = condition;
    this.update = update;
    this.body = body;
  }

  @Override
  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    result.append(init);
    result.append(condition);
    result.append(update);
    result.append(body);

    return result;
  }

  @Override
  public extension_analyzer to_analyzable() {
    return new for_analyzer(
        base_analyzer.make(init),
        base_analyzer.make(condition),
        base_analyzer.make(update),
        base_analyzer.make(body),
        this);
  }

  @Override
  public text_fragment print(printer p) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(p.print_word(keywords.FOR));
    fragments.append(p.print_space());

    list<text_fragment> expressions = new base_list<text_fragment>();
    if (!(init instanceof empty_construct)) {
      expressions.append(p.print(init));
    }
    expressions.append(p.print_punctuation(punctuation.SEMICOLON));
    if (!(condition instanceof empty_construct)) {
      expressions.append(p.print_space());
      expressions.append(p.print(condition));
    }
    expressions.append(p.print_punctuation(punctuation.SEMICOLON));

    // TODO: this is a hack; fix
    if (!(update instanceof empty_construct)) {
      expressions.append(p.print_space());
      expressions.append(p.print(update));
    }

    fragments.append(p.print_grouping_in_statement(text_utilities.join(expressions)));
    fragments.append(p.print_indented_statement(body));

    return text_utilities.join(fragments);
  }

  @Override
  public boolean is_terminated() {
    return true;
  }

  @Override
  public construct transform(transformer the_transformer) {
    return new for_construct(the_transformer.transform(init),
                             the_transformer.transform(condition),
                             the_transformer.transform(update),
                             the_transformer.transform(body),
                             this);
  }
}
