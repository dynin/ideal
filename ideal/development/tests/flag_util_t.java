/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tests;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.tools.*;

import junit.framework.TestCase;

public class flag_util_t extends TestCase {

  private static class options {
    public boolean ARG_BOOL;
    public string ARG_STRING;
  }

  public void test_flag_parse() {
    options the_options = new options();
    flag_util.parse_flags(new String[] { "-arg-bool=true", "-arg-string=str" }, the_options);
    assertEquals(the_options.ARG_BOOL, true);
    assertEquals(the_options.ARG_STRING, new base_string("str"));

    the_options = new options();
    flag_util.parse_flags(new String[] { "-noargbool", "-arg-string:bar" }, the_options);
    assertEquals(the_options.ARG_BOOL, false);
    assertEquals(the_options.ARG_STRING, new base_string("bar"));
  }

  public void test_unknown_flag() {
    try {
      flag_util.parse_flags(new String[] { "-whatever" }, new options());
    } catch (Exception e) {
      assertEquals(e.getClass(), RuntimeException.class);
    }
  }
}
