-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Describes underline style used in plain text rendering.
abstract class text_visitor[value result_type] {

  public result_type process(text_fragment fragment) {
    if (fragment is string) {
      return process_string(fragment);
    } else if (fragment is text_element) {
      return process_element(fragment);
    } else if (fragment is list_text_node) {
      return process_nodes(fragment);
    } else if (fragment is special_text) {
      return process_special(fragment);
    } else if (fragment is list_attribute_fragment) {
      return process_attributes(fragment);
    } else {
      utilities.panic("Unknown fragment: " ++ fragment);
    }
  }

  public result_type process_attribute(attribute_fragment fragment) {
    if (fragment is string) {
      return process_string(fragment);
    } else if (fragment is special_text) {
      return process_special(fragment);
    } else if (fragment is list_attribute_fragment) {
      return process_attributes(fragment);
    } else {
      utilities.panic("Unknown attribute fragment: " ++ fragment);
    }
  }

  protected abstract result_type process_string(string s);

  protected abstract result_type process_element(text_element element);

  protected abstract result_type process_nodes(list_text_node nodes);

  protected abstract result_type process_special(special_text t);

  protected abstract result_type process_attributes(list_attribute_fragment fragments);
}
