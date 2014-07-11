/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.documenters;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.texts.*;

public class doc_comment_parser {

  public static text_fragment parse(string s) {
    return to_tree(parse_to_list(s));
  }

  private static readonly_list<text_event> parse_to_list(string s) {
    list<text_event> result = new base_list<text_event>();

    String content = utilities.s(s);
    int index = 0;
    StringBuilder sb = new StringBuilder();
    boolean in_code = false;

    while (index < content.length()) {
      char c1 = content.charAt(index++);
      if (c1 != '<') {
	if (c1 == '|') {
	  append_string(result, sb);
          if (!in_code) {
            result.append(new start_element(doc_elements.CODE));
          } else {
            result.append(new end_element(doc_elements.CODE));
          }
	  in_code = !in_code;
	} else {
	  sb.append(c1);
	}
	continue;
      }
      append_string(result, sb);

      while (index < content.length()) {
	char c2 = content.charAt(index++);
	if (c2 == '>') {
	  break;
	}
	sb.append(c2);
      }
      String tag = sb.toString().trim();
      sb.setLength(0);

      assert tag.indexOf(' ') == -1;
      boolean end_tag = false;
      if (tag.charAt(0) == '/') {
        end_tag = true;
	tag = tag.substring(1);
	// TODO: block markup?...
      }

      string tag_name = new base_string(tag);
      element_id tag_id = doc_elements.WHITELIST.get(tag_name);

      if (tag_id == null) {
        // TODO: Should report error and continue...
        utilities.panic("Unrecognized tag " + tag_name);
      }

      if (!end_tag) {
        result.append(new start_element(tag_id));
      } else {
        result.append(new end_element(tag_id));
      }
    }

    append_string(result, sb);

    return result;
  }

  private static void append_string(list<text_event> result, StringBuilder sb) {
    if (sb.length() > 0) {
      result.append(new string_event(new base_string(sb.toString())));
      sb.setLength(0);
    }
  }

  // TODO: this should be moved to generic location, once error handling is done generically.
  private static text_fragment to_tree(readonly_list<text_event> events) {
    // return (base_string) displayer.to_plain_string(events);
    // TODO: create a data structure for ( start_element, list<text_fragment> )
    list<list<text_fragment>> fragments_tree = new base_list<list<text_fragment>>();
    fragments_tree.append(new base_list<text_fragment>());
    list<start_element> start_tags = new base_list<start_element>();

    for (int i = 0; i < events.size(); ++i) {
      text_event event = events.get(i);
      list<text_fragment> fragments = fragments_tree.get(fragments_tree.size() - 1);

      if (event instanceof string_event) {
        fragments.append(((string_event) event).s);
      } else if (event instanceof start_element) {
        start_tags.append((start_element) event);
        fragments_tree.append(new base_list<text_fragment>());
      } else if (event instanceof end_element) {
        if (start_tags.is_empty()) {
          fragments.append(report_error("Unexpected close tag: " + event));
          continue;
        }
        start_element start_tag = start_tags.remove_last();
        fragments_tree.remove_last();
        if (start_tag.get_id() != ((end_element) event).get_id()) {
          fragments.append(report_error("Unmatched close tag: " + event));
          continue;
        }
        list<text_fragment> upper_fragments = fragments_tree.get(fragments_tree.size() - 1);
        upper_fragments.append(base_element.make(start_tag.get_id(), text_util.join(fragments)));
      } else {
        utilities.panic("Unknown event: " + event);
      }
    }

    if (fragments_tree.size() != 1) {
      assert !start_tags.is_empty();
      return report_error("Some tags were not closed, for example " + start_tags.remove_last());
    }

    return text_util.join(fragments_tree.remove_last());
  }

  private static text_fragment report_error(String s) {
    return new base_string(" [*** ", s, " ***] ");
  }
}
