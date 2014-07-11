// Autogenerated from isource/runtime/texts/test_elements.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;

public class test_elements {
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test("test_elements.test_namespace_id");
    test_namespace_id();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_elements.test_element_id");
    test_element_id();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_elements.test_base_element");
    test_base_element();
    ideal.machine.elements.runtime_util.end_test();
  }
  public void test_namespace_id() {
    assert ideal.machine.elements.runtime_util.values_equal(text_library.HTML_NS.short_name(), new base_string("html"));
    assert ideal.machine.elements.runtime_util.values_equal(text_library.HTML_NS.to_string(), new base_string("html"));
  }
  public void test_element_id() {
    assert ideal.machine.elements.runtime_util.values_equal(text_library.P.short_name(), new base_string("p"));
    assert text_library.P.get_namespace() == text_library.HTML_NS;
    assert ideal.machine.elements.runtime_util.values_equal(text_library.P.to_string(), new base_string("html:p"));
    assert ideal.machine.elements.runtime_util.values_equal(text_library.DIV.short_name(), new base_string("div"));
    assert text_library.DIV.get_namespace() == text_library.HTML_NS;
    assert ideal.machine.elements.runtime_util.values_equal(text_library.DIV.to_string(), new base_string("html:div"));
  }
  public void test_base_element() {
    final text_element element = new base_element(text_library.P);
    assert element.get_id() == text_library.P;
    assert element.children() != null;
    assert element.children().is_empty();
  }
}
