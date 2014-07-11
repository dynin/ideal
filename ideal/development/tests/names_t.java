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
import ideal.machine.elements.runtime_util;
import ideal.development.elements.*;
import ideal.development.names.*;

import junit.framework.TestCase;

public class names_t extends TestCase {

  private static String to_s(convertible_to_string val) {
    return utilities.s(val.to_string());
  }

  public void test_simple_names() {
    simple_name foo = simple_name.make(new base_string("foo"));
    simple_name bar = simple_name.make(new base_string("bar"));
    simple_name foo2 = simple_name.make(new base_string("foo"));

    assertEquals(to_s(foo), "foo");
    assertEquals(to_s(bar), "bar");

    assertFalse(utilities.eq(foo, bar));
    assertTrue(utilities.eq(foo, foo2));
  }

  public void test_special_names() {
    special_name foo = new special_name("foo");
    special_name bar = new special_name("bar");
    special_name foo2 = new special_name("foo");

    assertEquals(to_s(foo), "<foo>");
    assertEquals(to_s(bar), "<bar>");

    assertFalse(utilities.eq(foo, bar));
    assertFalse(utilities.eq(foo, foo2));
  }

  public void test_segmented_names() {
    simple_name foo = simple_name.make(new base_string("foo"));
    simple_name bar = simple_name.make(new base_string("bar"));

    simple_name name = name_utilities.join(foo, bar);
    assertEquals(to_s(name), "foo_bar");

    simple_name name2 = name_utilities.join(name, foo);
    assertEquals(to_s(name2), "foo_bar_foo");
  }

  public void test_simple_names_equality() {
    simple_name foo = simple_name.make(new base_string("foo_bar"));
    simple_name bar = simple_name.make(new base_string("foo_bar"));

    assertTrue(foo == bar);
  }

  public void test_camel_case() {
    simple_name the_name = name_utilities.parse_camel_case("thisIsCamelCase");
    assertEquals(to_s(the_name), "this_is_camel_case");
  }
}
