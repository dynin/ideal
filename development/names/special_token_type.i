-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

namespace special_token_type {
  SIMPLE_NAME : base_token_type.new("<simple-name>", base_symbols.SIMPLE_NAME);
  SPECIAL_NAME : base_token_type.new("<special-name>", base_symbols.SPECIAL_NAME);

  KIND : base_token_type.new("<kind>", base_symbols.KIND);
  MODIFIER_KIND : base_token_type.new("<modifier-kind>", base_symbols.MODIFIER_KIND);
  SUBTYPE_TAG : base_token_type.new("<subtype-tag>", base_symbols.SUBTYPE_TAG);
  FLAVOR : base_token_type.new("<flavor>", base_symbols.FLAVOR);
  JUMP : base_token_type.new("<jump>", base_symbols.JUMP);

  LITERAL : base_token_type.new("<literal>", base_symbols.LITERAL);

  COMMENT : base_token_type.new("<comment>", base_symbols.COMMENT);

  --TEMPLATE : base_token_type.new("<template>", base_symbols.TEMPLATE);
}
