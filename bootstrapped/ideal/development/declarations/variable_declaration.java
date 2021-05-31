// Autogenerated from development/declarations/variable_declaration.i

package ideal.development.declarations;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.futures.*;
import ideal.development.names.*;
import ideal.development.comments.*;
import ideal.development.modifiers.*;
import ideal.development.comments.documentation;

public interface variable_declaration extends named_declaration, variable_id, readonly_variable_declaration, writeonly_variable_declaration {
  variable_declaration specialize(specialization_context context, principal_type new_parent);
}
