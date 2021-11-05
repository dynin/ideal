-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- JavaScript ...(sorry, ECMAScript)-specific information.
namespace javascript_language {
  set[modifier_kind] supported_modifiers : hash_set[modifier_kind].new();

  static {
    supported_modifiers.add_all([
        var_modifier,
        -- Hmmm...
        -- documentation_modifier,
    ]);
  }
}
