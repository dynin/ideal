// Autogenerated from isource/library/patterns.i

package ideal.library.patterns;

import ideal.library.elements.*;

import javax.annotation.Nullable;

public interface pattern<element_type> extends predicate<readonly_list<element_type>> {
  boolean is_viable_prefix(readonly_list<element_type> the_list);
  @Nullable range find_in(readonly_list<element_type> the_list, int start_index);
  immutable_list<immutable_list<element_type>> split(immutable_list<element_type> the_list);
}