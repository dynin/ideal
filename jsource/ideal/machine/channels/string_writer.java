/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.machine.channels;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.runtime.elements.*;

public class string_writer implements output<Character> {
  private final StringBuilder builder;

  public string_writer() {
    this.builder = new StringBuilder();
  }

  public void write(Character element) {
    builder.append(element);
  }

  public void write_all(readonly_list<Character> elements) {
    if (elements instanceof string) {
      builder.append(utilities.s((string) elements));
    } else {
      for (int i = 0; i < elements.size(); ++i) {
        builder.append(elements.get(i));
      }
    }
  }

  public void sync() {
  }

  public void close() {
  }

  public void teardown() {
  }

  public int size() {
    return builder.length();
  }

  public string elements() {
    return new base_string(builder.toString());
  }

  public void clear() {
    builder.setLength(0);
  }
}
