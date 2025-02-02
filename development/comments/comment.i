-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- This can be either a comment or whitespace.
class comment {
  extends debuggable;
  implements deeply_immutable data;

  final comment_type type;

  --- The content of comment excludes the comment delimeters.
  final string content;

  --- The image of content includes the delimeters.
  final string image;

  comment(comment_type type, string content, string image) {
    assert type is_not null;
    assert content is_not null;
    assert image is_not null;
    this.type = type;
    this.content = content;
    this.image = image;
  }
}
