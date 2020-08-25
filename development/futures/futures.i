-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

package futures {
  implicit import ideal.library.elements;
  implicit import ideal.runtime.elements;

  interface disposable;
  interface lifespan;
  class resource_manager;
  class base_lifespan;
  interface future;
  class base_future;
}
