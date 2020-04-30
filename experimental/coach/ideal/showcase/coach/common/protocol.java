/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.common;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;
import ideal.showcase.coach.reflections.*;
public interface protocol {
  name REQUEST_TYPE = new name("request_type");
  name PAYLOAD = new name("payload");
  name SOURCE = new name("source");

  string INIT = new base_string("INIT");
  string PING = new base_string("PING");
  string PULL = new base_string("PULL");
  string PUSH = new base_string("PUSH");

  string OK_RESPONSE = new base_string("Ok");
  string ERROR_RESPONSE = new base_string("Error");
}
