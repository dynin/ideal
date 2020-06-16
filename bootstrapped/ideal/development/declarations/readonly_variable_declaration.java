// Autogenerated from development/declarations/variable_declaration.i

package ideal.development.declarations;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.comments.*;
import ideal.development.modifiers.*;

import javax.annotation.Nullable;

public interface readonly_variable_declaration extends readonly_declaration, readonly_variable_id, any_variable_declaration {
  variable_category get_category();
  annotation_set annotations();
  action_name short_name();
  principal_type declared_in_type();
  type value_type();
  type reference_type();
  @Nullable analyzable get_type_analyzable();
  @Nullable analyzable initializer();
  @Nullable action init_action();
}