// Autogenerated from development/values/procedure_value.i

package ideal.development.values;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.origins.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.jumps.*;
import ideal.development.notifications.*;
import ideal.development.flags.*;

import javax.annotation.Nullable;

public interface readonly_procedure_value extends readonly_abstract_value, readonly_value_wrapper, any_procedure_value {
  action_name name();
  type type_bound();
  @Nullable declaration get_declaration();
  boolean has_this_argument();
  boolean supports_parameters(action_parameters parameters, action_context context);
}
