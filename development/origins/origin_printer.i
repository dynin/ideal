-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A collection of methods responsible for displaying origin
--- infromation associated with error and warning messages.
namespace origin_printer {

  --- If possible, display information about an origin.
  text_fragment show_origin(var origin or null the_origin) {
    var text_origin or null fragment_begin : missing.instance;
    var text_origin or null fragment_end : missing.instance;
    -- We only know how to display selected origins,
    -- so drill down until we find one.
    loop {
      if (the_origin is text_origin) {
        return render_text_origin(the_origin, fragment_begin, fragment_end);
      } else if (the_origin is special_origin) {
        -- TODO: retire cast
        description : the_origin.description !> base_string;
        return base_element.new(text_library.DIV, description);
      } else if (the_origin is fragment_origin) {
        if (fragment_begin is null) {
          fragment_begin = find_text_origin(the_origin.begin, true);
        }
        if (fragment_end is null) {
          fragment_end = find_text_origin(the_origin.end, false);
        }
      } else if (the_origin is source_content) {
        -- TODO: is there a better way to handle this?
        -- TODO: redundant cast.
        return "" !> base_string;
      } else if (the_origin is null) {
        utilities.panic("Can't display origin");
      }
      -- TODO: this assertion should be redundant. Update conditional_analyzer
      assert the_origin is_not null;
      the_origin = the_origin.deeper_origin;
    }
  }

  private text_origin or null find_text_origin(var origin or null the_origin, boolean begin) {
    while (the_origin is_not null) {
      if (the_origin is text_origin) {
        return the_origin;
      } else if (the_origin is fragment_origin) {
        the_origin = begin ? the_origin.begin : the_origin.end;
      } else {
        the_origin = the_origin.deeper_origin;
      }
    }
    return missing.instance;
  }

  text_node render_text_origin(text_origin the_text_origin,
      text_origin or null fragment_begin,
      text_origin or null fragment_end) {

    var highlight_begin : the_text_origin.begin;
    -- TODO: use &&
    if (fragment_begin is_not null) {
      if (fragment_begin.source == the_text_origin.source &&
          fragment_begin.begin < the_text_origin.begin) {
        highlight_begin = fragment_begin.begin;
      }
    }

    var highlight_end : the_text_origin.end;
    -- TODO: use &&
    if (fragment_end is_not null) {
      if (fragment_end.source == the_text_origin.source &&
         fragment_end.end > the_text_origin.end) {
        highlight_end = fragment_end.end;
      }
    }

    return do_render_text_origin(the_text_origin, highlight_begin, highlight_end);
  }

  private newline_pattern : singleton_pattern[character].new('\n');

  --- Display information about a text position -- a line and carets
  --- underlining the exact location, e.g.
  --- <pre>
  --- integer foo;
  ---         ^^^
  --- </pre>
  private text_node do_render_text_origin(text_origin the_text_origin,
      var nonnegative highlight_begin, var nonnegative highlight_end) {

    input : the_text_origin.source.content;
    var begin : the_text_origin.begin;
    -- nicer display of the end of file -- no caret by itself
    if (begin == input.size && begin > 0) {
      before_begin : begin - 1;
      assert before_begin is nonnegative;
      if (input[before_begin] == '\n') {
        begin = before_begin;
      }
    }

    line_begin_range : newline_pattern.find_last(input, begin);
    nonnegative line_begin : line_begin_range is_not null ? line_begin_range.begin + 1 : 0;

    var string prefix;
    var string highlight_prefix;
    if (highlight_begin < begin) {
      if (highlight_begin < line_begin) {
        highlight_begin = line_begin;
      }
      prefix = input.slice(line_begin, highlight_begin);
      highlight_prefix = input.slice(highlight_begin, begin);
    } else {
      prefix = input.slice(line_begin, begin);
      highlight_prefix = "";
    }

    line_end : newline_pattern.find_first(input, line_begin);
    var end : the_text_origin.end;

    -- print an extra caret:
    -- 1) if there were no carets printed (e.g. at the end of file),
    -- 2) to signal that origin continues past newline
    var suffix_caret : false;
    if (end == begin) {
      end = begin + 1;
    }
    if (line_end is_not null) {
      -- TODO: use &&
      if (end > line_end.begin) {
        end = line_end.begin;
        suffix_caret = true;
      }
    }
    underlined : input.slice(begin, end) ++ (suffix_caret ? " " : "");

    var suffix : "";
    var highlight_suffix : "";
    -- TODO: use &&
    if (line_end is_not null) {
      if (end < line_end.begin) {
        if (highlight_end > end) {
          if (highlight_end > line_end.begin) {
            highlight_end = line_end.begin;
          }
          highlight_suffix = input.slice(end, highlight_end);
          suffix = input.slice(highlight_end, line_end.begin);
        } else {
          suffix = input.slice(end, line_end.begin);
        }
      }
    }

    -- TODO: the cast is redundant
    underlined_element : base_element.new(text_library.U, underlined !> base_string);
    var text_element highlighted_element;
    if (highlight_prefix.is_empty && highlight_suffix.is_empty) {
      highlighted_element = underlined_element;
    } else {
      -- TODO: the casts are redundant
      highlighted : text_utilities.join(highlight_prefix !> base_string,
          underlined_element, highlight_suffix !> base_string);
      highlighted_element = base_element.new(text_library.U2,
          list_dictionary[attribute_id, attribute_fragment].new(), highlighted);
    }
    -- TODO: the casts are redundant
    text_line : text_utilities.join(prefix !> base_string, highlighted_element, suffix !> base_string);

    return base_element.new(text_library.DIV,
          list_dictionary[attribute_id, attribute_fragment].new(), text_line);
  }
}
