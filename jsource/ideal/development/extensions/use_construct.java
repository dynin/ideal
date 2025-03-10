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
import ideal.development.constructs.*;
import ideal.development.analyzers.*;

public class use_construct extends extension_construct {
  public final simple_name name;

  public use_construct(simple_name name, origin source) {
    super(source);
    this.name = name;
  }

  public use_construct(name_construct the_name_construct, origin source) {
    super(source);
    this.name = (simple_name) the_name_construct.the_name;
  }

  public readonly_list<construct> children() {
    return new empty<construct>();
  }

  @Override
  public analyzable to_analyzable() {
    return base_analyzable_action.nothing(this);
  }

  @Override
  public text_fragment print(printer p) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(p.print_word(keywords.USE));
    fragments.append(p.print_space());
    fragments.append(p.print_simple_name(name));

    return text_utilities.join(fragments);
  }

  @Override
  public boolean is_terminated() {
    return false;
  }

  @Override
  public construct transform(transformer t) {
    return this;
  }
}
