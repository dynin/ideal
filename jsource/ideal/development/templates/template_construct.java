/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.templates;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.constructs.*;
import ideal.development.components.*;

public class template_construct extends extension_construct {
  public final sexpression_construct body;

  public template_construct(sexpression_construct body, origin source) {
    super(source);
    this.body = body;
  }

  @Override
  public readonly_list<construct> children() {
    return new base_list<construct>(body);
  }

  @Override
  public analyzable to_analyzable() {
    return new template_analyzer(this);
  }

  @Override
  public text_fragment print(printer p) {
    return p.print_line(new base_string("#(...temlate...)"));
  }

  @Override
  public boolean is_terminated() {
    return true;
  }
}
