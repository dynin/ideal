-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_graph {

  testcase basic_test() {
    the_graph : base_graph[string, string].new();

    assert the_graph.adjacent("foo").is_empty;

    the_graph.add_edge("foo", "bar", "edge");

    assert the_graph.adjacent("bar").is_empty;

    foo : the_graph.adjacent("foo");
    assert !foo.is_empty;
    assert foo.size == 1;
    -- TODO: we shouldn't need to introduce a variable here.
    foo_elements : foo.elements;
    assert foo_elements.first == "bar";
  }

  testcase cycle_test() {
    the_graph : base_graph[string, string].new();

    the_graph.add_edge("A", "B", "A-B");
    the_graph.add_edge("A", "C", "A-C");
    the_graph.add_edge("C", "D", "C-D");

    assert the_graph.adjacent("A").size == 2;
    assert the_graph.adjacent("B").size == 0;
    assert the_graph.adjacent("C").size == 1;
    assert the_graph.adjacent("D").size == 0;

    assert the_graph.introduces_cycle("A", "A");
    assert !the_graph.introduces_cycle("A", "B");
    assert the_graph.introduces_cycle("B", "A");
    assert !the_graph.introduces_cycle("B", "C");
    assert the_graph.introduces_cycle("C", "A");
    assert the_graph.introduces_cycle("D", "A");
    assert the_graph.introduces_cycle("D", "C");
    assert !the_graph.introduces_cycle("D", "B");
  }
}
