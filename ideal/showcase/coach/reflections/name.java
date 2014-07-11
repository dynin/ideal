/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.reflections;

import ideal.library.elements.*;
import ideal.runtime.elements.*;

/**
 * Simple identifier for use in fluid_servlet.
 */
public class name implements identifier {
  private final String s;

  public name(String s) {
    this.s = s.intern();
  }

  public name(string s) {
    this(utilities.s(s));
  }

  public String s() {
    return s;
  }

  @Override
  public String toString() {
    return s;
  }

  @Override
  public string to_string() {
    return new base_string(s);
  }

  @Override
  public int hashCode() {
    return s.hashCode() + 68;
  }

  @Override
  public boolean equals(Object other) {
    return other != null && other.getClass() == this.getClass() && ((name) other).s == this.s;
  }
}
