/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.notifications;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.scanners.*;

/**
 * A collection of methods responsible for displaying position
 * infromation associatd with error and warning messages.
 */
public class position_printer {

  /** If possible, display information about a position. */
  public static text_fragment show_position(position pos) {
    assert pos != null;
    @Nullable text_position fragment_begin = null;
    @Nullable text_position fragment_end = null;
    // We only know how to display selected positions,
    // so drill down until we find one.
    while (true) {
      if (pos instanceof text_position) {
        return render_text_position((text_position) pos, fragment_begin, fragment_end);
      } else if (pos instanceof special_position) {
        string description = ((special_position) pos).description;
        return base_element.make(text_library.DIV, (base_string) description);
      } else if (pos instanceof fragment_position) {
        fragment_position fp = (fragment_position) pos;
        if (fragment_begin == null) {
          fragment_begin = find_text_position(fp.begin, true);
        }
        if (fragment_end == null) {
          fragment_end = find_text_position(fp.end, false);
        }
      } else if (pos.source_position() == null) {
        utilities.panic("Can't display position " + pos + " of type " + pos.getClass());
      }
      pos = pos.source_position();
    }
  }

  private static @Nullable text_position find_text_position(position pos, boolean begin) {
    while (pos != null) {
      if (pos instanceof text_position) {
        return (text_position) pos;
      } else if (pos instanceof fragment_position) {
        fragment_position fp = (fragment_position) pos;
        pos = begin ? fp.begin : fp.end;
      } else {
        pos = pos.source_position();
      }
    }
    return null;
  }

  public static text_node render_text_position(text_position pos,
      @Nullable text_position fragment_begin,
      @Nullable text_position fragment_end) {

    int highlight_begin = pos.begin;
    if (fragment_begin != null && fragment_begin.source == pos.source &&
        fragment_begin.begin < pos.begin) {
      highlight_begin = fragment_begin.begin;
    }

    int highlight_end = pos.end;
    if (fragment_end != null && fragment_end.source == pos.source &&
        fragment_end.end > pos.end) {
      highlight_end = fragment_end.end;
    }

    return render_text_position(pos, highlight_begin, highlight_end);
  }

  /**
   * Display information about a text position -- a line and carets
   * underlining the exact location, e.g.
   * <pre>
   * int foo;
   *     ^^^
   * </pre>
   */
  public static text_node render_text_position(text_position pos,
      int highlight_begin, int highlight_end) {

    String input = utilities.s(pos.source.content);
    int begin = pos.begin;
    // nicer display of the end of file -- no caret by itself
    if (begin == input.length() &&
        begin > 0 &&
        input.charAt(begin - 1) == '\n') {
      begin = begin - 1;
    }
    int line_begin = input.lastIndexOf('\n', begin - 1) + 1;

    base_string prefix;
    base_string highlight_prefix;
    if (highlight_begin < begin) {
      if (highlight_begin < line_begin) {
        highlight_begin = line_begin;
      }
      prefix = new base_string(input.substring(line_begin, highlight_begin));
      highlight_prefix = new base_string(input.substring(highlight_begin, begin));
    } else {
      prefix = new base_string(input.substring(line_begin, begin));
      highlight_prefix = new base_string("");
    }

    int line_end = input.indexOf('\n', line_begin);
    int end = pos.end;

    // print an extra caret:
    // 1) if there were no carets printed (e.g. at the end of file),
    // 2) to signal that position continues past newline
    boolean suffix_caret = false;
    if (end == begin) {
      end = begin + 1;
    }
    if (end > line_end) {
      end = line_end;
      suffix_caret = true;
    }
    base_string underlined = new base_string(input.substring(begin, end) +
        (suffix_caret ? " " : ""));

    base_string suffix = new base_string("");
    base_string highlight_suffix = new base_string("");
    if (end < line_end) {
      if (highlight_end > end) {
        if (highlight_end > line_end) {
          highlight_end = line_end;
        }
        highlight_suffix = new base_string(input.substring(end, highlight_end));
        suffix = new base_string(input.substring(highlight_end, line_end));
      } else {
        suffix = new base_string(input.substring(end, line_end));
      }
    }

    text_element underlined_element = base_element.make(text_library.UNDERLINE, underlined);
    text_element highlighted_element;
    if (highlight_prefix.is_empty() && highlight_suffix.is_empty()) {
      highlighted_element = underlined_element;
    } else {
      text_fragment highlighted = text_util.join(highlight_prefix, underlined_element,
          highlight_suffix);
      highlighted_element = new base_element(text_library.UNDERLINE2,
          new list_dictionary<attribute_id, string>(), highlighted);
    }
    text_fragment text_line = text_util.join(prefix, highlighted_element, suffix);

    return new base_element(text_library.DIV,
          new list_dictionary<attribute_id, string>(), text_line);
  }
}
