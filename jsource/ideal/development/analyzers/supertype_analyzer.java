/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.modifiers.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.values.*;

public class supertype_analyzer extends declaration_analyzer implements supertype_declaration {

  private final readonly_list<annotation_construct> annotations;
  private final @Nullable type_flavor subtype_flavor;
  private final subtype_tag tag;
  private final readonly_list<analyzable> super_analyzables;
  private @Nullable readonly_list<type> super_types;

  public supertype_analyzer(supertype_construct the_supertype_construct) {
    super(the_supertype_construct);
    this.annotations = the_supertype_construct.annotations;
    this.subtype_flavor = the_supertype_construct.subtype_flavor;
    this.tag = the_supertype_construct.tag;
    this.super_analyzables = make_list(the_supertype_construct.type_constructs);
  }

  public supertype_analyzer(@Nullable type_flavor subtype_flavor, subtype_tag tag,
      readonly_list<analyzable> super_analyzables, origin the_origin) {
    super(the_origin);
    this.annotations = new empty<annotation_construct>();
    this.subtype_flavor = subtype_flavor;
    this.tag = tag;
    this.super_analyzables = super_analyzables;
  }

  public supertype_analyzer(readonly_list<annotation_construct> annotations,
      type_flavor subtype_flavor, subtype_tag tag, type the_type, origin the_origin) {
    super(the_origin);
    this.annotations = annotations;
    this.subtype_flavor = subtype_flavor;
    this.tag = tag;
    this.super_analyzables = new base_list<analyzable>(
        base_analyzable_action.from(the_type, the_origin));
  }

  @Override
  public type_flavor subtype_flavor() {
    return subtype_flavor;
  }

  @Override
  public subtype_tag tag() {
    return tag;
  }

  @Override
  public readonly_list<analyzable> children() {
    list<analyzable> result = new base_list<analyzable>();
    result.append(annotations());
    result.append_all(super_analyzables);
    return result;
  }

  @Override
  protected signal do_multi_pass_analysis(analysis_pass pass) {
    origin the_origin = this;

    if (pass == analysis_pass.IMPORT_AND_TYPE_VAR_DECL) {
      process_annotations(annotations, access_modifier.public_modifier);
      for (int i = 0; i < super_analyzables.size(); ++i) {
        add_dependence(super_analyzables.get(i), declared_in_type(), declaration_pass.NONE);
      }
    } else if (pass == analysis_pass.SUPERTYPE_DECL) {
      assert super_types == null;

      list<type> analyzed_types = new base_list<type>();
      super_types = analyzed_types;
      for (int i = 0; i < super_analyzables.size(); ++i) {
        analyzable super_analyzable = super_analyzables.get(i);

        // TODO: detect loops in inheritance hierarchy; move to base_semantics
        error_signal error = find_error(super_analyzable);
        if (error != null) {
          return new error_signal(messages.error_in_supertype, error, source);
        }

        action supertype_action = action_not_error(super_analyzable);
        if (! (supertype_action instanceof type_action)) {
          return new error_signal(messages.type_expected, super_analyzable);
        }

        type the_supertype = ((type_action) supertype_action).get_type();
        analyzed_types.append(the_supertype);

        get_context().add_supertype(declared_in_type(), the_supertype);

        // Note that promotion is from parent, not declared_in_type.
        // The reason for this is to allow accessing static symbols from the subtype.
        get_context().add(parent(), special_name.PROMOTION,
            the_supertype.principal().to_action(the_origin));
      }
    }

    return ok_signal.instance;
  }

  @Override
  public readonly_list<type> super_types() {
    assert super_types != null;
    return super_types;
  }

  @Override
  public supertype_analyzer specialize(specialization_context context, principal_type new_parent) {
    assert new_parent != declared_in_type();
    list<analyzable> specialized_supertypes = new base_list<analyzable>();
    for (int i = 0; i < super_analyzables.size(); ++i) {
      analyzable super_analyzable = super_analyzables.get(i);
      specialized_supertypes.append(super_analyzable.specialize(context, new_parent));
    }
    supertype_analyzer result = new supertype_analyzer(subtype_flavor, tag, specialized_supertypes,
        this);
    result.set_context(new_parent, get_context());
    result.multi_pass_analysis(analysis_pass.SUPERTYPE_DECL);
    return result;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, super_analyzables.first());
  }
}
