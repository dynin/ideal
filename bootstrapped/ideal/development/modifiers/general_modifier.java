// Autogenerated from development/modifiers/general_modifier.i

package ideal.development.modifiers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class general_modifier {
  public final static modifier_kind static_modifier = new base_modifier_kind(new base_string("static"));
  public final static modifier_kind final_modifier = new base_modifier_kind(new base_string("final"));
  public final static modifier_kind pure_modifier = new base_modifier_kind(new base_string("pure"));
  public final static modifier_kind var_modifier = new base_modifier_kind(new base_string("var"));
  public final static modifier_kind abstract_modifier = new base_modifier_kind(new base_string("abstract"));
  public final static modifier_kind implicit_modifier = new base_modifier_kind(new base_string("implicit"));
  public final static modifier_kind explicit_modifier = new base_modifier_kind(new base_string("explicit"));
  public final static modifier_kind not_yet_implemented_modifier = new base_modifier_kind(new base_string("not_yet_implemented"));
  public final static modifier_kind varargs_modifier = new base_modifier_kind(new base_string("varargs"));
  public final static modifier_kind override_modifier = new base_modifier_kind(new base_string("override"));
  public final static modifier_kind overload_modifier = new base_modifier_kind(new base_string("overload"));
  public final static modifier_kind implement_modifier = new base_modifier_kind(new base_string("implement"));
  public final static modifier_kind noreturn_modifier = new base_modifier_kind(new base_string("noreturn"));
  public final static modifier_kind testcase_modifier = new base_modifier_kind(new base_string("testcase"));
  public final static modifier_kind dont_display_modifier = new base_modifier_kind(new base_string("dont_display"));
  public final static modifier_kind synchronized_modifier = new base_modifier_kind(new base_string("synchronized"));
  public final static modifier_kind volatile_modifier = new base_modifier_kind(new base_string("volatile"));
  public final static modifier_kind transient_modifier = new base_modifier_kind(new base_string("transient"));
  public final static modifier_kind native_modifier = new base_modifier_kind(new base_string("native"));
  public final static modifier_kind nullable_modifier = new base_modifier_kind(new base_string("nullable"));
  public final static set<modifier_kind> supported_by_java = new hash_set<modifier_kind>();
  public final static set<modifier_kind> java_annotations = new hash_set<modifier_kind>();
  public final static set<modifier_kind> supported_by_javascript = new hash_set<modifier_kind>();
  static {
    supported_by_java.add_all(new base_immutable_list<modifier_kind>(new ideal.machine.elements.array<modifier_kind>(new modifier_kind[]{ access_modifier.public_modifier, access_modifier.protected_modifier, access_modifier.private_modifier, static_modifier, final_modifier, abstract_modifier, synchronized_modifier, volatile_modifier, transient_modifier, native_modifier })));
    java_annotations.add_all(new base_immutable_list<modifier_kind>(new ideal.machine.elements.array<modifier_kind>(new modifier_kind[]{ override_modifier, nullable_modifier, dont_display_modifier })));
    supported_by_java.add_all(java_annotations);
    supported_by_javascript.add_all(new base_immutable_list<modifier_kind>(new ideal.machine.elements.array<modifier_kind>(new modifier_kind[]{ var_modifier })));
  }
}