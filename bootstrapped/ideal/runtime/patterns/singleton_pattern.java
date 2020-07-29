// Autogenerated from runtime/patterns/singleton_pattern.i

package ideal.runtime.patterns;

import ideal.library.elements.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public class singleton_pattern<element_type> implements reversible_pattern<element_type> {
  public final element_type the_element;
  public singleton_pattern(final element_type the_element) {
    this.the_element = the_element;
  }
  public @Override Boolean call(final readonly_list<element_type> the_list) {
    return the_list.size() == 1 && ideal.machine.elements.runtime_util.values_equal(the_list.first(), this.the_element);
  }
  public @Override boolean is_viable_prefix(final readonly_list<element_type> the_list) {
    return the_list.is_empty() || (the_list.size() == 1 && ideal.machine.elements.runtime_util.values_equal(the_list.first(), this.the_element));
  }
  public @Override @Nullable range find_first(final readonly_list<element_type> the_list, final int start_index) {
    for (int i = start_index; i < the_list.size(); i += 1) {
      if (ideal.machine.elements.runtime_util.values_equal(the_list.get(i), this.the_element)) {
        return new base_range(i, i + 1);
      }
    }
    return null;
  }
  public @Override @Nullable range find_last(final readonly_list<element_type> the_list, @Nullable Integer end_index) {
    int i;
    if (end_index == null) {
      i = the_list.size() - 1;
    } else {
      assert end_index < the_list.size();
      i = end_index;
    }
    for (; i >= 0; i -= 1) {
      assert i >= 0;
      if (ideal.machine.elements.runtime_util.values_equal(the_list.get(i), this.the_element)) {
        return new base_range(i, i + 1);
      }
    }
    return null;
  }
  public @Override immutable_list<immutable_list<element_type>> split(final immutable_list<element_type> the_list) {
    final base_list<immutable_list<element_type>> result = new base_list<immutable_list<element_type>>();
    int index = 0;
    while (true) {
      final @Nullable range match = this.find_first(the_list, index);
      if (match != null) {
        result.append(the_list.slice(index, match.begin()));
        index = match.end();
      } else {
        result.append(the_list.skip(index));
        break;
      }
    }
    return result.frozen_copy();
  }
}
