// Autogenerated from runtime/resources/resource_store.i

package ideal.runtime.resources;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;

public interface resource_store extends readonly_resource_store, writeonly_resource_store {
  void make_catalog(immutable_list<string> path);
  void write_string(immutable_list<string> path, string new_value);
}
