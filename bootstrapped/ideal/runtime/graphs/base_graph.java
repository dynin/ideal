// Autogenerated from runtime/graphs/base_graph.i

package ideal.runtime.graphs;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import ideal.runtime.elements.*;
import ideal.machine.elements.runtime_util;

import javax.annotation.Nullable;

public class base_graph<vertice_type, edge_type> extends debuggable implements graph<vertice_type, edge_type> {
  private static class edge<vertice_type, edge_type> implements immutable_data {
    public final vertice_type from;
    public final vertice_type to;
    public final edge_type the_source;
    public edge(final vertice_type from, final vertice_type to, final edge_type the_source) {
      this.from = from;
      this.to = to;
      this.the_source = the_source;
    }
  }
  protected final equivalence_relation<vertice_type> equivalence;
  private final dictionary<vertice_type, set<base_graph.edge<vertice_type, edge_type>>> all_edges;
  public base_graph(final equivalence_relation<vertice_type> equivalence) {
    this.equivalence = equivalence;
    this.all_edges = new hash_dictionary<vertice_type, set<base_graph.edge<vertice_type, edge_type>>>();
  }
  public base_graph() {
    this((equivalence_relation<vertice_type>) (readonly_value) runtime_util.default_equivalence);
  }
  public @Override readonly_set<vertice_type> vertices() {
    return this.all_edges.keys();
  }
  public @Override void add_edge(final vertice_type from, final vertice_type to, final edge_type the_source) {
    final base_graph.edge<vertice_type, edge_type> new_edge = new base_graph.edge<vertice_type, edge_type>(from, to, the_source);
    final @Nullable set<base_graph.edge<vertice_type, edge_type>> outgoing_edges = this.all_edges.get(from);
    if (outgoing_edges != null) {
      outgoing_edges.add(new_edge);
    } else {
      final hash_set<base_graph.edge<vertice_type, edge_type>> new_outgoing_edges = new hash_set<base_graph.edge<vertice_type, edge_type>>();
      new_outgoing_edges.add(new_edge);
      this.all_edges.put(from, new_outgoing_edges);
    }
  }
  public @Override immutable_set<vertice_type> adjacent(final vertice_type from) {
    final @Nullable set<base_graph.edge<vertice_type, edge_type>> edge_set = this.all_edges.get(from);
    if (edge_set == null) {
      return new empty<vertice_type>();
    }
    final hash_set<vertice_type> adjacent_vertices = new hash_set<vertice_type>();
    {
      final readonly_list<base_graph.edge<vertice_type, edge_type>> edge_list = edge_set.elements();
      for (Integer edge_index = 0; edge_index < edge_list.size(); edge_index += 1) {
        final base_graph.edge<vertice_type, edge_type> edge = edge_list.get(edge_index);
        adjacent_vertices.add(edge.to);
      }
    }
    return adjacent_vertices.frozen_copy();
  }
  public @Override boolean introduces_cycle(final vertice_type from, final vertice_type to) {
    if (this.equivalence.call(from, to)) {
      return true;
    }
    final list<vertice_type> considered = new base_list<vertice_type>(from, to);
    final hash_set<vertice_type> visited = new hash_set<vertice_type>();
    visited.add(from);
    visited.add(to);
    {
      final readonly_list<vertice_type> considered_vertice_list = considered;
      for (Integer considered_vertice_index = 0; considered_vertice_index < considered_vertice_list.size(); considered_vertice_index += 1) {
        final vertice_type considered_vertice = considered_vertice_list.get(considered_vertice_index);
        {
          final readonly_list<vertice_type> target_vertice_list = this.adjacent(considered_vertice).elements();
          for (Integer target_vertice_index = 0; target_vertice_index < target_vertice_list.size(); target_vertice_index += 1) {
            final vertice_type target_vertice = target_vertice_list.get(target_vertice_index);
            if (visited.contains(target_vertice)) {
              if (this.equivalence.call(target_vertice, from)) {
                return true;
              }
            } else {
              considered.append(target_vertice);
              visited.add(target_vertice);
            }
          }
        }
      }
    }
    return false;
  }
}
