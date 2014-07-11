/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.comments;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;

// This can be either a comment or whitespace.
public class comment extends debuggable implements deeply_immutable_data {
  public final comment_type type;

  // The content of comment excludes the comment delimeters
  public final string content;

  // The image of content includes the delimeters
  public final string image;

  public comment(comment_type type, string content, string image) {
    assert type != null;
    assert content != null;
    assert image != null;
    this.type = type;
    this.content = content;
    this.image = image;
  }
}
