-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_resolver {

  class test_store {
    extends base_resource_store;

    public test_store(string path_prefix, boolean is_current) {
      super(path_prefix, is_current, is_current);
    }

    override boolean exists(immutable list[string] path) {
      return false;
    }

    override string read_string(immutable list[string] path) {
      return "test";
    }

    override void write_string(immutable list[string] path, string new_value) {
    }

    override void make_catalog(immutable list[string] path) {
    }
  }

  static CURRENT : test_store.new(resource_util.CURRENT_CATALOG, true).top();
  static ROOT : test_store.new(resource_util.ROOT_CATALOG, false).top();

  test_case test_file_catalogs() {
    assert CURRENT.get_id().to_string == ".";
    assert ROOT.get_id().to_string == "/";
  }

  test_case test_simple_resolve() {
    foo : "foo";

    assert CURRENT.resolve(foo).to_string == "foo";
    assert ROOT.resolve(foo).to_string == "/foo";
  }

  test_case test_resolve_extension() {
    bar : "bar";
    html : base_extension.HTML;

    assert CURRENT.resolve(bar, html).to_string == "bar.html";
    assert ROOT.resolve(bar, html).to_string == "/bar.html";
  }

  test_case test_sub_catalogs() {
    cat : CURRENT;
    dog : cat.resolve("dog");

    assert dog.to_string == "dog";

    cat2 : dog.access_catalog();
    assert cat2.get_id().to_string == "dog";

    dog2 : cat2.resolve("Yoshka");
    assert dog2.to_string == "dog/Yoshka";
  }

  test_case test_root_subdirs() {
    cat : ROOT;
    dog : cat.resolve("dog");

    assert dog.to_string == "/dog";

    cat2 : dog.access_catalog();
    assert cat2.get_id().to_string == "/dog";

    dog2 : cat2.resolve("Yoshka");
    assert dog2.to_string == "/dog/Yoshka";
  }

  test_case test_multiple_subdirs() {
    var foo : ROOT;
    var bar : foo.resolve("bar");

    assert bar.to_string == "/bar";

    foo = bar.access_catalog();
    assert foo.get_id().to_string == "/bar";

    bar = foo.resolve("baz");
    assert bar.to_string == "/bar/baz";

    foo = bar.access_catalog();
    bar = foo.resolve("quux");
    assert bar.to_string == "/bar/baz/quux";

    foo = bar.access_catalog();
    bar = foo.resolve("");
    assert bar.to_string == "/bar/baz/quux";

    bar = foo.resolve("./././.");
    assert bar.to_string == "/bar/baz/quux";
  }

  test_case test_more_subdir_ops() {
    var foo : CURRENT;
    var bar : foo.resolve("foo/bar/././baz");

    assert "foo/bar/baz" == bar.to_string;

    foo = bar.access_catalog();
    bar = foo.resolve("..");
    assert "foo/bar" == bar.to_string;

    foo = bar.access_catalog();
    bar = foo.resolve("../..");
    assert "." == bar.to_string;

    foo = bar.access_catalog();
    bar = foo.resolve("..");
    assert "." == bar.to_string;
  }

  test_case test_parent_catalog() {
    foo : CURRENT;
    var bar : foo.resolve("foo/bar/././baz");

    assert "foo/bar/baz" == bar.to_string;

    bar = bar.parent();
    assert "foo/bar" == bar.to_string;

    bar = bar.parent();
    assert "foo" == bar.to_string;

    bar = bar.parent();
    assert "." == bar.to_string;

    bar = bar.parent();
    assert "." == bar.to_string;
  }
}
