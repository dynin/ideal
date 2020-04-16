-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class base_range {
  implements range;

  private nonnegative the_begin;
  private nonnegative the_end;

  base_range(nonnegative the_begin, nonnegative the_end) {
    assert the_begin <= the_end;
    this.the_begin = the_begin;
    this.the_end = the_end;
  }

  -- TODO: declared field 'begin' should make this redundant.
  implement nonnegative begin() {
    return the_begin;
  }

  -- TODO: declared field 'end' should make this redundant.
  implement nonnegative end() {
    return the_end;
  }

  implement nonnegative size() {
    the_size : the_end - the_begin;
    assert the_size is nonnegative;
    return the_size;
  }

  implement boolean is_empty() {
    return the_begin == the_end;
  }

  implement implicit readonly reference[nonnegative] get(nonnegative index) pure {
    result : the_begin + index;
    assert result < the_end;
    return result;
  }

  implement range elements() {
    return this;
  }

  implement range frozen_copy() {
    return this;
  }

  implement range skip(nonnegative count) {
    new_begin : the_begin + count;
    assert new_begin <= the_end;
    return base_range.new(new_begin, the_end);
  }

  implement range slice(nonnegative slice_begin, nonnegative slice_end) {
    new_begin : the_begin + slice_begin;
    new_end : the_begin + slice_end;
    assert new_begin <= new_end;
    return base_range.new(new_begin, new_end);
  }

  implement immutable list[nonnegative] reverse() {
    -- TODO: implement reverse_range.
    result : base_list[nonnegative].new();
    for (var value : the_end - 1; value >= the_begin; value -= 1) {
      assert value is nonnegative;
      result.append(value);
    }
    return result.frozen_copy();
  }
}
