// Autogenerated from runtime/formats/test_json_printer.i

package ideal.runtime.formats;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.formats.*;
import ideal.runtime.elements.*;
import ideal.runtime.characters.*;
import ideal.machine.characters.unicode_handler;

public class test_json_printer implements value {
  private json_printer make_printer() {
    return new json_printer(unicode_handler.instance);
  }
  public void test_basic_printer() {
    final json_printer printer = this.make_printer();
    final string json0 = printer.print(new base_string("\'Hello, world!\'\n\\"));
    assert ideal.machine.elements.runtime_util.values_equal(json0, new base_string("\"\'Hello, world!\'\\n\\\\\""));
    final string json1 = printer.print(68);
    assert ideal.machine.elements.runtime_util.values_equal(json1, new base_string("68"));
    final json_array array = new json_array_impl();
    array.append_all((readonly_list<Object>) (Object) ((readonly_list<Object>) (Object) new base_immutable_list<Integer>(new ideal.machine.elements.array<Integer>(new Integer[]{ 42, 68 }))));
    final string json2 = printer.print(array);
    assert ideal.machine.elements.runtime_util.values_equal(json2, new base_string("[42, 68]"));
    final string json3 = printer.print(null);
    assert ideal.machine.elements.runtime_util.values_equal(json3, new base_string("null"));
    final json_object the_object = new json_object_list();
    the_object.put(new base_string("foo"), new base_string("bar"));
    the_object.put(new base_string("baz"), 68);
    final string json4 = printer.print(the_object);
    assert ideal.machine.elements.runtime_util.values_equal(json4, new base_string("{\"foo\": \"bar\", \"baz\": 68}"));
    final json_object the_object2 = new json_object_list();
    the_object2.put(new base_string("foo"), false);
    the_object2.put(new base_string("bar"), true);
    final string json5 = printer.print(the_object2);
    assert ideal.machine.elements.runtime_util.values_equal(json5, new base_string("{\"foo\": false, \"bar\": true}"));
  }
  public test_json_printer() { }
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test(new base_string("test_json_printer.test_basic_printer"));
    this.test_basic_printer();
    ideal.machine.elements.runtime_util.end_test();
  }
}
