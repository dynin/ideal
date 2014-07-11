/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.texts.*;
import ideal.development.documenters.*;
public class html_rewriter extends text_rewriter {

  private static text_fragment wrap(text_element element,
      @Nullable immutable_list<text_node> children) {

    if (children == null || children.is_empty()) {
      return element;
    }

    list<text_node> new_children = new base_list<text_node>(); // TODO: use list.copy()
    new_children.append_all(element.children());
    new_children.append_all(children);
    return new base_element(element.get_id(), new_children);
  }

  private static boolean is_div(text_node node) {
    return node instanceof text_element && ((text_element) node).get_id() == text_library.DIV;
  }

  @Override
  protected text_fragment rewrite_element(element_id id, immutable_list<text_node> children) {
    if (id == text_library.INDENT) {
      return wrap(styles.indent_style, children);
    } else if (id.get_namespace() == doc_elements.DOC_NS) {
      if (id == doc_elements.CODE) {
        return wrap(styles.code_style, children);
      } else {
        return wrap(styles.documentation_note_style, children);
      }
    } else if (id == text_library.DIV && children.size() == 1 && is_div(children.get(0))) {
      return children.get(0);
    } else {
      return new base_element(id, children);
    }
  }
}
