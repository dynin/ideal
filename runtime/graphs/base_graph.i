-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import ideal.machine.elements.runtime_util;

class base_graph[readonly data vertice_type, readonly data edge_type] {
  extends debuggable;
  implements graph[vertice_type, edge_type];

  private class edge[readonly data vertice_type, readonly data edge_type] {
    implements immutable data;

    vertice_type from;
    vertice_type to;
    edge_type the_source;

    -- TODO: autogenerate constructor.
    edge(vertice_type from, vertice_type to, edge_type the_source) {
      this.from = from;
      this.to = to;
      this.the_source = the_source;
    }
  }

  protected equivalence_relation[vertice_type] equivalence;
  private dictionary[vertice_type, set[edge[vertice_type, edge_type]]] all_edges;

  overload base_graph(equivalence_relation[vertice_type] equivalence) {
    this.equivalence = equivalence;
    this.all_edges = hash_dictionary[vertice_type, set[edge[vertice_type, edge_type]]].new();
  }

  overload base_graph() {
    -- TODO: cast is redundant.
    this(runtime_util.default_equivalence !> readonly value !> equivalence_relation[vertice_type]);
  }

  override readonly set[vertice_type] vertices() {
    return all_edges.keys;
  }

  override add_edge(vertice_type from, vertice_type to, edge_type the_source) {
    new_edge : edge[vertice_type, edge_type].new(from, to, the_source);
    outgoing_edges : all_edges.get(from);
    if (outgoing_edges is_not null) {
      outgoing_edges.add(new_edge);
    } else {
      new_outgoing_edges : hash_set[edge[vertice_type, edge_type]].new();
      new_outgoing_edges.add(new_edge);
      all_edges.put(from, new_outgoing_edges);
    }
  }

  override immutable set[vertice_type] adjacent(vertice_type from) pure {
    edge_set : all_edges.get(from);
    if (edge_set is null) {
      -- TODO: define an empty set.
      return empty[vertice_type].new();
    }
    adjacent_vertices : hash_set[vertice_type].new();
    for (edge : edge_set.elements) {
      adjacent_vertices.add(edge.to);
    }
    return adjacent_vertices.frozen_copy;
  }

  override boolean introduces_cycle(vertice_type from, vertice_type to) pure {
    if (equivalence(from, to)) {
      return true;
    }

    -- TODO: infer this; use list initializer
    list[vertice_type] considered : base_list[vertice_type].new(from, to);
    visited : hash_set[vertice_type].new();
    visited.add(from);
    visited.add(to);

    for (considered_vertice : considered) {
      for (target_vertice : adjacent(considered_vertice).elements) {
        if (visited.contains(target_vertice)) {
          if (equivalence(target_vertice, from)) {
            return true;
          }
        } else {
          considered.append(target_vertice);
          visited.add(target_vertice);
        }
      }
    }

    return false;
  }
}
