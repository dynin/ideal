-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.library.texts;
implicit import ideal.development.comments;
implicit import ideal.development.documenters;
import ideal.development.comments.documentation;

construct_data class comment_construct {
  implements annotation_construct, documentation;

  comment the_comment;
  private var text_fragment or null the_text_fragment;

  override text_fragment or null section(documentation_section the_section) {
    if (the_text_fragment is null) {
      the_text_fragment = doc_comment_processor.parse(the_comment.content);
    }

    -- TODO: revert to switch
    if (the_section == documentation_section.ALL) {
      return the_text_fragment;
    } else if (the_section == documentation_section.SUMMARY) {
      text : the_text_fragment;
      assert text is_not null;
      -- TODO: cast should be redundant.
      return summary_extractor.get_summary(text) !> base_string;
    } else {
      utilities.panic("Unknown section: " ++ the_section);
    }
  }
}
