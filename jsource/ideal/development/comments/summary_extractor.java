/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.comments;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import javax.annotation.Nullable;

public class summary_extractor extends text_visitor<String> {

  public static string get_summary(text_fragment the_text) {
    return new base_string(instance.process(the_text));
  }

  public static final summary_extractor instance = new summary_extractor();

  private final static char dot = '.';

  private summary_extractor() { }

  @Override
  protected String process_string(string the_string) {
    String content = utilities.s(the_string);
    int index = content.indexOf(dot);
    if (index < 0) {
      return content;
    } else {
      return content.substring(0, index + 1);
    }
  }

  private String process_list(immutable_list<text_node> nodes) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < nodes.size(); ++i) {
      String s = process(nodes.get(i));
      result.append(s);
      if (endsWithDot(s)) {
        break;
      }
    }
    return result.toString();
  }

  private boolean endsWithDot(String s) {
    return !s.isEmpty() && s.charAt(s.length() - 1) == dot;
  }

  @Override
  protected String process_element(text_element element) {
    @Nullable text_fragment children = element.children();
    if (children != null) {
      return process(children);
    } else {
      return "";
    }
  }

  @Override
  protected String process_nodes(list_text_node nodes_list) {
    return process_list(nodes_list.nodes());
  }

  @Override
  protected String process_special(special_text t) {
    utilities.panic("....");
    return null;
  }
}
