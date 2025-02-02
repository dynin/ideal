-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.development.elements.access_modifier;
implicit import ideal.development.modifiers.general_modifier;

--- A selection of annotations that can be reused.
namespace annotation_library {
  PUBLIC_MODIFIERS : make_annotations(public_modifier, empty[modifier_kind].new());

  PUBLIC_OVERLOAD_MODIFIERS : make_annotations(public_modifier, [overload_modifier, ]);

  PUBLIC_OVERRIDE_MODIFIERS : make_annotations(public_modifier, [override_modifier, ]);

  PRIVATE_MODIFIERS : make_annotations(private_modifier, empty[modifier_kind].new());

  PRIVATE_VAR_MODIFIERS : make_annotations(private_modifier, [var_modifier, ]);

  PRIVATE_FINAL_MODIFIERS : make_annotations(private_modifier, [final_modifier, ]);

  PRIVATE_STATIC_MODIFIERS : make_annotations(private_modifier, [static_modifier, ]);

  PRIVATE_STATIC_VAR_MODIFIERS : make_annotations(private_modifier, [static_modifier, var_modifier]);

  annotation_set make_annotations(access_modifier the_access_modifier,
      readonly list[modifier_kind] the_modifiers) {
    modifier_set : hash_set[modifier_kind].new();
    modifier_set.add_all(the_modifiers);
    return base_annotation_set.new(the_access_modifier, missing.instance,
          modifier_set.frozen_copy, missing.instance, empty[origin].new());
  }
}
