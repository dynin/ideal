/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.targets;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.components.*;
import ideal.development.names.*;
import ideal.development.constructs.*;
import ideal.development.analyzers.*;

public class target_construct extends extension_construct {
  public final simple_name name;
  public final construct expression;

  public target_construct(simple_name name, construct expression, position source) {
    super(source);
    this.name = name;
    this.expression = expression;
  }

  public target_construct(name_construct the_name_construct, construct expression,
      position source) {
    super(source);
    this.name = (simple_name) the_name_construct.the_name;
    this.expression = expression;
  }

  public readonly_list<construct> children() {
    return new base_list<construct>(expression);
  }

  @Override
  public analyzable to_analyzable() {
    return new target_declaration(this);
  }

  @Override
  public text_fragment print(printer p) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(p.print_word(keyword.TARGET));
    fragments.append(p.print_space());
    fragments.append(p.print_simple_name(name));
    fragments.append(p.print_space());
    fragments.append(p.print_punctuation(punctuation.COLON));
    fragments.append(p.print_space());
    fragments.append(p.print(expression));

    return text_util.join(fragments);
  }

  @Override
  public boolean is_terminated() {
    return false;
  }
}
