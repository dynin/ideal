/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.experiment.mini;

import static ideal.experiment.mini.bootstrapped.*;
import static ideal.experiment.mini.library.*;

public abstract class construct_dispatch<result> implements function<result, construct> {

  @Override
  public result call(construct the_construct) {
    if (the_construct instanceof identifier) {
      return call_identifier((identifier) the_construct);
    }

    if (the_construct instanceof operator) {
      return call_operator((operator) the_construct);
    }

    if (the_construct instanceof string_literal) {
      return call_string_literal((string_literal) the_construct);
    }

    if (the_construct instanceof parameter_construct) {
      return call_parameter_construct((parameter_construct) the_construct);
    }

    if (the_construct instanceof modifier_construct) {
      return call_modifier_construct((modifier_construct) the_construct);
    }

    if (the_construct instanceof s_expression) {
      return call_s_expression((s_expression) the_construct);
    }

    if (the_construct instanceof block_construct) {
      return call_block_construct((block_construct) the_construct);
    }

    if (the_construct instanceof conditional_construct) {
      return call_conditional_construct((conditional_construct) the_construct);
    }

    if (the_construct instanceof return_construct) {
      return call_return_construct((return_construct) the_construct);
    }

    if (the_construct instanceof variable_construct) {
      return call_variable_construct((variable_construct) the_construct);
    }

    if (the_construct instanceof procedure_construct) {
      return call_procedure_construct((procedure_construct) the_construct);
    }

    if (the_construct instanceof dispatch_construct) {
      return call_dispatch_construct((dispatch_construct) the_construct);
    }

    if (the_construct instanceof supertype_construct) {
      return call_supertype_construct((supertype_construct) the_construct);
    }

    if (the_construct instanceof type_construct) {
      return call_type_construct((type_construct) the_construct);
    }

    return call_construct(the_construct);
  }

  public result call_construct(construct the_construct) {
    throw new Error("Unknown construct type for " + the_construct);
  }

  public result call_identifier(identifier the_identifier) {
    return call_construct(the_identifier);
  }

  public result call_operator(operator the_operator) {
    return call_construct(the_operator);
  }

  public result call_string_literal(string_literal the_string_literal) {
    return call_construct(the_string_literal);
  }

  public result call_parameter_construct(parameter_construct the_parameter_construct) {
    return call_construct(the_parameter_construct);
  }

  public result call_modifier_construct(modifier_construct the_modifier_construct) {
    return call_construct(the_modifier_construct);
  }

  public result call_s_expression(s_expression the_s_expression) {
    return call_construct(the_s_expression);
  }

  public result call_block_construct(block_construct the_block_construct) {
    return call_construct(the_block_construct);
  }

  public result call_conditional_construct(conditional_construct the_conditional_construct) {
    return call_construct(the_conditional_construct);
  }

  public result call_return_construct(return_construct the_return_construct) {
    return call_construct(the_return_construct);
  }

  public result call_variable_construct(variable_construct the_variable_construct) {
    return call_construct(the_variable_construct);
  }

  public result call_procedure_construct(procedure_construct the_procedure_construct) {
    return call_construct(the_procedure_construct);
  }

  public result call_dispatch_construct(dispatch_construct the_dispatch_construct) {
    return call_construct(the_dispatch_construct);
  }

  public result call_supertype_construct(supertype_construct the_supertype_construct) {
    return call_construct(the_supertype_construct);
  }

  public result call_type_construct(type_construct the_type_construct) {
    return call_construct(the_type_construct);
  }
}
