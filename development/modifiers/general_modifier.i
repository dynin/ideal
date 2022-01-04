-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- General modifiers used in ideal and other languages.
namespace general_modifier {
  -- TODO: drop explicit type (modifier_kind) when list promotion works.
  modifier_kind static_modifier : base_modifier_kind.new("static");
  modifier_kind final_modifier : base_modifier_kind.new("final");

  modifier_kind pure_modifier : base_modifier_kind.new("pure");

  modifier_kind var_modifier : base_modifier_kind.new("var");
  modifier_kind the_modifier : base_modifier_kind.new("the");
  modifier_kind abstract_modifier : base_modifier_kind.new("abstract");
  modifier_kind implicit_modifier : base_modifier_kind.new("implicit");
  modifier_kind explicit_modifier : base_modifier_kind.new("explicit");

  --modifier_kind documentation_modifier : base_modifier_kind.new("documentation");

  modifier_kind synthetic_modifier : base_modifier_kind.new("synthetic");

  modifier_kind varargs_modifier : base_modifier_kind.new("varargs");

  modifier_kind mutable_var_modifier : base_modifier_kind.new("mutable_var");
  modifier_kind override_modifier : base_modifier_kind.new("override");
  modifier_kind overload_modifier : base_modifier_kind.new("overload");
  modifier_kind implement_modifier : base_modifier_kind.new("implement");
  modifier_kind noreturn_modifier : base_modifier_kind.new("noreturn");
  modifier_kind dont_display_modifier : base_modifier_kind.new("dont_display");

  -- For interop with Java.
  modifier_kind synchronized_modifier : base_modifier_kind.new("synchronized");
  modifier_kind volatile_modifier : base_modifier_kind.new("volatile");
  modifier_kind transient_modifier : base_modifier_kind.new("transient");
  modifier_kind native_modifier : base_modifier_kind.new("native");
  modifier_kind nullable_modifier : base_modifier_kind.new("nullable");
}
