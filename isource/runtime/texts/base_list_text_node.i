-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Text identifier corresponding to element identifiers in markup languages.
class base_list_text_node {
  extends debuggable;
  implements list_text_node;

  private immutable list[text_node] the_nodes;

  public base_list_text_node(readonly list[text_node] the_nodes) {
    this.the_nodes = the_nodes.frozen_copy();
  }

  public overload static list_text_node make(text_node first, text_node second) {
    list[text_node] result : base_list[text_node].new();
    result.append(first);
    result.append(second);
    return base_list_text_node.new(result);
  }

  public overload static list_text_node make(text_node first, text_node second, text_node third) {
    list[text_node] result : base_list[text_node].new();
    result.append(first);
    result.append(second);
    result.append(third);
    return base_list_text_node.new(result);
  }

  override immutable list[text_node] nodes() {
    return the_nodes;
  }

  override string to_string() {
    -- TODO: return the_nodes.to_string() ?..
    return "base_list_text_node...";
  }
}
