// Autogenerated from runtime/resources/base_resource_store.i

package ideal.runtime.resources;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.machine.channels.string_writer;

public abstract class base_resource_store extends debuggable implements resource_store {
  private final string path_prefix;
  private final boolean do_allow_up;
  private final boolean skip_prefix;
  protected base_resource_store(final string path_prefix, final boolean do_allow_up, final boolean skip_prefix) {
    this.path_prefix = path_prefix;
    this.do_allow_up = do_allow_up;
    this.skip_prefix = skip_prefix;
  }
  protected abstract string default_scheme();
  public @Override boolean allow_up() {
    return this.do_allow_up;
  }
  public @Override string build_name(final string scheme, final immutable_list<string> path) {
    if (path.is_empty()) {
      return this.path_prefix;
    }
    final string_writer result = new string_writer();
    if (!this.skip_prefix) {
      result.write_all(this.path_prefix);
    }
    for (Integer i = 0; i < path.size(); i += 1) {
      if (i > 0) {
        result.write_all(resource_util.PATH_SEPARATOR);
      }
      result.write_all(path.get(i));
    }
    return result.elements();
  }
  public resource_catalog top() {
    return new base_resource_catalog(this, this.default_scheme(), new empty<string>());
  }
}
