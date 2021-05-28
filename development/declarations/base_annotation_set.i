-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Implementation of the annotation set.
auto_constructor class base_annotation_set {
  implements annotation_set;
  extends debuggable;

  private access_modifier the_access_level;
  private variance_modifier or null the_variance;
  private immutable set[modifier_kind] the_modifiers;
  private documentation or null the_documentation2;
  private immutable list[origin] the_origins;

  override access_modifier access_level => the_access_level;

  override variance_modifier or null variance => the_variance;

  override boolean has(modifier_kind the_kind) {
    verify the_kind is_not null;
    return the_modifiers.contains(the_kind);
  }

  override documentation or null the_documentation => the_documentation2;

  override origin or null deeper_origin() {
    -- TODO: update this.
    if (the_origins.is_empty) {
      return missing.instance;
    } else {
      return the_origins.first;
    }
  }

  immutable list[origin] origins => the_origins;

  readonly list[modifier_kind] modifiers => the_modifiers.elements;

  override readonly list[analyzable] children => empty[analyzable].new();

  override boolean has_errors => false;

  override base_annotation_set analyze => this;

  override base_annotation_set specialize(specialization_context context,
      principal_type new_parent) => this;

  base_annotation_set update_documentation(documentation or null new_documentation) {
    return base_annotation_set.new(the_access_level, variance, the_modifiers, new_documentation,
        the_origins);
  }
}
