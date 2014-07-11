// Autogenerated from isource/runtime/elements/base_range.i

package ideal.runtime.elements;

import ideal.library.elements.*;

public class base_range implements range {
  private final int the_begin;
  private final int the_end;
  public base_range(final int the_begin, final int the_end) {
    assert the_begin <= the_end;
    this.the_begin = the_begin;
    this.the_end = the_end;
  }
  public @Override int begin() {
    return the_begin;
  }
  public @Override int end() {
    return the_end;
  }
  public @Override int size() {
    final int the_size = the_end - the_begin;
    assert the_size >= 0;
    return the_size;
  }
  public @Override boolean is_empty() {
    return the_begin == the_end;
  }
  public @Override Integer get(final int index) {
    final int result = the_begin + index;
    assert result < the_end;
    return result;
  }
  public @Override range elements() {
    return this;
  }
  public @Override range frozen_copy() {
    return this;
  }
  public @Override range slice(final int slice_begin) {
    final int new_begin = the_begin + slice_begin;
    assert new_begin <= the_end;
    return new base_range(new_begin, the_end);
  }
  public @Override range slice(final int slice_begin, final int slice_end) {
    final int new_begin = the_begin + slice_begin;
    final int new_end = the_begin + slice_end;
    assert new_begin <= new_end;
    return new base_range(new_begin, new_end);
  }
  public @Override immutable_list<Integer> reverse() {
    final base_list<Integer> result = new base_list<Integer>();
    for (int value = the_end - 1; value >= the_begin; value -= 1) {
      assert value >= 0;
      result.append(value);
    }
    return result.frozen_copy();
  }
}