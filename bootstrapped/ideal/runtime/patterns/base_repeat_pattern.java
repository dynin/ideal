// Autogenerated from runtime/patterns/base_repeat_pattern.i

package ideal.runtime.patterns;

import ideal.library.elements.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public abstract class base_repeat_pattern<element_type> extends base_pattern<element_type> implements reversible_pattern<element_type> {
  public abstract boolean matches(element_type the_element);
  public abstract boolean match_empty();
  public @Override Boolean call(final readonly_list<element_type> the_list) {
    int index = 0;
    while (index < the_list.size()) {
      if (!this.matches(the_list.get(index))) {
        return false;
      }
      index += 1;
    }
    return index > 0 || this.match_empty();
  }
  public @Override boolean is_viable_prefix(final readonly_list<element_type> the_list) {
    {
      final readonly_list<element_type> the_element_list = the_list;
      for (int the_element_index = 0; the_element_index < the_element_list.size(); the_element_index += 1) {
        final element_type the_element = the_element_list.get(the_element_index);
        if (!this.matches(the_element)) {
          return false;
        }
      }
    }
    return true;
  }
  public @Override @Nullable Integer match_prefix(final readonly_list<element_type> the_list) {
    int index = 0;
    while (index < the_list.size() && this.matches(the_list.get(index))) {
      index += 1;
    }
    if (index == 0 && !this.match_empty()) {
      return null;
    } else {
      return index;
    }
  }
  public @Override @Nullable range find_first(final readonly_list<element_type> the_list, final int start_index) {
    for (int i = start_index; i < the_list.size(); i += 1) {
      if (this.matches(the_list.get(i))) {
        final int start_range = i;
        i += 1;
        while (i < the_list.size() && this.matches(the_list.get(i))) {
          i += 1;
        }
        return new base_range(start_range, i);
      }
    }
    return null;
  }
  public @Override @Nullable range find_last(final readonly_list<element_type> the_list, final @Nullable Integer end_index) {
    int i;
    if (end_index == null) {
      i = the_list.size() - 1;
    } else {
      assert end_index <= the_list.size();
      i = end_index - 1;
    }
    for (; i >= 0; i -= 1) {
      assert i >= 0;
      if (this.matches(the_list.get(i))) {
        final int end_range = i + 1;
        while (i > 0) {
          final int check_start = i - 1;
          assert check_start >= 0;
          if (!this.matches(the_list.get(check_start))) {
            break;
          }
          i = check_start;
        }
        return new base_range(i, end_range);
      }
    }
    return null;
  }
}
