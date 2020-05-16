-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Helper class for rewriting structured text.
abstract class text_rewriter {
  extends text_visitor[text_fragment];

  text_fragment rewrite(text_fragment fragment) {
    return process(fragment);
  }

  protected abstract text_fragment rewrite_element(element_id id,
      immutable dictionary[attribute_id, string] attributes,
      text_fragment or null children);

  -- TODO: inherit protected access modifier from supertype.
  protected override text_fragment process_string(string s) {
    return s as base_string;
  }

  protected override text_fragment process_element(text_element element) {
    var children : element.children;
    if (children is_not null) {
      children = rewrite(children);
    }
    return rewrite_element(element.get_id, element.attributes, children);
  }

  protected override text_fragment process_special(special_text t) {
    return t;
  }

  protected override text_fragment process_nodes(list_text_node nodes) {
    result : base_list[text_fragment].new();

    for (source : nodes.nodes) {
      result.append(process(source));
    }

    return text_util.join(result);
  }
}
