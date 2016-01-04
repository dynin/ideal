/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.machine.channels;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.runtime.elements.*;

import java.io.*;

public class writer_adapter implements output<Character> {
  protected Writer out;

  public writer_adapter(Writer out) {
    this.out = out;
  }

  public writer_adapter(OutputStream out) {
    this(new OutputStreamWriter(out));
  }

  public void write(Character element) {
    try {
      out.write(element);
    } catch (IOException e) {
    }
  }

  public void write_all(readonly_list<Character> elements) {
    String s;

    if (elements instanceof string) {
      s = utilities.s((string) elements);
    } else {
      StringBuilder builder = new StringBuilder();
      for (int i = 0; i < elements.size(); ++i) {
        builder.append(elements.get(i));
      }
      s = builder.toString();
    }

    try {
      out.write(s);
    } catch (IOException e) {
    }
  }

  public void sync() {
    try {
      out.flush();
    } catch (IOException e) {
    }
  }

  public void close() {
    try {
      out.close();
    } catch (IOException e) {
    }
  }
}
