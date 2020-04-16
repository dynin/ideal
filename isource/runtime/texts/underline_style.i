-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Describes underline style used in plain text rendering.
class underline_style {
  extends debuggable;
  implements deeply_immutable data;

  -- TODO: make readonly/immutable?
  public static var dictionary[element_id, underline_style] all_styles;

  public element_id style_id;
  public character display_character;

  private underline_style(element_id style_id, character display_character) {
    this.style_id = style_id;
    this.display_character = display_character;
  }

  private static void register(underline_style style) {
    all_styles.put(style.style_id, style);
  }

  override string to_string() {
    return style_id ++ " -> " ++ display_character;
  }

  private static character CARET : '^';
  private static character DASH : '-';

  static {
    all_styles = list_dictionary[element_id, underline_style].new();
    register(underline_style.new(text_library.UNDERLINE, CARET));
    register(underline_style.new(text_library.UNDERLINE2, DASH));
  }
}
