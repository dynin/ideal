-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.library.texts;
implicit import ideal.development.components;

meta_construct abstract class extension_construct {
  abstract readonly list[construct] children();
  abstract analyzable to_analyzable();
  abstract text_fragment print(printer p);
  abstract boolean is_terminated();
  abstract construct transform(transformer t);
}
