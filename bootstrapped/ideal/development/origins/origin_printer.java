// Autogenerated from development/origins/origin_printer.i

package ideal.development.origins;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.library.channels.*;
import ideal.library.resources.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.patterns.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;

import javax.annotation.Nullable;

public class origin_printer {
  public static text_fragment show_origin(@Nullable origin the_origin) {
    @Nullable text_origin fragment_begin = null;
    @Nullable text_origin fragment_end = null;
    while (true) {
      if (the_origin instanceof text_origin) {
        return origin_printer.render_text_origin(((text_origin) the_origin), fragment_begin, fragment_end);
      } else if (the_origin instanceof special_origin) {
        final base_string description = (base_string) ((special_origin) the_origin).description;
        return base_element.make(text_library.DIV, description);
      } else if (the_origin instanceof fragment_origin) {
        if (fragment_begin == null) {
          fragment_begin = origin_printer.find_text_origin(((fragment_origin) the_origin).begin, true);
        }
        if (fragment_end == null) {
          fragment_end = origin_printer.find_text_origin(((fragment_origin) the_origin).end, false);
        }
      } else if (the_origin instanceof source_content) {
        return (base_string) new base_string("");
      } else if (the_origin == null) {
        {
          utilities.panic(new base_string("Can\'t display origin"));
          return null;
        }
      }
      assert the_origin != null;
      the_origin = the_origin.deeper_origin();
    }
  }
  private static @Nullable text_origin find_text_origin(@Nullable origin the_origin, final boolean begin) {
    while (the_origin != null) {
      if (the_origin instanceof text_origin) {
        return ((text_origin) the_origin);
      } else if (the_origin instanceof fragment_origin) {
        the_origin = begin ? ((fragment_origin) the_origin).begin : ((fragment_origin) the_origin).end;
      } else {
        the_origin = the_origin.deeper_origin();
      }
    }
    return null;
  }
  public static text_node render_text_origin(final text_origin the_text_origin, final @Nullable text_origin fragment_begin, final @Nullable text_origin fragment_end) {
    Integer highlight_begin = the_text_origin.begin;
    if (fragment_begin != null) {
      if (fragment_begin.source == the_text_origin.source && fragment_begin.begin < the_text_origin.begin) {
        highlight_begin = fragment_begin.begin;
      }
    }
    Integer highlight_end = the_text_origin.end;
    if (fragment_end != null) {
      if (fragment_end.source == the_text_origin.source && fragment_end.end > the_text_origin.end) {
        highlight_end = fragment_end.end;
      }
    }
    return origin_printer.do_render_text_origin(the_text_origin, highlight_begin, highlight_end);
  }
  private static final singleton_pattern<Character> newline_pattern = new singleton_pattern<Character>('\n');
  private static text_node do_render_text_origin(final text_origin the_text_origin, Integer highlight_begin, Integer highlight_end) {
    final string input = the_text_origin.source.content;
    Integer begin = the_text_origin.begin;
    if (ideal.machine.elements.runtime_util.values_equal(begin, input.size()) && begin > 0) {
      final Integer before_begin = begin - 1;
      assert before_begin >= 0;
      if (input.get(before_begin) == '\n') {
        begin = before_begin;
      }
    }
    final @Nullable range line_begin_range = origin_printer.newline_pattern.find_last(input, begin);
    final Integer line_begin = line_begin_range != null ? line_begin_range.begin() + 1 : 0;
    string prefix;
    string highlight_prefix;
    if (highlight_begin < begin) {
      if (highlight_begin < line_begin) {
        highlight_begin = line_begin;
      }
      prefix = input.slice(line_begin, highlight_begin);
      highlight_prefix = input.slice(highlight_begin, begin);
    } else {
      prefix = input.slice(line_begin, begin);
      highlight_prefix = new base_string("");
    }
    final @Nullable range line_end = origin_printer.newline_pattern.find_first(input, line_begin);
    Integer end = the_text_origin.end;
    boolean suffix_caret = false;
    if (ideal.machine.elements.runtime_util.values_equal(end, begin)) {
      end = begin + 1;
    }
    if (line_end != null) {
      if (end > line_end.begin()) {
        end = line_end.begin();
        suffix_caret = true;
      }
    }
    final string underlined = ideal.machine.elements.runtime_util.concatenate(input.slice(begin, end), (suffix_caret ? new base_string(" ") : new base_string("")));
    string suffix = new base_string("");
    string highlight_suffix = new base_string("");
    if (line_end != null) {
      if (end < line_end.begin()) {
        if (highlight_end > end) {
          if (highlight_end > line_end.begin()) {
            highlight_end = line_end.begin();
          }
          highlight_suffix = input.slice(end, highlight_end);
          suffix = input.slice(highlight_end, line_end.begin());
        } else {
          suffix = input.slice(end, line_end.begin());
        }
      }
    }
    final text_element underlined_element = base_element.make(text_library.U, (base_string) underlined);
    text_element highlighted_element;
    if (highlight_prefix.is_empty() && highlight_suffix.is_empty()) {
      highlighted_element = underlined_element;
    } else {
      final text_fragment highlighted = text_util.join((base_string) highlight_prefix, underlined_element, (base_string) highlight_suffix);
      highlighted_element = new base_element(text_library.U2, new list_dictionary<attribute_id, attribute_fragment>(), highlighted);
    }
    final text_fragment text_line = text_util.join((base_string) prefix, highlighted_element, (base_string) suffix);
    return new base_element(text_library.DIV, new list_dictionary<attribute_id, attribute_fragment>(), text_line);
  }
}
