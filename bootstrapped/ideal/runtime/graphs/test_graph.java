// Autogenerated from runtime/graphs/test_graph.i

package ideal.runtime.graphs;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import ideal.runtime.elements.*;

public class test_graph {
  public void basic_test() {
    final base_graph<string, string> the_graph = new base_graph<string, string>();
    assert the_graph.adjacent(new base_string("foo")).is_empty();
    the_graph.add_edge(new base_string("foo"), new base_string("bar"), new base_string("edge"));
    assert the_graph.adjacent(new base_string("bar")).is_empty();
    final immutable_set<string> foo = the_graph.adjacent(new base_string("foo"));
    assert !foo.is_empty();
    assert foo.is_not_empty();
    assert ideal.machine.elements.runtime_util.values_equal(foo.size(), 1);
    final immutable_list<string> foo_elements = foo.elements();
    assert ideal.machine.elements.runtime_util.values_equal(foo_elements.first(), new base_string("bar"));
  }
  public void cycle_test() {
    final base_graph<string, string> the_graph = new base_graph<string, string>();
    the_graph.add_edge(new base_string("A"), new base_string("B"), new base_string("A-B"));
    the_graph.add_edge(new base_string("A"), new base_string("C"), new base_string("A-C"));
    the_graph.add_edge(new base_string("C"), new base_string("D"), new base_string("C-D"));
    assert ideal.machine.elements.runtime_util.values_equal(the_graph.adjacent(new base_string("A")).size(), 2);
    assert ideal.machine.elements.runtime_util.values_equal(the_graph.adjacent(new base_string("B")).size(), 0);
    assert ideal.machine.elements.runtime_util.values_equal(the_graph.adjacent(new base_string("C")).size(), 1);
    assert ideal.machine.elements.runtime_util.values_equal(the_graph.adjacent(new base_string("D")).size(), 0);
    assert the_graph.introduces_cycle(new base_string("A"), new base_string("A"));
    assert !the_graph.introduces_cycle(new base_string("A"), new base_string("B"));
    assert the_graph.introduces_cycle(new base_string("B"), new base_string("A"));
    assert !the_graph.introduces_cycle(new base_string("B"), new base_string("C"));
    assert the_graph.introduces_cycle(new base_string("C"), new base_string("A"));
    assert the_graph.introduces_cycle(new base_string("D"), new base_string("A"));
    assert the_graph.introduces_cycle(new base_string("D"), new base_string("C"));
    assert !the_graph.introduces_cycle(new base_string("D"), new base_string("B"));
  }
  public test_graph() { }
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test(new base_string("test_graph.basic_test"));
    this.basic_test();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test(new base_string("test_graph.cycle_test"));
    this.cycle_test();
    ideal.machine.elements.runtime_util.end_test();
  }
}
