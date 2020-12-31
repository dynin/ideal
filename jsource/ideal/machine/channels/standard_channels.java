/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.machine.channels;

import ideal.library.channels.*;

// TODO: this should be a service.
public class standard_channels {
  public static final output<Character> stdout = new writer_adapter(System.out);
}
