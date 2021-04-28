-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

abstract class construct_visitor[any value return_value] {
  implements value;

  return_value process(construct c) {
    if (c is block_construct) {
      return process_block(c);
    }

    if (c is constraint_construct) {
      return process_constraint(c);
    }

    if (c is empty_construct) {
      return process_empty(c);
    }

    if (c is comment_construct) {
      return process_comment(c);
    }

    if (c is extension_construct) {
      return process_extension(c);
    }

    if (c is flavor_construct) {
      return process_flavor(c);
    }

    if (c is procedure_construct) {
      return process_procedure(c);
    }

    if (c is list_construct) {
      return process_list(c);
    }

    if (c is name_construct) {
      return process_name(c);
    }

    if (c is conditional_construct) {
      return process_conditional(c);
    }

    if (c is import_construct) {
      return process_import(c);
    }

    if (c is modifier_construct) {
      return process_modifier(c);
    }

    if (c is operator_construct) {
      return process_operator(c);
    }

    if (c is parameter_construct) {
      return process_parameter(c);
    }

    if (c is resolve_construct) {
      return process_resolve(c);
    }

    if (c is return_construct) {
      return process_return(c);
    }

    if (c is supertype_construct) {
      return process_supertype(c);
    }

    if (c is type_declaration_construct) {
      return process_type_declaration(c);
    }

    if (c is type_announcement_construct) {
      return process_type_announcement(c);
    }

    if (c is literal_construct) {
      return process_literal(c);
    }

    if (c is variable_construct) {
      return process_variable(c);
    }

    if (c is loop_construct) {
      return process_loop(c);
    }

    if (c is jump_construct) {
      return process_jump(c);
    }

    if (c is null) {
      utilities.panic("null construct in visitor");
    }

    utilities.panic("unknown construct type in construct_visitor.visit(): " ++ c);
  }

  abstract return_value process_default(construct c);

  return_value process_block(block_construct c) {
    return process_default(c);
  }

  return_value process_conditional(conditional_construct c) {
    return process_default(c);
  }

  return_value process_constraint(constraint_construct c) {
    return process_default(c);
  }

  return_value process_empty(empty_construct c) {
    return process_default(c);
  }

  return_value process_comment(comment_construct c) {
    return process_default(c);
  }

  abstract return_value process_extension(extension_construct c);

  return_value process_flavor(flavor_construct c) {
    return process_default(c);
  }

  return_value process_procedure(procedure_construct c) {
    return process_default(c);
  }

  return_value process_list(list_construct c) {
    return process_default(c);
  }

  return_value process_name(name_construct c) {
    return process_default(c);
  }

  return_value process_import(import_construct c) {
    return process_default(c);
  }

  return_value process_modifier(modifier_construct c) {
    return process_default(c);
  }

  return_value process_operator(operator_construct c) {
    return process_default(c);
  }

  return_value process_parameter(parameter_construct c) {
    return process_default(c);
  }

  return_value process_resolve(resolve_construct c) {
    return process_default(c);
  }

  return_value process_return(return_construct c) {
    return process_default(c);
  }

  return_value process_supertype(supertype_construct c) {
    return process_default(c);
  }

  return_value process_type_declaration(type_declaration_construct c) {
    return process_default(c);
  }

  return_value process_type_announcement(type_announcement_construct c) {
    return process_default(c);
  }

  return_value process_literal(literal_construct c) {
    return process_default(c);
  }

  return_value process_variable(variable_construct c) {
    return process_default(c);
  }

  return_value process_loop(loop_construct c) {
    return process_default(c);
  }

  return_value process_jump(jump_construct c) {
    return process_default(c);
  }
}
