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

import javax.annotation.Nullable;

public interface readonly_procedure_declaration extends readonly_declaration, any_procedure_declaration {
  simple_name original_name();
  action_name short_name();
  annotation_set annotations();
  procedure_category get_category();
  type_flavor get_flavor();
  type get_return_type();
  principal_type declared_in_type();
  readonly_list<type> get_argument_types();
  type get_procedure_type();
  readonly_list<variable_declaration> get_parameter_variables();
  boolean overrides_variable();
  readonly_list<declaration> get_overriden();
  @Nullable action procedure_action();
  @Nullable analyzable get_return();
  @Nullable analyzable get_body();
  @Nullable action get_body_action();
  @Nullable variable_declaration get_this_declaration();
}
