-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

namespace special_token_type {
  SIMPLE_NAME : base_token_type.new("<simple-name>", "SIMPLE_NAME");
  SPECIAL_NAME : base_token_type.new("<special-name>", "SPECIAL_NAME");

  KIND : base_token_type.new("<kind>", "KIND");
  MODIFIER_KIND : base_token_type.new("<modifier-kind>", "MODIFIER_KIND");
  SUBTYPE_TAG : base_token_type.new("<subtype-tag>", "SUBTYPE_TAG");
  FLAVOR : base_token_type.new("<flavor>", "FLAVOR");
  JUMP : base_token_type.new("<jump>", "JUMP");

  CONSTRAINT : base_token_type.new("<constraint>", "CONSTRAINT");

  LITERAL : base_token_type.new("<literal>", "LITERAL");

  COMMENT : base_token_type.new("<comment>", "COMMENT");

  --TEMPLATE : base_token_type.new("<template>", "TEMPLATE");
}
