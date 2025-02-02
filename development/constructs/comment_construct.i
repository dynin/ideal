-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.library.texts;
implicit import ideal.development.comments;
implicit import ideal.development.documenters;
import ideal.development.comments.documentation;

meta_construct class comment_construct {
  implements annotation_construct, documentation;

  comment the_comment;
  private var text_fragment or null the_text_fragment;

  override text_fragment or null section(documentation_section the_section) {
    if (the_text_fragment is null) {
      the_text_fragment = doc_comment_processor.parse(the_comment.content);
    }

    switch (the_section) {
      case ALL:
        return the_text_fragment;
      case SUMMARY:
        text : the_text_fragment;
        assert text is_not null;
        return summary_extractor.get_summary(text);
      default:
        utilities.panic("Unknown section: " ++ the_section);
    }
  }
}
