// Autogenerated from development/declarations/supertype_declaration.i

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

public interface readonly_supertype_declaration extends readonly_declaration, any_supertype_declaration {
  annotation_set annotations();
  type_flavor subtype_flavor();
  subtype_tag tag();
  readonly_list<type> super_types();
}
