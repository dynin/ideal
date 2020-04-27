// Autogenerated from runtime/resources/base_resource_identifier.i

package ideal.runtime.resources;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public class base_resource_identifier extends debuggable implements resource_identifier {
  private final resource_store the_resource_store;
  private final immutable_list<string> path;
  public base_resource_identifier(final resource_store the_resource_store, final immutable_list<string> path) {
    this.the_resource_store = the_resource_store;
    this.path = path;
  }
  public @Override base_resource_identifier parent() {
    final int parent_path_size = path.size() - 1;
    if (parent_path_size >= 0) {
      final immutable_list<string> parent_path = path.slice(0, parent_path_size);
      return new base_resource_identifier(the_resource_store, path.slice(0, parent_path_size));
    } else {
      return this;
    }
  }
  public @Override boolean exists() {
    return the_resource_store.exists(path);
  }
  public @Override resource<string> access_string(final @Nullable access_option options) {
    return new string_resource(this, options);
  }
  public @Override resource<resource_catalog> access_catalog() {
    return new catalog_resource(this);
  }
  public @Override string to_string() {
    return the_resource_store.build_name(path);
  }
  private static class string_resource implements resource<string>, reference<string> {
    private final base_resource_identifier the_identifier;
    private final @Nullable access_option options;
    public string_resource(final base_resource_identifier the_identifier, final @Nullable access_option options) {
      this.the_identifier = the_identifier;
      this.options = options;
    }
    public @Override reference<string> content() {
      return this;
    }
    public @Override string get() {
      return the_identifier.the_resource_store.read_string(the_identifier.path);
    }
    public @Override void set(final string new_value) {
      if (options instanceof make_catalog_option && the_identifier.path.size() > 1) {
        the_identifier.the_resource_store.make_catalog(the_identifier.parent().path);
      }
      the_identifier.the_resource_store.write_string(the_identifier.path, new_value);
    }
  }
  private static class catalog_resource implements resource<resource_catalog>, reference<resource_catalog> {
    private final base_resource_identifier the_identifier;
    public catalog_resource(final base_resource_identifier the_identifier) {
      this.the_identifier = the_identifier;
    }
    public @Override reference<resource_catalog> content() {
      return this;
    }
    public @Override resource_catalog get() {
      return new base_resource_catalog(the_identifier.the_resource_store, the_identifier.path);
    }
    public @Override void set(final resource_catalog new_value) {
      utilities.panic(new base_string("can't set a catalog"));
    }
  }
}
