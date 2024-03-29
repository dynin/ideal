// Autogenerated from development/declarations/type_announcement.i

package ideal.development.declarations;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.comments.*;
import ideal.development.modifiers.*;
import ideal.development.comments.documentation;

public interface readonly_type_announcement extends readonly_declaration, any_type_announcement {
  action_name short_name();
  kind get_kind();
  annotation_set annotations();
  principal_type get_declared_type();
  type_declaration get_type_declaration();
  readonly_list<declaration> external_declarations();
}
