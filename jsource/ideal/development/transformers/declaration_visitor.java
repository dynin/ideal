/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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

public abstract class declaration_visitor<T> implements value {

  public T process(declaration the_declaration) {
    if (the_declaration instanceof block_declaration) {
      return process_block((block_declaration) the_declaration);
    }

    if (the_declaration instanceof list_analyzer) {
      return process_list((list_analyzer) the_declaration);
    }

    if (the_declaration instanceof import_declaration) {
      return process_import((import_declaration) the_declaration);
    }

    if (the_declaration instanceof procedure_declaration) {
      return process_procedure((procedure_declaration) the_declaration);
    }

    if (the_declaration instanceof supertype_declaration) {
      return process_supertype((supertype_declaration) the_declaration);
    }

    if (the_declaration instanceof target_declaration) {
      return process_target((target_declaration) the_declaration);
    }

    // NOTE: this is before type_declaration
    if (the_declaration instanceof type_announcement) {
      return process_type_announcement((type_announcement) the_declaration);
    }

    // NOTE: this is before type_declaration
    if (the_declaration instanceof type_parameter_declaration) {
      return process_type_parameter((type_parameter_declaration) the_declaration);
    }

    if (the_declaration instanceof type_declaration) {
      return process_type((type_declaration) the_declaration);
    }

    if (the_declaration instanceof variable_declaration) {
      return process_variable((variable_declaration) the_declaration);
    }

    if (the_declaration == null) {
      utilities.panic("Null declaration in visitor");
    }

    utilities.panic("Unknown declaration in " +
        "declaration_visitor.visit(): " + the_declaration.getClass());
    return null;
  }

  public abstract T process_default(declaration the_declaration);

  public T process_block(block_declaration the_block) {
    return process_default(the_block);
  }

  public T process_list(list_analyzer the_list_analyzer) {
    return process_default(the_list_analyzer);
  }

  public T process_import(import_declaration the_import) {
    return process_default(the_import);
  }

  public T process_procedure(procedure_declaration the_procedure) {
    return process_default(the_procedure);
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
