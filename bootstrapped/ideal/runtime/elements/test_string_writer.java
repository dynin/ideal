// Autogenerated from runtime/elements/test_string_writer.i

package ideal.runtime.elements;

import ideal.library.elements.*;
import ideal.machine.channels.string_writer;

public class test_string_writer implements value {
  public void basic_test() {
    final string_writer the_writer = new string_writer();
    assert ideal.machine.elements.runtime_util.values_equal(the_writer.size(), 0);
    assert ideal.machine.elements.runtime_util.values_equal(the_writer.elements(), new base_string(""));
    the_writer.write_all(new base_string("foo"));
    assert ideal.machine.elements.runtime_util.values_equal(the_writer.size(), 3);
    assert ideal.machine.elements.runtime_util.values_equal(the_writer.elements(), new base_string("foo"));
    the_writer.write('b');
    the_writer.write('a');
    the_writer.write('r');
    assert ideal.machine.elements.runtime_util.values_equal(the_writer.size(), 6);
    assert ideal.machine.elements.runtime_util.values_equal(the_writer.elements(), new base_string("foobar"));
    final string elements = the_writer.elements();
    the_writer.clear();
    assert ideal.machine.elements.runtime_util.values_equal(elements, new base_string("foobar"));
    assert ideal.machine.elements.runtime_util.values_equal(the_writer.size(), 0);
    assert ideal.machine.elements.runtime_util.values_equal(the_writer.elements(), new base_string(""));
  }
  public test_string_writer() { }
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test(new base_string("test_string_writer.basic_test"));
    this.basic_test();
    ideal.machine.elements.runtime_util.end_test();
  }
}
