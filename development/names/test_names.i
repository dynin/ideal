-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_names {
  testcase test_simple_names() {
    foo : simple_name.make("foo");
    bar : simple_name.make("bar");
    foo2 : simple_name.make("foo");

    -- TODO: use foo.to_string, here and below
    assert foo.to_string() == "foo";
    assert bar.to_string() == "bar";

    assert foo != bar;
    assert foo == foo2;
  }

  testcase test_special_names() {
    foo : special_name.new("foo");
    bar : special_name.new("bar");
    foo2 : special_name.new("foo");

    assert foo.to_string() == "<foo>";
    assert bar.to_string() == "<bar>";

    assert foo != bar;
    assert foo != foo2;
  }

  testcase test_segmented_names() {
    foo : simple_name.make("foo");
    bar : simple_name.make("bar");

    name : name_utilities.join(foo, bar);
    assert name.to_string() == "foo_bar";

    name2 : name_utilities.join(name, foo);
    assert name2.to_string() == "foo_bar_foo";
  }

  testcase test_simple_names_equality() {
    foo : simple_name.make("foo_bar");
    bar : simple_name.make("foo_bar");

    assert foo == bar;
  }

  testcase test_camel_case() {
    the_name : name_utilities.parse_camel_case("thisIsCamelCase");
    assert the_name.to_string() == "this_is_camel_case";

    the_name2 : name_utilities.parse_camel_case("ThatIsCamelCase");
    assert the_name2.to_string() == "that_is_camel_case";
  }
}
