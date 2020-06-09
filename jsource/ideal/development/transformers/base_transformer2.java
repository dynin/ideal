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
import ideal.development.declarations.*;
import ideal.development.constructs.*;
import ideal.development.analyzers.*;
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
      origin the_origin) {
    list<annotation_construct> result = new base_list<annotation_construct>();
    result.append(new modifier_construct(annotations.access_level(), the_origin));

    readonly_list<modifier_kind> modifiers = ((base_annotation_set) annotations).modifiers();
    for (int i = 0; i < modifiers.size(); ++i) {
      // TODO: handle parametrized modifiers
      result.append(new modifier_construct(modifiers.get(i), the_origin));
    }

    return result;
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
    return new block_construct(to_annotations(the_block.annotations(), the_origin),
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

  public construct process_import(import_analyzer the_import) {
    return process_default(the_import);
  }

  public construct process_jump(jump_analyzer the_jump) {
    return process_default(the_jump);
  }

  public construct process_list_initializer(list_initializer_analyzer the_list_initializer) {
    return process_default(the_list_initializer);
  }

  public construct process_literal(literal_analyzer the_literal) {
    return process_default(the_literal);
  }

  public construct process_loop(loop_analyzer the_loop) {
    return process_default(the_loop);
  }

  public construct process_parameter(parameter_analyzer the_parameter) {
    return process_default(the_parameter);
  }

  public construct process_procedure(procedure_declaration the_procedure) {
    return process_default(the_procedure);
  }

  public construct process_resolve(resolve_analyzer the_resolve) {
    return process_default(the_resolve);
  }

  public construct process_return(return_analyzer the_return) {
    return process_default(the_return);
  }

  public construct process_statement_list(statement_list_analyzer the_statement_list) {
    return process_default(the_statement_list);
  }

  public construct process_supertype(supertype_declaration the_supertype) {
    return process_default(the_supertype);
  }

  public construct process_target(target_declaration the_target) {
    return process_default(the_target);
  }

  public construct process_type_announcement(type_announcement the_type_announcement) {
    return process_default(the_type_announcement);
  }

  public construct process_type(type_declaration the_type) {
    return process_default(the_type);
  }

  public construct process_type_parameter(type_parameter_declaration the_type_parameter) {
    return process_default(the_type_parameter);
  }

  public construct process_variable(variable_declaration the_variable) {
    return process_default(the_variable);
  }
}
