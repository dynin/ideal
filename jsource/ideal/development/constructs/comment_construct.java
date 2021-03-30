/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.comments.*;
import ideal.development.documenters.*;

import javax.annotation.Nullable;

public class comment_construct extends base_construct
    implements annotation_construct, documentation {

  public final comment the_comment;
  private @Nullable text_fragment the_text;

  public comment_construct(comment the_comment, @Nullable text_fragment the_text, origin source) {
    super(source);
    assert the_comment != null;
    this.the_comment = the_comment;
    this.the_text = the_text;
  }

  @Override
  public @Nullable text_fragment section(documentation_section the_section) {
    if (the_text == null) {
      the_text = doc_comment_processor.parse(the_comment.content);
    }

    switch (the_section) {
      case ALL:
        return the_text;
      case SUMMARY:
        return (base_string) summary_extractor.get_summary(the_text);
      default:
        utilities.panic("Unknown section: " + the_section);
        return null;
    }
  }

  @Override
  public readonly_list<construct> children() {
    return new empty<construct>();
  }
}
