// Autogenerated from development/declarations/annotation_library.i

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
import static ideal.development.modifiers.access_modifier.*;
import static ideal.development.modifiers.general_modifier.*;

public class annotation_library {
  public static final annotation_set PUBLIC_MODIFIERS = annotation_library.make_annotations(access_modifier.public_modifier, new empty<modifier_kind>());
  public static final annotation_set PUBLIC_OVERLOAD_MODIFIERS = annotation_library.make_annotations(access_modifier.public_modifier, new base_immutable_list<modifier_kind>(new ideal.machine.elements.array<modifier_kind>(new modifier_kind[]{ general_modifier.overload_modifier })));
  public static final annotation_set PUBLIC_OVERRIDE_MODIFIERS = annotation_library.make_annotations(access_modifier.public_modifier, new base_immutable_list<modifier_kind>(new ideal.machine.elements.array<modifier_kind>(new modifier_kind[]{ general_modifier.override_modifier })));
  public static final annotation_set PRIVATE_MODIFIERS = annotation_library.make_annotations(access_modifier.private_modifier, new empty<modifier_kind>());
  public static final annotation_set PRIVATE_VAR_MODIFIERS = annotation_library.make_annotations(access_modifier.private_modifier, new base_immutable_list<modifier_kind>(new ideal.machine.elements.array<modifier_kind>(new modifier_kind[]{ general_modifier.var_modifier })));
  public static final annotation_set PRIVATE_FINAL_MODIFIERS = annotation_library.make_annotations(access_modifier.private_modifier, new base_immutable_list<modifier_kind>(new ideal.machine.elements.array<modifier_kind>(new modifier_kind[]{ general_modifier.final_modifier })));
  public static final annotation_set PRIVATE_STATIC_MODIFIERS = annotation_library.make_annotations(access_modifier.private_modifier, new base_immutable_list<modifier_kind>(new ideal.machine.elements.array<modifier_kind>(new modifier_kind[]{ general_modifier.static_modifier })));
  public static final annotation_set PRIVATE_STATIC_VAR_MODIFIERS = annotation_library.make_annotations(access_modifier.private_modifier, new base_immutable_list<modifier_kind>(new ideal.machine.elements.array<modifier_kind>(new modifier_kind[]{ general_modifier.static_modifier, general_modifier.var_modifier })));
  public static annotation_set make_annotations(final access_modifier the_access_modifier, final readonly_list<modifier_kind> the_modifiers) {
    final hash_set<modifier_kind> modifier_set = new hash_set<modifier_kind>();
    modifier_set.add_all(the_modifiers);
    return new base_annotation_set(the_access_modifier, null, modifier_set.frozen_copy(), null, new empty<origin>());
  }
}