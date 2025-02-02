-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.library.graphs;

interface type_declaration_context {
  extends value;

  graph[principal_type, origin] type_graph;
  declare_type(principal_type the_type, declaration_pass pass);
  boolean is_parametrizable(type the_type);
  boolean is_subtype_of(abstract_value the_value, type the_type);
  --- Used when creating common_types.
  master_type get_or_create_type(action_name name, kind kind, principal_type parent,
      flavor_profile the_flavor_profile);
}
