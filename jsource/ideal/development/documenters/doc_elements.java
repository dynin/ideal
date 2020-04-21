/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.documenters;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
public class doc_elements {

  public static final text_namespace DOC_NS = new base_namespace(new base_string("ideal-doc"));

  public static final element_id CODE = new base_element_id(DOC_NS, new base_string("code"));
  public static final element_id C = new base_element_id(DOC_NS, new base_string("c"));
  public static final element_id J = new base_element_id(DOC_NS, new base_string("j"));
  public static final element_id CPP = new base_element_id(DOC_NS, new base_string("cpp"));

  public static final dictionary<string, element_id> WHITELIST = new hash_dictionary<string, element_id>();

  private static void add_element(element_id id) {
    WHITELIST.put(id.short_name(), id);
  }

  static {
    add_element(text_library.DIV);
    add_element(text_library.EM);

    add_element(CODE);
    add_element(C);
    add_element(J);
    add_element(CPP);
  }
}
