/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.machine.channels;

import ideal.library.channels.*;

// TODO: this should be a service.
public class standard_channels {
  public static final output<Character> stdout = new writer_adapter(System.out);
}
