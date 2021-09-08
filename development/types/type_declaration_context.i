-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.library.graphs;

interface type_declaration_context {
  extends value;

  graph[principal_type, origin] type_graph;
  void declare_type(principal_type the_type, declaration_pass pass);
  boolean is_parametrizable(type the_type);
  boolean is_subtype_of(abstract_value the_value, type the_type);
}
