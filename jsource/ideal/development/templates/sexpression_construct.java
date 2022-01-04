/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.templates;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.constructs.*;
import ideal.development.components.*;

public class sexpression_construct extends extension_construct {
  public final readonly_list<construct> elements;

  public sexpression_construct(readonly_list<construct> elements, origin source) {
    super(source);
    this.elements = elements;
  }

  @Override
  public readonly_list<construct> children() {
    return elements;
  }

  @Override
  public analyzable to_analyzable() {
    utilities.panic("Can't process an s-expression outside of a template");
    return null;
  }

  @Override
  public text_fragment print(printer p) {
    return new base_string("(...s-expression...");
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
