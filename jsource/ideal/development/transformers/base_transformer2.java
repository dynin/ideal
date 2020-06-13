/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
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

public class base_transformer2 extends analyzable_visitor<Object> {

  public construct transform(@Nullable analyzable_or_declaration the_analyzable) {
    if (the_analyzable == null) {
      return null;
    }

    construct new_construct = (construct) process(the_analyzable);
    return new_construct;
  }

  protected common_library library() {
    return common_library.get_instance();
  }

/*
  protected final annotation_construct transform(annotation_construct mod) {
    return (annotation_construct) transform((construct) mod);
  }*/

  public list<construct> transform1(analyzable_or_declaration the_analyzable) {
    if (the_analyzable instanceof statement_list_analyzer) {
      return transform_list(((statement_list_analyzer) the_analyzable).elements());
    } else if (the_analyzable instanceof declaration_list_analyzer) {
      return transform_list(((declaration_list_analyzer) the_analyzable).elements());
    } else {
      return transform_list(new base_list<analyzable_or_declaration>(the_analyzable));
    }
  }

  public list<construct> transform_list(
      @Nullable readonly_list<? extends analyzable_or_declaration> the_analyzables) {
    if (the_analyzables == null) {
      return null;
    }

    list<construct> result = new base_list<construct>();
    for (int i = 0; i < the_analyzables.size(); ++i) {
      if (the_analyzables.get(i) != null) {
        Object transformed = process(the_analyzables.get(i));
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
    }
    return result;
  }

  protected readonly_list<annotation_construct> to_annotations(annotation_set annotations,
      boolean skip_access, origin the_origin) {
    list<annotation_construct> result = new base_list<annotation_construct>();
    if (!skip_access && is_modifier_supported(annotations.access_level())) {
      result.append(new modifier_construct(annotations.access_level(), the_origin));
    }

    readonly_list<modifier_kind> modifiers = ((base_annotation_set) annotations).modifiers();
    for (int i = 0; i < modifiers.size(); ++i) {
      // TODO: handle parametrized modifiers
      modifier_kind the_modifier_kind = modifiers.get(i);
      if (is_modifier_supported(the_modifier_kind)) {
        result.append(new modifier_construct(the_modifier_kind, the_origin));
      }
    }

    return result;
  }

  protected boolean is_modifier_supported(modifier_kind the_modifier_kind) {
    return true;
  }

  protected @Nullable construct get_construct(@Nullable analyzable_or_declaration the_analyzable) {
    @Nullable origin the_origin = the_analyzable;

    while (the_origin != null) {
      if (the_origin instanceof construct) {
        return (construct) the_origin;
      }

      the_origin = the_origin.deeper_origin();
    }

    return null;
  }

  @Override
  public construct process_default(analyzable_or_declaration the_analyzable) {
    utilities.panic("base_transformer2.process_default()");
    return null;
  }

  public construct process_analyzable_action(analyzable_action the_analyzable_action) {
    return process_default(the_analyzable_action);
  }

  public construct process_block(block_declaration the_block) {
    origin the_origin = the_block;
    return new block_construct(to_annotations(the_block.annotations(), true, the_origin),
        transform1(the_block.get_body()), the_origin);
  }

  public construct process_conditional(conditional_analyzer the_conditional) {
    origin the_origin = the_conditional;
    // TODO: infer is_statement from conditional_analyzer
    boolean is_statement = true;
    construct the_construct = get_construct(the_conditional);
    if (the_construct instanceof conditional_construct) {
      is_statement = ((conditional_construct) the_construct).is_statement;
    }

    return new conditional_construct(transform(the_conditional.condition),
        transform(the_conditional.then_branch), transform(the_conditional.else_branch),
        is_statement, the_origin);
  }

  public construct process_constraint(constraint_analyzer the_constraint) {
    origin the_origin = the_constraint;
    return new constraint_construct(transform(the_constraint.expression), the_origin);
  }

  public construct process_declaration_list(declaration_list_analyzer the_declaration_list) {
    // TODO: report error
    return process_default(the_declaration_list);
  }

  public construct process_enum_value(enum_value_analyzer the_enum_value) {
    origin the_origin = the_enum_value;
    name_construct the_name = new name_construct(the_enum_value.short_name(), the_origin);
    if (the_enum_value.parameters != null) {
      grouping_type grouping = grouping_type.PARENS;
      construct the_construct = get_construct(the_enum_value);
      if (the_construct instanceof parameter_construct) {
        grouping = ((parameter_construct) the_construct).parameters.grouping;
      }
      return new parameter_construct(the_name, new list_construct(
          transform_list(the_enum_value.constructor_parameters()),
              grouping, false, the_origin), the_origin);
    } else {
      return the_name;
    }
  }

  public construct process_error_signal(error_signal the_error_signal) {
    return process_default(the_error_signal);
  }

  public construct process_extension(extension_analyzer the_extension) {
    return transform(the_extension.expand());
  }

  public construct process_flavor(flavor_analyzer the_flavor) {
    origin the_origin = the_flavor;
    return new flavor_construct(the_flavor.flavor, transform(the_flavor.expression), the_origin);
  }

  public import_construct process_import(import_analyzer the_import) {
    origin the_origin = the_import;
    return new import_construct(to_annotations(the_import.annotations(), true, the_origin),
        transform(the_import.type_analyzable), the_origin);
  }

  public construct process_jump(jump_analyzer the_jump) {
    origin the_origin = the_jump;
    return new jump_construct(the_jump.the_jump_type, the_origin);
  }

  public construct process_list_initializer(list_initializer_analyzer the_list_initializer) {
    origin the_origin = the_list_initializer;
    grouping_type grouping = grouping_type.PARENS;
    boolean has_trailing_comma = the_list_initializer.analyzable_parameters.size() == 1;
    return new list_construct(transform_list(the_list_initializer.analyzable_parameters),
        grouping, has_trailing_comma, the_origin);
  }

  public construct process_literal(literal_analyzer the_literal) {
    origin the_origin = the_literal;
    return new literal_construct(the_literal.the_literal, the_origin);
  }

  public construct process_loop(loop_analyzer the_loop) {
    origin the_origin = the_loop;
    return new loop_construct(transform(the_loop), the_origin);
  }

  public construct process_parameter(parameter_analyzer the_parameter) {
    origin the_origin = the_parameter;
    grouping_type grouping = grouping_type.PARENS;
    boolean has_trailing_comma = false;
    construct the_construct = get_construct(the_parameter);
    if (the_construct instanceof parameter_construct) {
      grouping = ((parameter_construct) the_construct).parameters.grouping;
      has_trailing_comma = ((parameter_construct) the_construct).parameters.has_trailing_comma;
    }
    return new parameter_construct(transform(the_parameter.main_analyzable),
        new list_construct(transform_list(the_parameter.analyzable_parameters),
            grouping, has_trailing_comma, the_origin), the_origin);
  }

  protected simple_name get_simple_name(principal_type the_type) {
    if (type_utilities.is_union(the_type)) {
      the_type = remove_null_type(the_type).principal();
    }

    if (the_type.short_name() instanceof simple_name) {
      return (simple_name) the_type.short_name();
    }

    assert the_type.get_parent() != null;
    return get_simple_name(the_type.get_parent());
  }


  protected type remove_null_type(type the_type) {
    assert type_utilities.is_union(the_type);
    type_flavor union_flavor = the_type.get_flavor();
    immutable_list<abstract_value> the_values = type_utilities.get_union_parameters(the_type);
    assert the_values.size() == 2;
    type null_type = (type) the_values.get(1);
    assert null_type.principal() == library().null_type();
    type result = (type) the_values.first();
    if (union_flavor != flavor.nameonly_flavor) {
      result = result.get_flavored(union_flavor);
    }
    return result;
  }

  protected simple_name make_name(simple_name type_name, principal_type the_type,
      type_flavor flavor) {
    if (flavor == nameonly_flavor || flavor == the_type.get_flavor_profile().default_flavor()) {
      return type_name;
    } else {
      return name_utilities.join(flavor.name(), type_name);
    }
  }

  private construct make_full_name(construct name, principal_type the_type, origin the_origin) {
    @Nullable principal_type parent = the_type.get_parent();
    if (parent == null) {
      return name;
    }

    if (! (parent.short_name() instanceof simple_name)) {
      utilities.panic("Full name of " + the_type + ", parent " + parent);
    }

    construct parent_name = new name_construct(parent.short_name(), the_origin);
    return make_full_name(new resolve_construct(parent_name, name, the_origin), parent, the_origin);
  }

  protected construct make_type(type the_type, origin the_origin) {
    principal_type principal = the_type.principal();

    if (type_utilities.is_union(principal)) {
      principal = remove_null_type(principal).principal();
    }

    construct name = new name_construct(make_name(get_simple_name(principal), principal,
        the_type.get_flavor()), the_origin);
    return make_full_name(name, principal, the_origin);
  }

  public construct process_procedure(procedure_declaration the_procedure) {
    origin the_origin = the_procedure;
    grouping_type grouping = grouping_type.PARENS;
    boolean has_trailing_comma = false;
    list_construct parameters = new list_construct(
        transform_list(the_procedure.get_parameter_variables()),
            grouping, has_trailing_comma, the_origin);
    return new procedure_construct(to_annotations(the_procedure.annotations(), false, the_origin),
        make_type(the_procedure.get_return_type(), the_origin), the_procedure.original_name(),
        parameters, new empty<annotation_construct>(),
        transform(the_procedure.get_body()), the_origin);
  }

  public construct process_resolve(resolve_analyzer the_resolve) {
    origin the_origin = the_resolve;
    name_construct the_name = new name_construct(the_resolve.short_name(), the_origin);
    if (the_resolve.has_from()) {
      return new resolve_construct(transform(the_resolve.get_from()), the_name, the_origin);
    } else {
      return the_name;
    }
  }

  public construct process_return(return_analyzer the_return) {
    origin the_origin = the_return;
    return new return_construct(transform(the_return.the_expression), the_return);
  }

  public construct process_statement_list(statement_list_analyzer the_statement_list) {
    utilities.panic("base_transformer2.process_statement_list()");
    return process_default(the_statement_list);
  }

  public construct process_supertype(supertype_declaration the_supertype) {
    origin the_origin = the_supertype;
    // TODO: add annotation support
    return new supertype_construct(new empty<annotation_construct>(),
        the_supertype.subtype_flavor(), the_supertype.tag(),
        new base_list<construct>(transform(the_supertype.supertype_analyzable())), the_origin);
  }

  public construct process_target(target_declaration the_target) {
    origin the_origin = the_target;
    return new target_construct(the_target.short_name(),
        transform(the_target.get_expression()), the_origin);
  }

  public construct process_type_announcement(type_announcement the_type_announcement) {
    origin the_origin = the_type_announcement;
    // TODO: skip annotations?
    return new type_announcement_construct(
        to_annotations(the_type_announcement.annotations(), true, the_origin),
        the_type_announcement.get_kind(), the_type_announcement.short_name(), the_origin);
  }

  public construct process_type(type_declaration the_type) {
    origin the_origin = the_type;
    @Nullable list_construct parameters = null;
    if (the_type.get_parameters() != null)  {
      parameters = new list_construct(transform_list(the_type.get_parameters()),
          grouping_type.PARENS, false, the_origin);
    }
    return new type_declaration_construct(to_annotations(the_type.annotations(), false, the_origin),
        the_type.get_kind(), the_type.short_name(), parameters,
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
        to_annotations(the_type_parameter.annotations(), true, the_origin),
        the_type, the_type_parameter.short_name(), new empty<annotation_construct>(), null,
        the_origin);
  }

  public construct process_variable(variable_declaration the_variable) {
    origin the_origin = the_variable;
    construct the_type;
    if (the_variable.value_type() != null) {
      the_type = make_type(the_variable.value_type(), the_origin);
    } else {
      the_type = null;
    }
    variable_category category = the_variable.get_category();
    boolean skip_access = category == variable_category.LOCAL ||
        category == variable_category.ENUM_VALUE;
    return new variable_construct(to_annotations(the_variable.annotations(), skip_access,
        the_origin), the_type, the_variable.short_name(), new empty<annotation_construct>(),
        transform(the_variable.initializer()), the_origin);
  }
}
