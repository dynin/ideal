-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class singleton_value {
  extends base_data_value;

  singleton_value(principal_type singleton_type) {
    super(singleton_type.get_flavored(flavor.deeply_immutable_flavor));
    if (singleton_type.get_kind != type_kinds.singleton_kind) {
      utilities.panic("Not a singleton type: " ++ singleton_type);
    }
  }

  override string to_string() {
    return this.type_bound ++ "." ++ common_names.instance_name;
  }
}
