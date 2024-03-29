// Autogenerated from runtime/patterns/sequence_pattern.i

package ideal.runtime.patterns;

import ideal.library.elements.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public class sequence_pattern<element_type> extends base_pattern<element_type> {
  public final immutable_list<pattern<element_type>> patterns_list;
  private boolean validated;
  public sequence_pattern(final readonly_list<pattern<element_type>> patterns_list) {
    this.patterns_list = patterns_list.frozen_copy();
  }
  public @Override void validate() {
    if (this.validated) {
      return;
    }
    this.validated = true;
    assert this.patterns_list.is_not_empty();
    {
      final readonly_list<pattern<element_type>> the_pattern_list = this.patterns_list;
      for (Integer the_pattern_index = 0; the_pattern_index < the_pattern_list.size(); the_pattern_index += 1) {
        final pattern<element_type> the_pattern = the_pattern_list.get(the_pattern_index);
        ((validatable) the_pattern).validate();
      }
    }
  }
  public @Override Boolean call(final readonly_list<element_type> the_list) {
    final @Nullable Integer match = this.match_prefix(the_list);
    return match != null && ideal.machine.elements.runtime_util.values_equal(match, the_list.size());
  }
  public @Override boolean is_viable_prefix(final readonly_list<element_type> the_list) {
    if (the_list.is_empty()) {
      return true;
    }
    Integer index = 0;
    Integer prefix = 0;
    while (index < this.patterns_list.size() - 1) {
      final @Nullable Integer match = this.patterns_list.get(index).match_prefix(the_list.skip(prefix));
      if (match == null) {
        return false;
      }
      prefix += match;
      if (ideal.machine.elements.runtime_util.values_equal(prefix, the_list.size())) {
        return true;
      }
      index += 1;
    }
    assert ideal.machine.elements.runtime_util.values_equal(index, this.patterns_list.size() - 1);
    return this.patterns_list.get(index).is_viable_prefix(the_list.skip(prefix));
  }
  private @Nullable Integer match_subsequence(final readonly_list<element_type> the_list, Integer index, Integer prefix) {
    while (index < this.patterns_list.size()) {
      final @Nullable Integer match = this.patterns_list.get(index).match_prefix(the_list.skip(prefix));
      if (match == null) {
        return null;
      }
      prefix += match;
      index += 1;
    }
    return prefix;
  }
  public @Override @Nullable Integer match_prefix(final readonly_list<element_type> the_list) {
    return this.match_subsequence(the_list, 0, 0);
  }
  public @Override @Nullable range find_first(final readonly_list<element_type> the_list, Integer start_index) {
    while (start_index <= the_list.size()) {
      final @Nullable range first_match = this.patterns_list.get(0).find_first(the_list, start_index);
      if (first_match == null) {
        return null;
      }
      final @Nullable Integer rest_match = this.match_subsequence(the_list, 1, first_match.end());
      if (rest_match != null) {
        return new base_range(first_match.begin(), rest_match);
      }
      start_index = first_match.begin() + 1;
    }
    return null;
  }
}
