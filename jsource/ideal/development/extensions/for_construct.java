/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
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
      position pos) {
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
  public analyzable to_analyzable() {
    position pos = this;

    analyzable body_and_update = new statement_list_analyzer(
        new base_list<analyzable>(base_analyzer.make(body), base_analyzer.make(update)), pos);
    analyzable break_statement = new jump_analyzer(jump_type.BREAK_JUMP, pos);
    analyzable if_statement = new conditional_analyzer(base_analyzer.make(condition),
        body_and_update, break_statement, pos);
    analyzable loop_statement = new loop_analyzer(if_statement, pos);
    return new block_analyzer(new statement_list_analyzer(
        new base_list<analyzable>(base_analyzer.make(init), loop_statement), pos), pos);
  }

  @Override
  public text_fragment print(printer p) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(p.print_word(keyword.FOR));
    fragments.append(p.print_space());

    list<text_fragment> expressions = new base_list<text_fragment>();
    expressions.append(p.print(init));
    expressions.append(p.print_punctuation(punctuation.SEMICOLON));
    expressions.append(p.print_space());
    expressions.append(p.print(condition));
    expressions.append(p.print_punctuation(punctuation.SEMICOLON));
    expressions.append(p.print_space());
    // TODO: this is a hack; fix
    if (!(update instanceof empty_construct)) {
      expressions.append(p.print(update));
    }

    fragments.append(p.print_grouping_in_statement(text_util.join(expressions)));
    fragments.append(p.print_indented_statement(body));

    return text_util.join(fragments);
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
