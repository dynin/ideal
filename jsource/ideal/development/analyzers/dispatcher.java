/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.actions.*;
import ideal.development.elements.*;
import ideal.development.kinds.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.switches.switch_analyzer;
import ideal.development.extensions.grouping_analyzer;
import ideal.development.extensions.test_suite_extension;
import javax.annotation.Nullable;


public class dispatcher extends construct_visitor<analyzable> {

  @Override
  public analyzable process_default(construct source) {
    utilities.panic("Unknown construct " + source);
    return new error_signal(new base_string(
        "unknown construct " + source.getClass().getName()), source);
  }

  @Override
  public analyzable process_extension(extension_construct source) {
    return source.to_analyzable();
  }

  @Override
  public analyzable process_block(block_construct source) {
    return new block_analyzer(source);
  }

  @Override
  public analyzable process_conditional(conditional_construct source) {
    return new conditional_analyzer(source);
  }

  @Override
  public analyzable process_constraint(constraint_construct source) {
    return new constraint_analyzer(source);
  }

  @Override
  public analyzable process_empty(empty_construct source) {
    return base_analyzable_action.nothing(source);
  }

  @Override
  public analyzable process_comment(comment_construct source) {
    return base_analyzable_action.nothing(source);
  }

  @Override
  public analyzable process_procedure(procedure_construct source) {
    return handle_extension(new procedure_analyzer(source), source.annotations);
  }

  @Override
  public analyzable process_list(list_construct source) {
    if (source.is_simple_grouping()) {
      return new grouping_analyzer(process(source.the_elements.first()), source);
    } else {
      return new list_initializer_analyzer(source);
    }
  }

  @Override
  public analyzable process_name(name_construct source) {
    return new resolve_analyzer(source);
  }

  @Override
  public analyzable process_import(import_construct source) {
    return new import_analyzer(source);
  }

  @Override
  public analyzable process_flavor(flavor_construct source) {
    return new flavor_analyzer(source);
  }

  @Override
  public analyzable process_operator(operator_construct source) {
    return new parameter_analyzer(source);
  }

  @Override
  public analyzable process_parameter(parameter_construct source) {
    return new parameter_analyzer(source);
  }

  @Override
  public analyzable process_return(return_construct source) {
    return new return_analyzer(source);
  }

  @Override
  public analyzable process_resolve(resolve_construct source) {
    return new resolve_analyzer(source);
  }

  @Override
  public analyzable process_supertype(supertype_construct source) {
    if (source.type_constructs.size() == 1) {
      return new supertype_analyzer(source.subtype_flavor, source.tag,
          process(source.type_constructs.first()), source);
    } else {
      return new list_analyzer(make_supertype_list(source, source), true, source);
    }
  }

  public readonly_list<analyzable> make_supertype_list(
      supertype_construct the_supertype_construct, origin source) {
    list<analyzable> result = new base_list<analyzable>();
    readonly_list<construct> supertypes = the_supertype_construct.type_constructs;

    for (int i = 0; i < supertypes.size(); ++i) {
      construct supertype = supertypes.get(i);
      result.append(new supertype_analyzer(the_supertype_construct.subtype_flavor,
          the_supertype_construct.tag, process(supertype), source));
    }

    return result;
  }

  @Override
  public analyzable process_type_declaration(type_declaration_construct source) {
    type_declaration_analyzer the_type_declaration = new type_declaration_analyzer(source);
    // TODO: this is not robust.  Must be fixed.
    if (the_type_declaration.get_kind() == type_kinds.test_suite_kind) {
      extension_kind the_extension_kind = test_suite_extension.instance.the_extension_kind;
      return the_extension_kind.make_extension(the_type_declaration,
          new modifier_construct(the_extension_kind, source));
    }
    return handle_extension(the_type_declaration, source.annotations);
  }

  @Override
  public analyzable process_type_announcement(type_announcement_construct source) {
    return new type_announcement_analyzer(source);
  }

  @Override
  public analyzable process_literal(literal_construct source) {
    return new literal_analyzer(source);
  }

  @Override
  public analyzable process_variable(variable_construct source) {
    return handle_extension(new variable_analyzer(source), source.annotations);
  }

  @Override
  public analyzable process_loop(loop_construct the_loop) {
    return new loop_analyzer(the_loop);
  }

  @Override
  public analyzable process_jump(jump_construct the_jump) {
    return new jump_analyzer(the_jump);
  }

  @Override
  public analyzable process_switch(switch_construct the_switch) {
    return new switch_analyzer(the_switch);
  }

  private @Nullable analyzable handle_extension(declaration_analyzer the_declaration,
      readonly_list<annotation_construct> annotations) {

    @Nullable extension_kind the_extension_kind = null;
    @Nullable modifier_construct the_modifier_construct = null;

    for (int i = 0; i < annotations.size(); ++i) {
      annotation_construct annotation = annotations.get(i);
      if (annotation instanceof modifier_construct) {
        modifier_kind the_kind = ((modifier_construct) annotation).the_kind;
        if (the_kind instanceof extension_kind) {
          if (the_extension_kind == null) {
            the_extension_kind = (extension_kind) the_kind;
            the_modifier_construct = (modifier_construct) annotation;
          } else {
            // TODO: report the error instead, don't panic
            utilities.panic("More than one extension for " + the_declaration);
          }
        }
      }
    }

    if (the_extension_kind != null) {
      return the_extension_kind.make_extension(the_declaration, the_modifier_construct);
    } else {
      return the_declaration;
    }
  }
}
