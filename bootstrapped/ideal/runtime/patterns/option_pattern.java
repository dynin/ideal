// Autogenerated from runtime/patterns/option_pattern.i

package ideal.runtime.patterns;

import ideal.library.elements.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public class option_pattern<element_type> extends base_pattern<element_type> {
  protected final list<pattern<element_type>> options;
  private boolean validated;
  public option_pattern(final readonly_collection<pattern<element_type>> options) {
    this.options = new base_list<pattern<element_type>>();
    ((base_list<pattern<element_type>>) this.options).append_all(options.elements());
  }
  public void add_option(final pattern<element_type> option) {
    this.options.append(option);
  }
  public @Override void validate() {
    if (this.validated) {
      return;
    }
    this.validated = true;
    assert this.options.size() > 1;
    {
      final readonly_list<pattern<element_type>> option_list = this.options;
      for (Integer option_index = 0; option_index < option_list.size(); option_index += 1) {
        final pattern<element_type> option = option_list.get(option_index);
        ((validatable) option).validate();
        assert !((function1<Boolean, readonly_list<element_type>>) (Object) option).call(new empty<element_type>());
      }
    }
  }
  public @Override Boolean call(final readonly_list<element_type> the_list) {
    {
      final readonly_list<pattern<element_type>> option_list = this.options;
      for (Integer option_index = 0; option_index < option_list.size(); option_index += 1) {
        final pattern<element_type> option = option_list.get(option_index);
        if (((function1<Boolean, readonly_list<element_type>>) (Object) option).call(the_list)) {
          return true;
        }
      }
    }
    return false;
  }
  public @Override boolean is_viable_prefix(final readonly_list<element_type> the_list) {
    if (the_list.is_empty()) {
      return true;
    }
    {
      final readonly_list<pattern<element_type>> option_list = this.options;
      for (Integer option_index = 0; option_index < option_list.size(); option_index += 1) {
        final pattern<element_type> option = option_list.get(option_index);
        if (option.is_viable_prefix(the_list)) {
          return true;
        }
      }
    }
    return false;
  }
  public @Override @Nullable Integer match_prefix(final readonly_list<element_type> the_list) {
    @Nullable Integer result = null;
    {
      final readonly_list<pattern<element_type>> option_list = this.options;
      for (Integer option_index = 0; option_index < option_list.size(); option_index += 1) {
        final pattern<element_type> option = option_list.get(option_index);
        final @Nullable Integer match = option.match_prefix(the_list);
        if (match != null) {
          if (result == null || result < match) {
            result = match;
          }
        }
      }
    }
    return result;
  }
  public @Override @Nullable range find_first(final readonly_list<element_type> the_list, Integer start_index) {
    @Nullable range result = null;
    {
      final readonly_list<pattern<element_type>> option_list = this.options;
      for (Integer option_index = 0; option_index < option_list.size(); option_index += 1) {
        final pattern<element_type> option = option_list.get(option_index);
        final @Nullable range match = option.find_first(the_list, start_index);
        if (match == null) {
          continue;
        }
        if (result == null) {
          result = match;
        } else if (match.begin() < result.begin() || (ideal.machine.elements.runtime_util.values_equal(match.begin(), result.begin()) && match.end() > result.end())) {
          result = match;
        }
      }
    }
    return result;
  }
}
