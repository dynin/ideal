/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.transformers;

import javax.annotation.Nullable;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.declarations.*;
import ideal.development.constructs.*;
import ideal.development.analyzers.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.targets.*;
import static ideal.development.flavors.flavor.*;
import static ideal.development.kinds.type_kinds.*;
import static ideal.development.kinds.subtype_tags.*;
import ideal.development.notifications.error_signal;
import ideal.development.extensions.extension_analyzer;
import ideal.development.targets.target_declaration;

public class base_transformer extends declaration_visitor<Object> {

  public construct transform(@Nullable declaration the_declaration) {
    if (the_declaration == null) {
      return null;
    }

    construct new_construct = (construct) process(the_declaration);
    return new_construct;
  }

  public list<construct> transform1(declaration the_analyzable) {
    if (the_analyzable instanceof list_analyzer) {
      return transform_list(((list_analyzer) the_analyzable).declarations());
    } else {
      return transform_list(new base_list<declaration>(the_analyzable));
    }
  }

  public list<construct> transform_list(
      @Nullable readonly_list<? extends declaration> the_analyzables) {
    if (the_analyzables == null) {
      return null;
    }

    list<construct> result = new base_list<construct>();
    for (int i = 0; i < the_analyzables.size(); ++i) {
      declaration the_analyzable = the_analyzables.get(i);
      if (the_analyzable == null) {
        continue;
      }

      Object transformed = process(the_analyzable);
      if (transformed instanceof construct) {
        result.append((construct) transformed);
      } else if (transformed instanceof readonly_list/*<construct>*/) {
        result.append_all((readonly_list<construct>) transformed);
      } else if (transformed == null) {
        // nothing
      } else {
        utilities.panic("Unknown result of transform " + transformed);
      }
    }
    return result;
  }

  protected list<annotation_construct> to_annotations(annotation_set annotations,
      annotation_category category, boolean skip_access, origin the_origin) {
    list<annotation_construct> result = new base_list<annotation_construct>();
    if (!skip_access) {
      @Nullable modifier_kind processed = process_modifier(annotations.access_level(), category);
      if (processed != null) {
        result.append(new modifier_construct(processed, the_origin));
      }
    }

    readonly_list<modifier_kind> modifiers = ((base_annotation_set) annotations).modifiers();
    for (int i = 0; i < modifiers.size(); ++i) {
      // TODO: handle parametrized modifiers
      modifier_kind the_modifier_kind = modifiers.get(i);
      @Nullable modifier_kind processed = process_modifier(the_modifier_kind, category);
      if (processed != null) {
        result.append(new modifier_construct(processed, the_origin));
      }
    }

    return result;
  }

  protected modifier_kind process_modifier(modifier_kind the_modifier_kind,
      annotation_category category) {
    return the_modifier_kind;
  }

  protected @Nullable construct get_construct(@Nullable origin the_origin) {
    while (the_origin != null) {
      if (the_origin instanceof construct) {
        return (construct) the_origin;
      }

      the_origin = the_origin.deeper_origin();
    }

    return null;
  }

  public construct transform_action(action the_action) {
    utilities.panic("transform_action: " + the_action);
    return null;
  }

  @Override
  public construct process_default(declaration the_declaration) {
    utilities.panic("base_transformer.process_default()");
    return null;
  }

  public construct process_analyzable_action(analyzable_action the_analyzable_action) {
    origin the_origin = the_analyzable_action;
    action the_action = the_analyzable_action.get_action();
    return process_action(the_action, the_origin);
  }

  public construct process_action(action the_action, origin the_origin) {
    if (the_action instanceof type_action) {
      return make_type(((type_action) the_action).get_type(), the_origin);
    }

    utilities.panic("processing action " + the_action);
    return null;
  }

  public construct process_block(block_declaration the_block) {
    // TODO: this should always be overriden
    return process_default(the_block);
  }

  public construct process_enum_value(enum_value_analyzer the_enum_value) {
    return process_default(the_enum_value);
  }

  @Override
  public construct process_list(list_analyzer the_list_analyzer) {
    // TODO: report error
    return process_default(the_list_analyzer);
  }

  public import_construct process_import(import_declaration the_import) {
    origin the_origin = the_import;
    return new import_construct(to_annotations(the_import.annotations(),
        annotation_category.IMPORT, true, the_origin),
        make_type(the_import.get_type(), the_origin), the_origin);
  }

  protected simple_name get_simple_name(principal_type the_type) {
    if (type_utilities.is_union(the_type)) {
      the_type = common_types.remove_null_type(the_type).principal();
    }

    if (the_type.short_name() instanceof simple_name) {
      return (simple_name) the_type.short_name();
    }

    assert the_type.get_parent() != null;
    return get_simple_name(the_type.get_parent());
  }

  protected simple_name make_name(simple_name type_name, principal_type the_type,
      type_flavor flavor) {
    if (flavor == nameonly_flavor || flavor == the_type.get_flavor_profile().default_flavor()) {
      return type_name;
    } else {
      return name_utilities.join(flavor.name(), type_name);
    }
  }

  protected static construct make_resolve(@Nullable construct qualifier, action_name the_name,
      origin the_origin) {
    if (qualifier == null) {
      return new name_construct(the_name, the_origin);
    } else {
      return new resolve_construct(qualifier, the_name, the_origin);
    }
  }

  private @Nullable construct make_full_name(principal_type the_type, origin the_origin) {
    if (the_type == null || the_type == common_types.root_type()) {
      return null;
    }

    if (! (the_type.short_name() instanceof simple_name)) {
      utilities.panic("Full name of " + the_type);
    }

    return make_resolve(make_full_name(the_type.get_parent(), the_origin), the_type.short_name(),
        the_origin);
  }

  protected construct make_type(type the_type, origin the_origin) {
    principal_type principal = the_type.principal();

    if (type_utilities.is_union(principal)) {
      principal = common_types.remove_null_type(principal).principal();
    }

    action_name the_name = make_name(get_simple_name(principal), principal, the_type.get_flavor());
    return make_resolve(make_full_name(principal.get_parent(), the_origin), the_name, the_origin);
  }

  public construct process_procedure(procedure_declaration the_procedure) {
    origin the_origin = the_procedure;
    return new procedure_construct(to_annotations(the_procedure.annotations(),
        annotation_category.PROCEDURE, false, the_origin),
        make_type(the_procedure.get_return_type(), the_origin), the_procedure.original_name(),
        transform_list(the_procedure.get_parameter_variables()), new empty<annotation_construct>(),
        transform_action(the_procedure.get_body_action()), the_origin);
  }

  public construct process_supertype(supertype_declaration the_supertype) {
    origin the_origin = the_supertype;
    // TODO: add annotation support
    return new supertype_construct(new empty<annotation_construct>(),
        the_supertype.subtype_flavor(), the_supertype.tag(),
        new base_list<construct>(make_type(the_supertype.get_supertype(), the_origin)), the_origin);
  }

  public construct process_target(target_declaration the_target) {
    origin the_origin = the_target;
    return new target_construct(the_target.short_name(),
        transform_action(the_target.get_target_action()), the_origin);
  }

  public construct process_type_announcement(type_announcement the_type_announcement) {
    origin the_origin = the_type_announcement;
    // TODO: skip annotations?
    return new type_announcement_construct(
        to_annotations(the_type_announcement.annotations(), annotation_category.TYPE, true,
            the_origin),
        the_type_announcement.get_kind(), the_type_announcement.short_name(), the_origin);
  }

  public Object process_type(type_declaration the_type) {
    origin the_origin = the_type;
    return new type_declaration_construct(to_annotations(the_type.annotations(),
        annotation_category.TYPE, false, the_origin),
        the_type.get_kind(), the_type.short_name(), transform_list(the_type.get_parameters()),
        transform_list(the_type.get_signature()), the_origin);
  }

  public construct process_type_parameter(type_parameter_declaration the_type_parameter) {
    origin the_origin = the_type_parameter;
    construct the_type;
    if (the_type_parameter.variable_type() != null) {
      the_type = make_type(the_type_parameter.variable_type(), the_origin);
    } else {
      the_type = null;
    }
    return new variable_construct(
        to_annotations(the_type_parameter.annotations(), annotation_category.TYPE_PARAMETER, true,
            the_origin),
        the_type, the_type_parameter.short_name(), new empty<annotation_construct>(), null,
        the_origin);
  }

  public construct process_variable(variable_declaration the_variable) {
    origin the_origin = the_variable;
    construct the_type = make_type(the_variable.value_type(), the_origin);
    /*
    if (the_variable.value_type() != null) {
      the_type = make_type(the_variable.value_type(), the_origin);
    } else {
      the_type = null;
    }
    variable_category category = the_variable.get_category();
    boolean skip_access = category == variable_category.LOCAL ||
        category == variable_category.ENUM_VALUE;
    */
    boolean skip_access = false;
    return new variable_construct(to_annotations(the_variable.annotations(),
        annotation_category.VARIABLE, skip_access,
        the_origin), the_type, the_variable.short_name(), new empty<annotation_construct>(),
        transform_action(the_variable.init_action()), the_origin);
  }
}
