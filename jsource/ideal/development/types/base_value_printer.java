/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

// TODO: implement printer that outputs text.
public class base_value_printer extends debuggable implements value_printer {

  private static boolean OMIT_DEFAULT_FLAVOR = false;

  private principal_type library_elements_type;

  public base_value_printer(principal_type library_elements_type) {
    this.library_elements_type = library_elements_type;
  }

  @Override
  public string print_value(abstract_value the_value) {
    // TODO: render constant_values...
    return print_type(the_value.type_bound());
  }

  public string print_type(type the_type) {
    if (the_type instanceof flavored_type) {
      type_flavor flavor = the_type.get_flavor();
      string principal_name = print_type(the_type.principal());
      if (OMIT_DEFAULT_FLAVOR &&
          flavor == the_type.principal().get_flavor_profile().default_flavor()) {
        return principal_name;
      } else {
        return new base_string(flavor.to_string(), new base_string(" "), principal_name);
      }
    } else {
      principal_type principal = (principal_type) the_type;
      if (type_utilities.is_union(principal)) {
        return print_union_type(principal);
      }
      string principal_name = (principal.get_parent() == library_elements_type) ?
          principal.short_name().to_string() : print_hierarchical_name(principal);
      if (principal instanceof parametrized_type) {
        return new base_string(principal_name, print_parameters((parametrized_type) principal));
      } else {
        return principal_name;
      }
    }
  }

  private string print_hierarchical_name(principal_type the_type) {
    immutable_list<simple_name> full_names = type_utilities.get_full_names(the_type);
    if (full_names.is_empty()) {
      return the_type.to_string();
    } else {
      // Use list.join()
      StringBuilder s = new StringBuilder();
      boolean first = true;
      for (int i = 0; i < full_names.size(); ++i) {
        if (first) {
          first = false;
        } else {
          s.append(".");
        }
        s.append(utilities.s(full_names.get(i).to_string()));
      }
      return new base_string(s.toString());
    }
  }

  private string print_union_type(principal_type the_type) {
    immutable_list<abstract_value> parameters = type_utilities.get_union_parameters(the_type);
    assert parameters.size() == 2;

    return new base_string(print_value(parameters.get(0)), " or ",
        print_value(parameters.get(1)));
  }

  private string print_parameters(parametrized_type the_type) {
    StringBuilder s = new StringBuilder();
    immutable_list<abstract_value> parameters = the_type.get_parameters().internal_access();

    s.append('[');
    boolean first = true;
    for (int i = 0; i < parameters.size(); ++i) {
      if (first) {
        first = false;
      } else {
        s.append(", ");
      }
      s.append(utilities.s(print_value(parameters.get(i))));
    }
    s.append(']');

    return new base_string(s.toString());
  }
}
