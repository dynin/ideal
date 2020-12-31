-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

-- TODO: merge this with special_token_name.
class special_name {
  extends debuggable;
  implements action_name;

  static special_name ROOT : special_name.new("root");

  static special_name PROMOTION : special_name.new("promotion");
  static special_name TYPE_ALIAS : special_name.new("type_alias");
  static special_name THIS : special_name.new(keywords.THIS);
  static special_name SUPER : special_name.new(keywords.SUPER);
  static special_name NEW : special_name.new(keywords.NEW);
  static special_name IMPLICIT_CALL : special_name.new("implicit_call");
  static special_name SUPERTYPE : special_name.new("supertype");
  static special_name CONSTRUCTOR : special_name.new("constructor");

  static special_name THIS_CONSTRUCTOR : special_name.new("this_constructor");
  static special_name SUPER_CONSTRUCTOR : special_name.new("super_constructor");

  -- Used to a name the return pseudo-variable
  static final special_name RETURN : special_name.new(keywords.RETURN);

  final string name;

  overload special_name(token_type the_token_name) {
    this.name = the_token_name.name;
  }

  overload special_name(string name) {
    this.name = name;
  }

  -- TODO: make this obsolete.
  overload special_name(string name, string namespace_id) {
    this(name);
  }

  string to_string => name_utilities.in_brackets(name);
}
