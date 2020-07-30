/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.extensions.grouping_analyzer;
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
    procedure_analyzer result = new procedure_analyzer(source);
    // TODO: switch to using annotation set.
    // TODO: handle more than one extension on the same procedure_analyzer.
    @Nullable readonly_list<annotation_construct> annotations = result.annotations_list();
    if (result != null) {
      for (int i = 0; i < annotations.size(); ++i) {
        annotation_construct annotation = annotations.get(i);
        if (annotation instanceof modifier_construct) {
          modifier_kind the_kind = ((modifier_construct) annotation).the_kind;
          if (the_kind instanceof extension_modifier_kind) {
            return ((extension_modifier_kind) the_kind).make_extension(result,
                (modifier_construct) annotation);
          }
        }
      }
    }

    return result;
  }

  @Override
  public analyzable process_list(list_construct source) {
    if (source.is_simple_grouping()) {
      return new grouping_analyzer(process(source.elements.first()), source);
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
    if (source.types.size() == 1) {
      return new supertype_analyzer(source.subtype_flavor, source.tag,
          process(source.types.first()), source);
    } else {
      return new declaration_list(make_supertype_list(source, source), source);
    }
  }

  public readonly_list<analyzable> make_supertype_list(
      supertype_construct the_supertype_construct, origin source) {
    list<analyzable> result = new base_list<analyzable>();
    readonly_list<construct> supertypes = the_supertype_construct.types;

    for (int i = 0; i < supertypes.size(); ++i) {
      construct supertype = supertypes.get(i);
      result.append(new supertype_analyzer(the_supertype_construct.subtype_flavor,
          the_supertype_construct.tag, process(supertype), source));
    }

    return result;
  }

  @Override
  public analyzable process_type_declaration(type_declaration_construct source) {
    type_declaration_analyzer result = new type_declaration_analyzer(source);
    // TODO: switch to using annotation set.
    // TODO: handle more than one extension on the same type_declaration_analyzer.
    @Nullable readonly_list<annotation_construct> annotations = result.annotations_list();
    if (result != null) {
      for (int i = 0; i < annotations.size(); ++i) {
        annotation_construct annotation = annotations.get(i);
        if (annotation instanceof modifier_construct) {
          modifier_kind the_kind = ((modifier_construct) annotation).the_kind;
          if (the_kind instanceof extension_modifier_kind) {
            return ((extension_modifier_kind) the_kind).make_extension(result,
                (modifier_construct) annotation);
          }
        }
      }
    }

    return result;
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
    variable_analyzer result = new variable_analyzer(source);
    // TODO: switch to using annotation set.
    // TODO: handle more than one extension on the same variable_analyzer.
    @Nullable readonly_list<annotation_construct> annotations = result.annotations_list();
    if (result != null) {
      for (int i = 0; i < annotations.size(); ++i) {
        annotation_construct annotation = annotations.get(i);
        if (annotation instanceof modifier_construct) {
          modifier_kind the_kind = ((modifier_construct) annotation).the_kind;
          if (the_kind instanceof extension_modifier_kind) {
            return ((extension_modifier_kind) the_kind).make_extension(result,
                (modifier_construct) annotation);
          }
        }
      }
    }

    return result;
  }

  @Override
  public analyzable process_loop(loop_construct the_loop) {
    return new loop_analyzer(the_loop);
  }

  @Override
  public analyzable process_jump(jump_construct the_jump) {
    return new jump_analyzer(the_jump);
  }
}
