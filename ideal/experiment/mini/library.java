/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.experiment.mini;

import static ideal.experiment.mini.bootstrapped.text;
import static ideal.experiment.mini.bootstrapped.text_string;
import static ideal.experiment.mini.bootstrapped.indented_text;
import static ideal.experiment.mini.bootstrapped.text_list;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

public class library {

  public static text join_text(text... texts) {
    return new text_list(Arrays.asList(texts));
  }

  public static text join_text(List<text> texts, text separator) {
    List<text> result = new ArrayList<text>();
    for (int i = 0; i < texts.size(); ++i) {
      if (i > 0) {
        result.add(separator);
      }
      result.add(texts.get(i));
    }
    return new text_list(result);
  }

  public static String render_text(text the_text) {
    StringBuilder result = new StringBuilder();
    do_render_text(the_text, true, 0, result);
    return result.toString();
  }

  private static final String INDENT_STRING = "  ";

  private static boolean do_render_text(text the_text, boolean first, int indent,
      StringBuilder result) {
    if (the_text instanceof text_string) {
      String value = ((text_string) the_text).value();
      for (int i = 0; i < value.length(); ++i) {
        char c = value.charAt(i);
        if (c != '\n') {
          if (first) {
            for (int j = 0; j < indent; ++j) {
              result.append(INDENT_STRING);
            }
          }
          result.append(c);
          first = false;
        } else {
          result.append('\n');
          first = true;
        }
      }
    } else if (the_text instanceof indented_text) {
      first = do_render_text(((indented_text) the_text).inside(), first, indent + 1, result);
    } else {
      assert the_text instanceof text_list;
      for (text sub_text : ((text_list)the_text).texts()) {
        first = do_render_text(sub_text, first, indent, result);
      }
    }

    return first;
  }
}
