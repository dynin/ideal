/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.templates;

import ideal.library.elements.*;
import ideal.library.texts.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.constructs.*;
import ideal.development.extensions.*;
import ideal.development.flavors.*;
import ideal.development.modifiers.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;

public class sexpression_library {

  private static dictionary<action_name, sexpression_handler> handler_library =
      new hash_dictionary<action_name, sexpression_handler>();

  public static @Nullable sexpression_handler lookup(action_name name) {
    return handler_library.get(name);
  }

  private static void add_handler(sexpression_handler handler) {
    handler_library.put(handler.name(), handler);
  }

  static {
    add_handler(new element_handler(text_library.BODY));
    add_handler(new element_handler(text_library.DIV));
    add_handler(new element_handler(text_library.H1));
    add_handler(new element_handler(text_library.H2));
    add_handler(new element_handler(text_library.A));

    add_handler(new attribute_handler(text_library.STYLE));
    add_handler(new attribute_handler(text_library.NAME));

    add_handler(new iteration_handler());
  }

  private sexpression_library() { }
}
