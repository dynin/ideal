-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implementation of logging-related types.
namespace logs {
  implicit import ideal.library.elements;
  implicit import ideal.library.texts;
  implicit import ideal.library.messages;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.texts;

  class simple_message;
  namespace log;

  interface displayable;

  class test_display;
}
