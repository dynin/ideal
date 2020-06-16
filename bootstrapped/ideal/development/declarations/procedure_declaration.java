// Autogenerated from development/declarations/procedure_declaration.i

package ideal.development.declarations;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.comments.*;
import ideal.development.modifiers.*;

public interface procedure_declaration extends declaration, readonly_procedure_declaration, writeonly_procedure_declaration {
  procedure_declaration specialize(specialization_context context, principal_type new_parent);
}