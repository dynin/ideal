// Autogenerated from runtime/texts/text_utilities.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;
import ideal.machine.channels.string_writer;

import javax.annotation.Nullable;

public class text_utilities {
  public static final text_fragment EMPTY_FRAGMENT = new base_list_text_node(new empty<text_node>());
  public static boolean is_indent(final text_element element) {
    return element.get_id() == text_library.INDENT;
  }
  public static boolean is_block(final text_element element) {
    final element_id id = element.get_id();
    return text_utilities.is_indent(element) || id == text_library.HTML || id == text_library.HEAD || id == text_library.BODY || id == text_library.TITLE || id == text_library.META || id == text_library.LINK || id == text_library.P || id == text_library.DIV || id == text_library.H1 || id == text_library.H2 || id == text_library.TABLE || id == text_library.TR || id == text_library.TH || id == text_library.TD || id == text_library.BR;
  }
  public static immutable_list<text_node> make_singleton(final text_node element) {
    final base_list<text_node> the_list = new base_list<text_node>();
    the_list.append(element);
    return the_list.frozen_copy();
  }
  public static immutable_list<text_node> to_list(final @Nullable text_fragment fragment) {
    if (fragment == null) {
      return new empty<text_node>();
    } else if (fragment instanceof text_node) {
      return text_utilities.make_singleton(((text_node) fragment));
    } else if (fragment instanceof list_text_node) {
      return ((list_text_node) fragment).nodes();
    } else {
      {
        utilities.panic(ideal.machine.elements.runtime_util.concatenate(new base_string("Unknown type "), fragment));
        return null;
      }
    }
  }
  public static text_fragment join(final readonly_list<text_fragment> fragments) {
    if (fragments.size() <= 1) {
      if (fragments.is_empty()) {
        return text_utilities.EMPTY_FRAGMENT;
      } else {
        return fragments.get(0);
      }
    }
    final base_list<text_node> nodes = new base_list<text_node>();
    {
      final readonly_list<text_fragment> fragment_list = fragments;
      for (Integer fragment_index = 0; fragment_index < fragment_list.size(); fragment_index += 1) {
        final text_fragment fragment = fragment_list.get(fragment_index);
        text_utilities.append(nodes, fragment);
      }
    }
    return text_utilities.to_fragment(nodes);
  }
  public static text_fragment join(final text_fragment first, final text_fragment second) {
    final base_list<text_node> nodes = new base_list<text_node>();
    text_utilities.append(nodes, first);
    text_utilities.append(nodes, second);
    return text_utilities.to_fragment(nodes);
  }
  public static text_fragment join(final text_fragment first, final text_fragment second, final text_fragment third) {
    final base_list<text_node> nodes = new base_list<text_node>();
    text_utilities.append(nodes, first);
    text_utilities.append(nodes, second);
    text_utilities.append(nodes, third);
    return text_utilities.to_fragment(nodes);
  }
  public static text_fragment join(final text_fragment first, final text_fragment second, final text_fragment third, final text_fragment fourth) {
    final base_list<text_node> nodes = new base_list<text_node>();
    text_utilities.append(nodes, first);
    text_utilities.append(nodes, second);
    text_utilities.append(nodes, third);
    text_utilities.append(nodes, fourth);
    return text_utilities.to_fragment(nodes);
  }
  public static attribute_fragment join_attributes(final readonly_list<attribute_fragment> fragments) {
    if (ideal.machine.elements.runtime_util.values_equal(fragments.size(), 1)) {
      return fragments.first();
    }
    final base_list<attribute_fragment> result_fragments = new base_list<attribute_fragment>();
    {
      final readonly_list<attribute_fragment> fragment_list = fragments;
      for (Integer fragment_index = 0; fragment_index < fragment_list.size(); fragment_index += 1) {
        final attribute_fragment fragment = fragment_list.get(fragment_index);
        result_fragments.append(fragment);
      }
    }
    return new base_list_attribute_fragment(result_fragments);
  }
  private static void append(final list<text_node> nodes, final text_fragment fragment) {
    if (fragment instanceof string) {
      if (((string) fragment).is_empty()) {
        return;
      }
    }
    if (fragment instanceof text_node) {
      nodes.append(((text_node) fragment));
    } else {
      nodes.append_all(((list_text_node) fragment).nodes());
    }
  }
  private static text_fragment to_fragment(final readonly_list<text_node> nodes) {
    if (nodes.is_empty()) {
      return text_utilities.EMPTY_FRAGMENT;
    } else if (ideal.machine.elements.runtime_util.values_equal(nodes.size(), 1)) {
      return nodes.first();
    } else {
      return new base_list_text_node(nodes);
    }
  }
  public static string to_plain_text(final text_fragment text) {
    final string_writer the_writer = new string_writer();
    final plain_formatter formatter = new plain_formatter(the_writer);
    formatter.write(text);
    return the_writer.elements();
  }
  public static string to_markup_string(final text_fragment text) {
    final string_writer the_writer = new string_writer();
    final markup_formatter formatter = new markup_formatter(the_writer);
    formatter.write(text);
    return the_writer.elements();
  }
  public static text_element make_element(final element_id id, final readonly_list<text_node> children) {
    @Nullable text_fragment child_fragment;
    if (children != null) {
      child_fragment = new base_list_text_node(children);
    } else {
      child_fragment = null;
    }
    return new base_element(id, new list_dictionary<attribute_id, attribute_fragment>(), child_fragment);
  }
  public static text_element make_html_link(final text_fragment text, final string link_target) {
    return new base_element(text_library.A, text_library.HREF, (base_string) link_target, text);
  }
  public static text_element make_css_link(final string css_href) {
    final list_dictionary<attribute_id, attribute_fragment> attributes = new list_dictionary<attribute_id, attribute_fragment>();
    attributes.put(text_library.HREF, (base_string) css_href);
    attributes.put(text_library.REL, (base_string) new base_string("stylesheet"));
    attributes.put(text_library.TYPE, (base_string) new base_string("text/css"));
    return new base_element(text_library.LINK, attributes, null);
  }
}