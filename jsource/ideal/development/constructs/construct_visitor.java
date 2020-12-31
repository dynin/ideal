/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public abstract class construct_visitor<T> implements value {
  public T process(construct c) {
    if (c instanceof block_construct) {
      return process_block((block_construct) c);
    }

    if (c instanceof constraint_construct) {
      return process_constraint((constraint_construct) c);
    }

    if (c instanceof empty_construct) {
      return process_empty((empty_construct) c);
    }

    if (c instanceof comment_construct) {
      return process_comment((comment_construct) c);
    }

    if (c instanceof extension_construct) {
      return process_extension((extension_construct) c);
    }

    if (c instanceof flavor_construct) {
      return process_flavor((flavor_construct) c);
    }

    if (c instanceof procedure_construct) {
      return process_procedure((procedure_construct) c);
    }

    if (c instanceof list_construct) {
      return process_list((list_construct) c);
    }

    if (c instanceof name_construct) {
      return process_name((name_construct) c);
    }

    if (c instanceof conditional_construct) {
      return process_conditional((conditional_construct) c);
    }

    if (c instanceof import_construct) {
      return process_import((import_construct) c);
    }

    if (c instanceof modifier_construct) {
      return process_modifier((modifier_construct) c);
    }

    if (c instanceof operator_construct) {
      return process_operator((operator_construct) c);
    }

    if (c instanceof parameter_construct) {
      return process_parameter((parameter_construct) c);
    }

    if (c instanceof resolve_construct) {
      return process_resolve((resolve_construct) c);
    }

    if (c instanceof return_construct) {
      return process_return((return_construct) c);
    }

    if (c instanceof supertype_construct) {
      return process_supertype((supertype_construct) c);
    }

    if (c instanceof type_declaration_construct) {
      return process_type_declaration((type_declaration_construct) c);
    }

    if (c instanceof type_announcement_construct) {
      return process_type_announcement((type_announcement_construct) c);
    }

    if (c instanceof literal_construct) {
      return process_literal((literal_construct) c);
    }

    if (c instanceof variable_construct) {
      return process_variable((variable_construct) c);
    }

    if (c instanceof loop_construct) {
      return process_loop((loop_construct) c);
    }

    if (c instanceof jump_construct) {
      return process_jump((jump_construct) c);
    }

    if (c == null) {
      throw new RuntimeException("null construct in visitor");
    }

    throw new RuntimeException("unknown construct type in " +
        "construct_visitor.visit(): " + c.getClass());
  }

  public abstract T process_default(construct c);

  public T process_block(block_construct c) {
    return process_default(c);
  }

  public T process_conditional(conditional_construct c) {
    return process_default(c);
  }

  public T process_constraint(constraint_construct c) {
    return process_default(c);
  }

  public T process_empty(empty_construct c) {
    return process_default(c);
  }

  public T process_comment(comment_construct c) {
    return process_default(c);
  }

  public abstract T process_extension(extension_construct c);

  public T process_flavor(flavor_construct c) {
    return process_default(c);
  }

  public T process_procedure(procedure_construct c) {
    return process_default(c);
  }

  public T process_list(list_construct c) {
    return process_default(c);
  }

  public T process_name(name_construct c) {
    return process_default(c);
  }

  public T process_import(import_construct c) {
    return process_default(c);
  }

  public T process_modifier(modifier_construct c) {
    return process_default(c);
  }

  public T process_operator(operator_construct c) {
    return process_default(c);
  }

  public T process_parameter(parameter_construct c) {
    return process_default(c);
  }

  public T process_resolve(resolve_construct c) {
    return process_default(c);
  }

  public T process_return(return_construct c) {
    return process_default(c);
  }

  public T process_supertype(supertype_construct c) {
    return process_default(c);
  }

  public T process_type_declaration(type_declaration_construct c) {
    return process_default(c);
  }

  public T process_type_announcement(type_announcement_construct c) {
    return process_default(c);
  }

  public T process_literal(literal_construct c) {
    return process_default(c);
  }

  public T process_variable(variable_construct c) {
    return process_default(c);
  }

  public T process_loop(loop_construct c) {
    return process_default(c);
  }

  public T process_jump(jump_construct c) {
    return process_default(c);
  }
}
