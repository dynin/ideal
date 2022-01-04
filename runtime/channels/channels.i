-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Implementation of input/output channels.
namespace channels {
  implicit import ideal.library.elements;
  implicit import ideal.library.channels;
  implicit import ideal.runtime.elements;

  class appender;
  class output_counter;
  class output_transformer;
  class auto_sync_output;

  test_suite test_output_transformer;
}
