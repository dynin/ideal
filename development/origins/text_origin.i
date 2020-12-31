-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- References a fragment of text in a |source_content|.
class text_origin {
  extends debuggable;
  implements deeply_immutable data, mutable origin, stringable;

  dont_display source_content source;
  dont_display nonnegative begin;
  dont_display nonnegative end;

  text_origin(source_content source, nonnegative begin, nonnegative end) {
    this.source = source;
    this.begin = begin;
    this.end = end;
    assert begin >= 0 && end >= begin && source.content.size >= end;
  }

  implement origin deeper_origin => source;

  string image => source.content.slice(begin, end);

  implement string to_string => "(" ++ begin ++ "-" ++ end ++ ")";
}
