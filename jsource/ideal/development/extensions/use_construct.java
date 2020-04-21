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
import ideal.development.constructs.*;
import ideal.development.analyzers.*;

public class use_construct extends extension_construct {
  public final simple_name name;

  public use_construct(simple_name name, position source) {
    super(source);
    this.name = name;
  }

  public use_construct(name_construct the_name_construct, position source) {
    super(source);
    this.name = (simple_name) the_name_construct.the_name;
  }

  public readonly_list<construct> children() {
    return new empty<construct>();
  }

  @Override
  public analyzable to_analyzable() {
    return analyzable_action.nothing(this);
  }

  @Override
  public text_fragment print(printer p) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(p.print_word(keyword.USE));
    fragments.append(p.print_space());
    fragments.append(p.print_simple_name(name));

    return text_util.join(fragments);
  }

  @Override
  public boolean is_terminated() {
    return false;
  }
}
