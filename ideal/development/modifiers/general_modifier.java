/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.modifiers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class general_modifier {
  public static modifier_kind static_modifier = new base_modifier_kind("static");
  public static modifier_kind final_modifier = new base_modifier_kind("final");

  public static modifier_kind pure_modifier = new base_modifier_kind("pure");

  public static modifier_kind var_modifier = new base_modifier_kind("var");
  public static modifier_kind abstract_modifier = new base_modifier_kind("abstract");
  public static modifier_kind implicit_modifier = new base_modifier_kind("implicit");

  //public static modifier_kind documentation_modifier = new base_modifier_kind("documentation");

  public static modifier_kind not_yet_implemented_modifier =
      new base_modifier_kind("not_yet_implemented");

  public static modifier_kind varargs_modifier = new base_modifier_kind("varargs");

  public static modifier_kind override_modifier = new base_modifier_kind("override");
  public static modifier_kind overload_modifier = new base_modifier_kind("overload");
  public static modifier_kind implement_modifier = new base_modifier_kind("implement");
  public static modifier_kind noreturn_modifier = new base_modifier_kind("noreturn");
  public static modifier_kind testcase_modifier = new base_modifier_kind("testcase");

  // For interop with Java.
  public static modifier_kind synchronized_modifier = new base_modifier_kind("synchronized");
  public static modifier_kind volatile_modifier = new base_modifier_kind("volatile");
  public static modifier_kind transient_modifier = new base_modifier_kind("transient");
  public static modifier_kind native_modifier = new base_modifier_kind("native");
  public static modifier_kind nullable_modifier = new base_modifier_kind("nullable");

  public static set<modifier_kind> supported_by_java;
  public static set<modifier_kind> java_annotations;
  public static set<modifier_kind> supported_by_javascript;

  static {
    supported_by_java = new hash_set<modifier_kind>();
    supported_by_java.add_all(to_set(new modifier_kind[] {
        // Supported access modifiers
        access_modifier.public_modifier,
        access_modifier.protected_modifier,
        access_modifier.private_modifier,

        // Supported general modifiers
        static_modifier,
        final_modifier,
        abstract_modifier,
        // documentation_modifier,
        synchronized_modifier,
        volatile_modifier,
        transient_modifier,
        native_modifier
    }));

    java_annotations = new hash_set<modifier_kind>();
    java_annotations.add_all(to_set(new modifier_kind[] {
        override_modifier,
        nullable_modifier
    }));
    supported_by_java.add_all(java_annotations);

    supported_by_javascript = new hash_set<modifier_kind>();
    supported_by_javascript.add_all(to_set(new modifier_kind[] {
        var_modifier,
        // Hmmm...
        // documentation_modifier,
    }));
  }

  // TODO: use list constants...
  private static set<modifier_kind> to_set(modifier_kind[] modifiers) {
    set<modifier_kind> result = new hash_set<modifier_kind>();
    for (int i = 0; i < modifiers.length; ++i) {
      result.add(modifiers[i]);
    }
    return result;
  }
}
