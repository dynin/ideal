-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Java-specific information.
namespace java_language {
  set[modifier_kind] supported_modifiers : hash_set[modifier_kind].new();
  set[modifier_kind] supported_annotations : hash_set[modifier_kind].new();

  var order[annotation_construct] annotation_order;

  static {
    modifiers_list : [
        -- TODO: resurrect documentation_modifier?

        -- Supported access modifiers
        access_modifier.public_modifier,
        access_modifier.protected_modifier,
        access_modifier.private_modifier,

        -- Supported general modifiers
        static_modifier,
        final_modifier,
        abstract_modifier,
        synchronized_modifier,
        volatile_modifier,
        transient_modifier,
        native_modifier
    ];

    annotations_list : [
        override_modifier,
        nullable_modifier,
        dont_display_modifier
    ];

    supported_annotations.add_all(annotations_list);

    all_modifiers : base_list[modifier_kind].new();
    all_modifiers.append_all(modifiers_list);
    all_modifiers.append_all(annotations_list);

    supported_modifiers.add_all(all_modifiers);

    -- Used by the import construct
    all_modifiers.append(implicit_modifier);
    annotation_order = annotation_list_order.new(all_modifiers);
  }
}
