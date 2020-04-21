/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.machine.resources;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.channels.*;

import java.io.IOException;
import java.io.Reader;

public class io_util {

  private static final int BUFFER_SIZE = 10240;

  public static void io_error(IOException exception) {
    utilities.panic("I/O error: " + exception.getMessage());
  }

  public static base_string read(Reader input) throws IOException {
    StringBuilder result = new StringBuilder();

    while (true) {
      char data[] = new char[BUFFER_SIZE];
      int len = input.read(data);
      if (len <= 0) {
        break;
      }
      result.append(new String(data, 0, len));
    }
    input.close();

    return new base_string(result.toString());
  }

  private io_util() { }
}
