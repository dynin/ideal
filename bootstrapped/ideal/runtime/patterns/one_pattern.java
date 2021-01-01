// Autogenerated from runtime/patterns/one_pattern.i

package ideal.runtime.patterns;

import ideal.library.elements.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public abstract class one_pattern<element_type> extends base_pattern<element_type> implements reversible_pattern<element_type> {
  public abstract boolean matches(element_type the_element);
  public @Override Boolean call(final readonly_list<element_type> the_list) {
    return the_list.size() == 1 && this.matches(the_list.first());
  }
  public @Override boolean is_viable_prefix(final readonly_list<element_type> the_list) {
    return the_list.is_empty() || (the_list.size() == 1 && this.matches(the_list.first()));
  }
  public @Override @Nullable Integer match_prefix(final readonly_list<element_type> the_list) {
    if (the_list.is_not_empty() && this.matches(the_list.first())) {
      return 1;
    } else {
      return null;
    }
  }
  public @Override @Nullable range find_first(final readonly_list<element_type> the_list, final int start_index) {
    for (int i = start_index; i < the_list.size(); i += 1) {
      if (this.matches(the_list.get(i))) {
        return new base_range(i, i + 1);
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
        return new base_range(i, i + 1);
      }
    }
    return null;
  }
  public @Override void validate() { }
}
