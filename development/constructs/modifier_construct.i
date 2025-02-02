-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

meta_construct class modifier_construct {
  implements annotation_construct;

  modifier_kind the_kind;
  readonly list[construct] or null parameters;
  grouping_type or null paramaters_grouping;

  overload modifier_construct(modifier_kind the_kind, readonly list[construct] or null parameters,
      grouping_type or null paramaters_grouping, origin the_origin) {
    super(the_origin);
    this.the_kind = the_kind;
    this.parameters = parameters;
    this.paramaters_grouping = paramaters_grouping;
  }

  overload modifier_construct(modifier_kind the_kind, list_construct or null parameters,
      origin the_origin) {
    -- TODO: casts should be redundant.
    this(the_kind,
         parameters is_not null ?
            parameters.the_elements :
            missing.instance .> (readonly list[construct] or null),
         parameters is_not null ?
            parameters.grouping :
            missing.instance .> (grouping_type or null),
         the_origin);
  }

  overload modifier_construct(modifier_kind the_kind, origin the_origin) {
    this(the_kind, missing.instance, missing.instance, the_origin);
  }

  override string to_string => utilities.describe(this, the_kind);
}
