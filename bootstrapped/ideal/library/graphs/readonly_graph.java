// Autogenerated from library/graphs.i

package ideal.library.graphs;

import ideal.library.elements.*;

public interface readonly_graph<vertice_type, edge_type> extends readonly_data, any_graph<vertice_type, edge_type> {
  readonly_set<vertice_type> vertices();
  immutable_set<vertice_type> adjacent(vertice_type from);
  boolean introduces_cycle(vertice_type from, vertice_type to);
}
