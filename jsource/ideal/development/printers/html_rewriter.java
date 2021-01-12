/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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

  private static boolean is_div(text_fragment fragment) {
    return fragment instanceof text_element &&
        ((text_element) fragment).get_id() == text_library.DIV;
  }

  @Override
  protected text_fragment rewrite_element(element_id id,
      immutable_dictionary<attribute_id, attribute_fragment> attributes,
      @Nullable text_fragment children) {

    if (id == text_library.INDENT) {
      return styles.wrap(styles.indent_style, children);
    } else if (id.get_namespace() == doc_elements.DOC_NS) {
      if (id == doc_elements.CODE) {
        return styles.wrap(styles.code_style, children);
      } else {
        return styles.wrap(styles.documentation_note_style, children);
      }
    } else if (id == text_library.DIV && attributes.is_empty() && is_div(children)) {
      return children;
    } else {
      return new base_element(id, attributes, children);
    }
  }
}
