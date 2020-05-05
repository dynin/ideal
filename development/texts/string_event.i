-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A |text_event| which is a plain |string|.
class string_event {
  implements text_event;

  final string payload;

  string_event(string payload) {
    this.payload = payload;
  }

  override string to_string() => payload;
}
