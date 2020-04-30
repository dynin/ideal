// Autogenerated from runtime/resources/base_resource_catalog.i

package ideal.runtime.resources;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.library.patterns.*;
import ideal.runtime.patterns.*;

import javax.annotation.Nullable;

public class base_resource_catalog implements resource_catalog, reference<dictionary<string, resource_identifier>> {
  private static final pattern<Character> path_separator = new singleton_pattern<Character>('/');
  private final resource_store the_resource_store;
  private final immutable_list<string> path;
  protected base_resource_catalog(final resource_store the_resource_store, final immutable_list<string> path) {
    this.the_resource_store = the_resource_store;
    this.path = path;
  }
  public @Override reference<dictionary<string, resource_identifier>> content() {
    return this;
  }
  public @Override @Nullable dictionary<string, resource_identifier> get() {
    return null;
  }
  public @Override void set(final @Nullable dictionary<string, resource_identifier> new_value) {
    utilities.panic(new base_string("can't set a catalog"));
  }
  public @Override resource_identifier get_id() {
    return new base_resource_identifier(the_resource_store, path);
  }
  public @Override resource_identifier resolve(final string name) {
    if (name.is_empty()) {
      return new base_resource_identifier(the_resource_store, path);
    }
    final immutable_list<immutable_list<Character>> components = path_separator.split(name);
    boolean absolute = false;
    int index;
    final base_list<string> result = new base_list<string>();
    if (components.first().is_empty()) {
      if (the_resource_store.allow_up()) {
        absolute = true;
      } else { }
      index = 1;
    } else {
      index = 0;
      result.append_all(path);
    }
    while (index < components.size()) {
      final string component = (string) components.get(index);
      index += 1;
      if (component.is_empty() || ideal.machine.elements.runtime_util.values_equal(component, resource_util.CURRENT_CATALOG)) {
        continue;
      } else if (ideal.machine.elements.runtime_util.values_equal(component, resource_util.PARENT_CATALOG)) {
        if (result.is_empty()) {
          if (the_resource_store.allow_up()) {
            result.append(component);
          } else { }
        } else {
          if (ideal.machine.elements.runtime_util.values_equal(result.last(), resource_util.PARENT_CATALOG)) {
            assert the_resource_store.allow_up();
            result.append(component);
          } else {
            result.remove_last();
          }
        }
      } else {
        result.append(component);
      }
    }
    if (absolute) {
      result.prepend(new base_string(""));
    } else if (result.is_empty()) {
      result.append(resource_util.CURRENT_CATALOG);
    }
    return new base_resource_identifier(the_resource_store, result.frozen_copy());
  }
  public @Override resource_identifier resolve(final string name, final @Nullable extension the_extension) {
    if (the_extension != null) {
      return resolve(new base_string(name, the_extension.dot_name()));
    } else {
      return resolve(name);
    }
  }
}
