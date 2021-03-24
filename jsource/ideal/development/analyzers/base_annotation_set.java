/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.comments.*;
import ideal.development.modifiers.*;
import ideal.development.constructs.*;
import ideal.development.declarations.*;

import javax.annotation.Nullable;

public class base_annotation_set extends debuggable implements annotation_set {

  private final access_modifier access_level;
  private final immutable_set<modifier_kind> the_modifiers;
  private final @Nullable documentation the_documentation;
  private final immutable_list<origin> origins;

  public base_annotation_set(access_modifier access_level,
      readonly_set<modifier_kind> the_modifiers,
      @Nullable documentation the_documentation,
      immutable_list<origin> origins) {
    assert access_level != null;

    this.access_level = access_level;
    this.the_modifiers = the_modifiers.frozen_copy();
    this.the_documentation = the_documentation;
    this.origins = origins;
  }

  @Override
  public access_modifier access_level() {
    return access_level;
  }

  @Override
  public boolean has(modifier_kind the_kind) {
    assert the_kind != null;
    return the_modifiers.contains(the_kind);
  }

  @Override
  public @Nullable documentation the_documentation() {
    return the_documentation;
  }

  @Override
  public origin deeper_origin() {
    // TODO: update this.
    if (origins.is_empty()) {
      return null;
    } else {
      return origins.first();
    }
  }

  // TODO: drop this.
  @Override
  public immutable_list<origin> origins() {
    return origins;
  }

  public readonly_list<modifier_kind> modifiers() {
    return the_modifiers.elements();
  }

  @Override
  public readonly_list<analyzable> children() {
    return new empty<analyzable>();
  }

  @Override
  public boolean has_errors() {
    return false;
  }

  @Override
  public base_annotation_set analyze() {
    return this;
  }

  @Override
  public base_annotation_set specialize(specialization_context context, principal_type new_parent) {
    return this;
  }

  public base_annotation_set update_documentation(@Nullable documentation new_documentation) {
    return new base_annotation_set(access_level, the_modifiers, new_documentation, origins);
  }
}
