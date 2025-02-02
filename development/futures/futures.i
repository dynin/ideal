-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

package futures {
  implicit import ideal.library.elements;
  implicit import ideal.runtime.elements;

  interface disposable;
  interface lifespan;
  class resource_manager;
  class base_lifespan;
  interface operation;
  class base_operation;
  interface future;
  class base_future;
  namespace event_queue;

  test_suite test_futures;
}
