/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.values;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.flavors.*;

public class procedure_util {

  private procedure_util() { }

  public static type make_procedure_type(boolean is_function, type return_type,
      type first_argument_type, type second_argument_type) {
    list<abstract_value> parameters = new base_list<abstract_value>();
    parameters.append(return_type);
    parameters.append(first_argument_type);
    parameters.append(second_argument_type);
    return do_make_procedure(is_function, new type_parameters(parameters));
  }

  public static type make_procedure_type(boolean is_function, type return_type,
      type argument_type) {
    list<abstract_value> parameters = new base_list<abstract_value>();
    parameters.append(return_type);
    parameters.append(argument_type);
    return do_make_procedure(is_function, new type_parameters(parameters));
  }

  public static type do_make_procedure(boolean is_function, type_parameters parameters) {
    common_library library = common_library.get_instance();
    master_type the_master_type = is_function ? library.function_type() : library.procedure_type();
    return the_master_type.bind_parameters(parameters).get_flavored(flavor.immutable_flavor);
  }
}
