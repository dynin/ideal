/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
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

public class supertype_analyzer extends declaration_analyzer implements supertype_declaration {

  private final readonly_list<annotation_construct> annotations;
  private final @Nullable type_flavor subtype_flavor;
  private final subtype_tag tag;
  private final analyzable the_analyzable;
  private @Nullable type the_supertype;

  public supertype_analyzer(type_flavor subtype_flavor, subtype_tag tag,
      analyzable the_analyzable, origin the_origin) {
    super(the_origin);
    this.annotations = new empty<annotation_construct>();
    this.subtype_flavor = subtype_flavor;
    this.tag = tag;
    this.the_analyzable = the_analyzable;
  }

  public supertype_analyzer(readonly_list<annotation_construct> annotations,
      type_flavor subtype_flavor, subtype_tag tag, type the_type, origin the_origin) {
    super(the_origin);
    this.annotations = annotations;
    this.subtype_flavor = subtype_flavor;
    this.tag = tag;
    this.the_analyzable = base_analyzable_action.from(the_type, the_origin);
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
  protected signal do_multi_pass_analysis(analysis_pass pass) {
    origin the_origin = this;

    if (pass == analysis_pass.IMPORT_AND_TYPE_VAR_DECL) {
      process_annotations(annotations, access_modifier.public_modifier);
      add_dependence(the_analyzable, declared_in_type(), declaration_pass.NONE);

    } else if (pass == analysis_pass.SUPERTYPE_DECL) {

      assert the_supertype == null;

      // TODO: detect loops in inheritance hierarchy; move to semantics
      error_signal error = find_error(the_analyzable);
      if (error != null) {
        return new error_signal(messages.error_in_supertype, error, source);
      }

      action supertype_action = action_not_error(the_analyzable);
      if (! (supertype_action instanceof type_action)) {
        return new error_signal(messages.type_expected, the_analyzable);
      }

      the_supertype = ((type_action) supertype_action).get_type();

      get_context().add(declared_in_type(), special_name.SUPERTYPE,
          the_supertype.to_action(the_origin));

      // Note that promotion is from parent, not declared_in_type.
      // The reason for this is to allow accessing static symbols from the subtype.
      get_context().add(parent(), special_name.PROMOTION,
          the_supertype.principal().to_action(the_origin));
    } else if (pass == analysis_pass.BODY_CHECK) {

      if (false && !has_errors()) {
        type_utilities.prepare(declared_in_type(), declaration_pass.TYPES_AND_PROMOTIONS);
        action_utilities.process_super_flavors(declared_in_type(),
            subtype_flavor(), get_supertype(), the_origin, get_context());
      }
    }

    return ok_signal.instance;
  }

  @Override
  public type get_supertype() {
    assert the_supertype != null;
    return the_supertype;
  }

  @Override
  public supertype_analyzer specialize(specialization_context context, principal_type new_parent) {
    assert new_parent != declared_in_type();
    analyzable specialized = the_analyzable.specialize(context, new_parent);
    supertype_analyzer result = new supertype_analyzer(subtype_flavor, tag, specialized, this);
    result.set_context(new_parent, get_context());
    result.multi_pass_analysis(analysis_pass.SUPERTYPE_DECL);
    return result;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, the_analyzable);
  }
}
