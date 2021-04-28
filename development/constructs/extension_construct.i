-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.library.texts;
implicit import ideal.development.components;

construct_data abstract class extension_construct {
  abstract readonly list[construct] children();
  abstract analyzable to_analyzable();
  abstract text_fragment print(printer p);
  abstract boolean is_terminated();
  abstract construct transform(transformer t);
}
