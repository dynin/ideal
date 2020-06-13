/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.transformers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;
import ideal.development.analyzers.*;
import ideal.development.notifications.error_signal;
import ideal.development.extensions.extension_analyzer;
import ideal.development.targets.target_declaration;

public abstract class analyzable_visitor<T> implements value {

  public T process(analyzable_or_declaration the_analyzable) {
    if (the_analyzable instanceof analyzable_action) {
      return process_analyzable_action((analyzable_action) the_analyzable);
    }

    if (the_analyzable instanceof block_declaration) {
      return process_block((block_declaration) the_analyzable);
    }

    if (the_analyzable instanceof conditional_analyzer) {
      return process_conditional((conditional_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof constraint_analyzer) {
      return process_constraint((constraint_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof declaration_list_analyzer) {
      return process_declaration_list((declaration_list_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof enum_value_analyzer) {
      return process_enum_value((enum_value_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof error_signal) {
      return process_error_signal((error_signal) the_analyzable);
    }

    if (the_analyzable instanceof extension_analyzer) {
      return process_extension((extension_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof flavor_analyzer) {
      return process_flavor((flavor_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof import_analyzer) {
      return process_import((import_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof jump_analyzer) {
      return process_jump((jump_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof list_initializer_analyzer) {
      return process_list_initializer((list_initializer_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof literal_analyzer) {
      return process_literal((literal_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof loop_analyzer) {
      return process_loop((loop_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof parameter_analyzer) {
      return process_parameter((parameter_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof procedure_declaration) {
      return process_procedure((procedure_declaration) the_analyzable);
    }

    if (the_analyzable instanceof resolve_analyzer) {
      return process_resolve((resolve_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof return_analyzer) {
      return process_return((return_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof statement_list_analyzer) {
      return process_statement_list((statement_list_analyzer) the_analyzable);
    }

    if (the_analyzable instanceof supertype_declaration) {
      return process_supertype((supertype_declaration) the_analyzable);
    }

    if (the_analyzable instanceof target_declaration) {
      return process_target((target_declaration) the_analyzable);
    }

    // NOTE: this is before type_declaration
    if (the_analyzable instanceof type_announcement) {
      return process_type_announcement((type_announcement) the_analyzable);
    }

    // NOTE: this is before type_declaration
    if (the_analyzable instanceof type_parameter_declaration) {
      return process_type_parameter((type_parameter_declaration) the_analyzable);
    }

    if (the_analyzable instanceof type_declaration) {
      return process_type((type_declaration) the_analyzable);
    }

    if (the_analyzable instanceof variable_declaration) {
      return process_variable((variable_declaration) the_analyzable);
    }

    if (the_analyzable == null) {
      utilities.panic("Null analyzable in visitor");
    }

    utilities.panic("Unknown analyzable in " +
        "analyzable_visitor.visit(): " + the_analyzable.getClass());
    return null;
  }

  public abstract T process_default(analyzable_or_declaration the_analyzable);

  public T process_analyzable_action(analyzable_action the_analyzable_action) {
    return process_default(the_analyzable_action);
  }

  public T process_block(block_declaration the_block) {
    return process_default(the_block);
  }

  public T process_conditional(conditional_analyzer the_conditional) {
    return process_default(the_conditional);
  }

  public T process_constraint(constraint_analyzer the_constraint) {
    return process_default(the_constraint);
  }

  public T process_declaration_list(declaration_list_analyzer the_declaration_list) {
    return process_default(the_declaration_list);
  }

  public T process_enum_value(enum_value_analyzer the_enum_value) {
    return process_default(the_enum_value);
  }

  public T process_error_signal(error_signal the_error_signal) {
    return process_default(the_error_signal);
  }

  public T process_extension(extension_analyzer the_extension) {
    return process_default(the_extension);
  }

  public T process_flavor(flavor_analyzer the_flavor) {
    return process_default(the_flavor);
  }

  public T process_import(import_analyzer the_import) {
    return process_default(the_import);
  }

  public T process_jump(jump_analyzer the_jump) {
    return process_default(the_jump);
  }

  public T process_list_initializer(list_initializer_analyzer the_list_initializer) {
    return process_default(the_list_initializer);
  }

  public T process_literal(literal_analyzer the_literal) {
    return process_default(the_literal);
  }

  public T process_loop(loop_analyzer the_loop) {
    return process_default(the_loop);
  }

  public T process_parameter(parameter_analyzer the_parameter) {
    return process_default(the_parameter);
  }

  public T process_procedure(procedure_declaration the_procedure) {
    return process_default(the_procedure);
  }

  public T process_resolve(resolve_analyzer the_resolve) {
    return process_default(the_resolve);
  }

  public T process_return(return_analyzer the_return) {
    return process_default(the_return);
  }

  public T process_statement_list(statement_list_analyzer the_statement_list) {
    return process_default(the_statement_list);
  }

  public T process_supertype(supertype_declaration the_supertype) {
    return process_default(the_supertype);
  }

  public T process_target(target_declaration the_target) {
    return process_default(the_target);
  }

  public T process_type_announcement(type_announcement the_type_announcement) {
    return process_default(the_type_announcement);
  }

  public T process_type(type_declaration the_type) {
    return process_default(the_type);
  }

  public T process_type_parameter(type_parameter_declaration the_type_parameter) {
    return process_default(the_type_parameter);
  }

  public T process_variable(variable_declaration the_variable) {
    return process_default(the_variable);
  }
}
