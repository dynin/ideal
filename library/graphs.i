-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Declaration for graph interface.
package graphs {
  implicit import ideal.library.elements;

  -- TODO: this interface needs to be refined.
  interface graph[readonly data vertice_type, readonly data edge_type] {
    extends data;

    readonly set[vertice_type] vertices;

    void add_edge(vertice_type from, vertice_type to, edge_type the_source);
    immutable set[vertice_type] adjacent(vertice_type from) pure;
    boolean introduces_cycle(vertice_type from, vertice_type to) pure;
  }
}
