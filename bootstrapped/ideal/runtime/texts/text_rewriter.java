// Autogenerated from runtime/texts/text_rewriter.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;

import javax.annotation.Nullable;

public abstract class text_rewriter extends text_visitor<text_fragment> {
  public text_fragment rewrite(final text_fragment fragment) {
    return this.process(fragment);
  }
  protected abstract text_fragment rewrite_element(element_id id, immutable_dictionary<attribute_id, attribute_fragment> attributes, @Nullable text_fragment children);
  protected @Override text_fragment process_string(final string s) {
    return (base_string) s;
  }
  protected @Override text_fragment process_element(final text_element element) {
    @Nullable text_fragment children = element.children();
    if (children != null) {
      children = this.rewrite(children);
    }
    return this.rewrite_element(element.get_id(), element.attributes(), children);
  }
  protected @Override text_fragment process_special(final special_text t) {
    return t;
  }
  protected @Override text_fragment process_nodes(final list_text_node nodes) {
    final base_list<text_fragment> result = new base_list<text_fragment>();
    {
      final readonly_list<text_node> source_list = nodes.nodes();
      for (Integer source_index = 0; source_index < source_list.size(); source_index += 1) {
        final text_node source = source_list.get(source_index);
        result.append(this.process(source));
      }
    }
    return text_utilities.join(result);
  }
  protected @Override text_fragment process_attributes(final list_attribute_fragment fragments) {
    final base_list<attribute_fragment> result = new base_list<attribute_fragment>();
    {
      final readonly_list<attribute_fragment> source_list = fragments.fragments();
      for (Integer source_index = 0; source_index < source_list.size(); source_index += 1) {
        final attribute_fragment source = source_list.get(source_index);
        result.append((attribute_fragment) this.process(source));
      }
    }
    return new base_list_attribute_fragment(result);
  }
  public text_rewriter() { }
}
