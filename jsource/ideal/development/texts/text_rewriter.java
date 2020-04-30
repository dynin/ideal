/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
    @Nullable text_fragment children = rewrite(element.children());
    return rewrite_element(element.get_id(), element.attributes(), children);
  }

  protected abstract text_fragment rewrite_element(element_id id,
      immutable_dictionary<attribute_id, string> attributes,
      @Nullable text_fragment children);

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
