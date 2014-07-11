// Autogenerated from isource/runtime/texts/text_visitor.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;

public abstract class text_visitor<result_type> {
  public result_type process(final text_fragment fragment) {
    if (fragment instanceof string) {
      return process_string(((string) fragment));
    } else if (fragment instanceof text_element) {
      return process_element(((text_element) fragment));
    } else if (fragment instanceof list_text_node) {
      return process_nodes(((list_text_node) fragment));
    } else if (fragment instanceof special_text) {
      return process_special(((special_text) fragment));
    } else {
      utilities.panic(ideal.machine.elements.runtime_util.concatenate(new base_string("Unknown fragment: "), fragment));
      return null;
    }
  }
  protected abstract result_type process_string(string s);
  protected abstract result_type process_element(text_element element);
  protected abstract result_type process_nodes(list_text_node nodes);
  protected abstract result_type process_special(special_text t);
}