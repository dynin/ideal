-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace elementary_types {

  private var master_type ROOT;
  private var master_type ERROR;
  private var master_type ANY_TYPE;
  private var master_type UNREACHABLE;
  private var master_type TARGET;
  private var master_type UNDEFINED;

  static {
    -- TODO: the above declarations shouldn't be var
    ROOT = master_type.new(special_name.new("root"), type_kinds.block_kind);
    ANY_TYPE = master_type.new(special_name.new("any_type"), type_kinds.block_kind);
    ERROR = master_type.new(special_name.new("error"), type_kinds.block_kind);
    UNREACHABLE = master_type.new(special_name.new("unreachable"), type_kinds.block_kind);
    TARGET = master_type.new(special_name.new("unreachable"), type_kinds.block_kind);
    UNDEFINED = master_type.new(special_name.new("undefined"), type_kinds.block_kind);
  }

  var principal_type root_type => ROOT;
  var principal_type any_type => ANY_TYPE;
  var type error_type => ERROR;
  var type unreachable_type => UNREACHABLE;
  var type target_type => TARGET;
  var type undefined_type => UNDEFINED;

  void set_context(type_declaration_context the_context) {
    ROOT.set_context(the_context);
    ERROR.set_context(the_context);
    ANY_TYPE.set_context(the_context);
    UNREACHABLE.set_context(the_context);
    UNDEFINED.set_context(the_context);
    TARGET.set_context(the_context);
    union_type.set_context(the_context);
  }
}
