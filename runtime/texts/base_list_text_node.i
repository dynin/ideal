-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Text identifier corresponding to element identifiers in markup languages.
class base_list_text_node {
  extends debuggable;
  implements list_text_node;

  import ideal.machine.channels.string_writer;

  private immutable list[text_node] the_nodes;

  public base_list_text_node(readonly list[text_node] the_nodes) {
    this.the_nodes = the_nodes.frozen_copy();
  }

  public overload static list_text_node make(text_node first, text_node second) {
    return base_list_text_node.new([first, second]);
  }

  public overload static list_text_node make(text_node first, text_node second, text_node third) {
    return base_list_text_node.new([first, second, third]);
  }

  override immutable list[text_node] nodes() {
    return the_nodes;
  }

  override string to_string() {
    the_writer : string_writer.new();
    for (node : the_nodes) {
      the_writer.write_all(node.to_string);
    }
    return the_writer.elements();
  }
}
