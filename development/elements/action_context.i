-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.library.graphs.graph;

interface action_context {

  language_settings settings;

  readonly list[action] lookup(type from, action_name name);

  add(type from, action_name name, the action);

  add_supertype(type subtype, type supertype);

  readonly list[action] resolve(type from, action_name name, the origin);

  boolean can_promote(action from, type target_type);

  action to_value(action expression, origin the_origin);

  boolean is_subtype_of(abstract_value the_value, type the_type);

  readonly set[type] find_matching_supertype(type the_type, predicate[type] the_predicate);

  action promote(action from, type target_type, origin pos);

  graph[principal_type, origin] type_graph;
}
