-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- General modifiers used in ideal and other languages.
class general_modifier {
  -- TODO: drop explicit type (modifier_kind) when list promotion works.
  static modifier_kind static_modifier : base_modifier_kind.new("static");
  static modifier_kind final_modifier : base_modifier_kind.new("final");

  static modifier_kind pure_modifier : base_modifier_kind.new("pure");

  static modifier_kind var_modifier : base_modifier_kind.new("var");
  static modifier_kind abstract_modifier : base_modifier_kind.new("abstract");
  static modifier_kind implicit_modifier : base_modifier_kind.new("implicit");

  --static modifier_kind documentation_modifier : base_modifier_kind.new("documentation");

  static modifier_kind not_yet_implemented_modifier : base_modifier_kind.new("not_yet_implemented");

  static modifier_kind varargs_modifier : base_modifier_kind.new("varargs");

  static modifier_kind override_modifier : base_modifier_kind.new("override");
  static modifier_kind overload_modifier : base_modifier_kind.new("overload");
  static modifier_kind implement_modifier : base_modifier_kind.new("implement");
  static modifier_kind noreturn_modifier : base_modifier_kind.new("noreturn");
  static modifier_kind testcase_modifier : base_modifier_kind.new("testcase");
  static modifier_kind dont_display_modifier : base_modifier_kind.new("dont_display");

  -- For interop with Java.
  static modifier_kind synchronized_modifier : base_modifier_kind.new("synchronized");
  static modifier_kind volatile_modifier : base_modifier_kind.new("volatile");
  static modifier_kind transient_modifier : base_modifier_kind.new("transient");
  static modifier_kind native_modifier : base_modifier_kind.new("native");
  static modifier_kind nullable_modifier : base_modifier_kind.new("nullable");

  static set[modifier_kind] supported_by_java : hash_set[modifier_kind].new();
  static set[modifier_kind] java_annotations : hash_set[modifier_kind].new();
  static set[modifier_kind] supported_by_javascript : hash_set[modifier_kind].new();

  static {
    supported_by_java.add_all([
        -- Supported access modifiers
        access_modifier.public_modifier,
        access_modifier.protected_modifier,
        access_modifier.private_modifier,

        -- Supported general modifiers
        static_modifier,
        final_modifier,
        abstract_modifier,
        -- documentation_modifier,
        synchronized_modifier,
        volatile_modifier,
        transient_modifier,
        native_modifier
    ]);

    java_annotations.add_all([
        override_modifier,
        nullable_modifier,
        dont_display_modifier
    ]);
    supported_by_java.add_all(java_annotations);

    supported_by_javascript.add_all([
        var_modifier,
        -- Hmmm...
        -- documentation_modifier,
    ]);
  }
}
