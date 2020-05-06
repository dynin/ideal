// Autogenerated from development/comments/summary_extractor.i

package ideal.development.comments;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.runtime.patterns.singleton_pattern;
import ideal.machine.channels.string_writer;

import javax.annotation.Nullable;

public class summary_extractor extends text_visitor<string> {
  public static string get_summary(final text_fragment the_text) {
    return instance.process(the_text);
  }
  public static final summary_extractor instance = new summary_extractor();
  private static final char dot = '.';
  private static final singleton_pattern<Character> dot_pattern = new singleton_pattern<Character>(dot);
  private summary_extractor() { }
  protected @Override string process_string(final string the_string) {
    final @Nullable range range = dot_pattern.find_in(the_string, 0);
    if (range == null) {
      return the_string;
    } else {
      return the_string.slice(0, range.end());
    }
  }
  private string process_list(final immutable_list<text_node> nodes) {
    final string_writer result = new string_writer();
    for (int i = 0; i < nodes.size(); i += 1) {
      final string s = process(nodes.get(i));
      result.write_all(s);
      if (!s.is_empty() && s.last() == dot) {
        break;
      }
    }
    return result.extract_elements();
  }
  protected @Override string process_element(final text_element element) {
    final @Nullable text_fragment children = element.children();
    if (children != null) {
      return process(children);
    } else {
      return new base_string("");
    }
  }
  protected @Override string process_nodes(final list_text_node nodes_list) {
    return process_list(nodes_list.nodes());
  }
  protected @Override string process_special(final special_text t) {
    return t.to_plain_text();
  }
}
