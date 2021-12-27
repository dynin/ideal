// Autogenerated from development/declarations/variable_category.i

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

public enum variable_category implements enum_data {
  LOCAL,
  INSTANCE,
  STATIC,
  ENUM_VALUE;
  public string to_string() {
    return new base_string(toString());
  }
}
