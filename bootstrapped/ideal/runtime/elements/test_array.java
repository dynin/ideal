// Autogenerated from isource/runtime/elements/test_array.i

package ideal.runtime.elements;

import ideal.library.elements.*;
import ideal.machine.elements.array;

public class test_array {
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test("test_array.test_creation");
    test_creation();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_array.test_access");
    test_access();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_array.test_move");
    test_move();
    ideal.machine.elements.runtime_util.end_test();
  }
  public void test_creation() {
    final array<string> the_array = new array<string>(10);
    assert the_array.size == 10;
  }
  public void test_access() {
    final array<string> the_array = new array<string>(10);
    the_array.set(5, new base_string("foo"));
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("foo"), the_array.at(5).get());
  }
  public void test_move() {
    final array<string> the_array = new array<string>(3);
    the_array.set(0, new base_string("foo"));
    the_array.set(1, new base_string("bar"));
    the_array.set(2, new base_string("baz"));
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("foo"), the_array.at(0).get());
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("bar"), the_array.at(1).get());
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("baz"), the_array.at(2).get());
    the_array.move(0, 1, 2);
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("foo"), the_array.at(0).get());
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("foo"), the_array.at(1).get());
    assert ideal.machine.elements.runtime_util.values_equal(new base_string("bar"), the_array.at(2).get());
  }
}