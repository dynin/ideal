/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.texts;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
public abstract class text_rewriter extends text_visitor<text_fragment> {

  public text_fragment rewrite(text_fragment fragment) {
    return process(fragment);
  }

  @Override
  protected text_fragment process_string(string s) {
    return (base_string) s;
  }

  @Override
  protected text_fragment process_element(text_element element) {
    if (element.get_id() instanceof element_id) {
      element_id id = (element_id) element.get_id();
      immutable_list<text_node> children = element.children();
      if (!children.is_empty()) {
        children = text_util.to_list(rewrite(new base_list_text_node(children)));
      }

      text_fragment result = rewrite_element(id, children);
      assert result != null;
      return result;
    } else {
      // TODO: support rewriting attributes?
      return element;
    }
  }

  protected abstract text_fragment rewrite_element(element_id id,
      immutable_list<text_node> children);

  @Override
  protected text_fragment process_special(special_text t) {
    return t;
  }

  @Override
  protected text_fragment process_nodes(list_text_node nodes) {
    immutable_list<text_node> source = nodes.nodes();
    list<text_fragment> result = new base_list<text_fragment>();

    for (int i = 0; i < source.size(); ++i) {
      result.append(process(source.get(i)));
    }

    return text_util.join(result);
  }
}
