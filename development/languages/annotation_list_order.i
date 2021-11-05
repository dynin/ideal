-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Ordering of annotations based on a list.
class annotation_list_order {
  implements order[annotation_construct];

  private mapping : hash_dictionary[modifier_kind, nonnegative].new();

  annotation_list_order(the readonly list[modifier_kind]) {
    for (index : the_list.indexes) {
      mapping.put(the_list[index], index);
    }
  }

  implement implicit sign call(annotation_construct first, annotation_construct second) {
    if (first is comment_construct && second is comment_construct) {
      return sign.equal;
    }
    if (first is comment_construct) {
      return sign.less;
    }
    if (second is comment_construct) {
      return sign.greater;
    }

    assert first is modifier_construct;
    assert second is modifier_construct;

    first_index : mapping.get(first.the_kind);
    second_index : mapping.get(second.the_kind);
    assert first_index is_not null;
    assert second_index is_not null;

    return first_index <=> second_index;
  }
}
