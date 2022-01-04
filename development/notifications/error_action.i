-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

class error_action {
  extends error_signal;
  implements deeply_immutable data;
  implements mutable action, mutable abstract_value;

  error_action(error_signal signal) {
    super(signal.cause, signal.is_cascading);
  }

  override type type_bound => common_types.error_type;

  override action to_action(origin the_origin) => this;

  override abstract_value result => this;

  override boolean is_parametrizable => false;

  override declaration or null get_declaration => missing.instance;

  override boolean has_side_effects => false;

  override action combine(action from, origin the_origin) => this;

  override entity_wrapper execute(entity_wrapper from_entity, execution_context context) =>
    panic_value.new("Attempting to execute error_signal");
}
