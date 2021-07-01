-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.channels.string_writer;

-- TODO: implement printer that outputs text.
class base_value_printer {
  extends debuggable;
  implements value_printer;

  private static OMIT_DEFAULT_FLAVOR : false;

  private principal_type library_elements_type;

  base_value_printer(principal_type library_elements_type) {
    this.library_elements_type = library_elements_type;
  }

  implement string print_value(abstract_value the_value) {
    -- TODO: render constant_values...
    return print_type(the_value.type_bound);
  }

  string print_type(type the_type) {
    if (the_type is flavored_type) {
      flavor : the_type.get_flavor;
      principal_name : print_type(the_type.principal);
      if (OMIT_DEFAULT_FLAVOR &&
          flavor == the_type.principal.get_flavor_profile.default_flavor) {
        return principal_name;
      } else {
        return flavor ++ " " ++ principal_name;
      }
    } else {
      principal : the_type !> principal_type;
      if (type_utilities.is_union(principal)) {
        return print_union_type(principal);
      }
      principal_name : (principal.get_parent == library_elements_type) ?
          principal.short_name.to_string : print_hierarchical_name(principal);
      if (principal is parametrized_type) {
        return principal_name ++ print_parameters(principal);
      } else {
        return principal_name;
      }
    }
  }

  private string print_hierarchical_name(principal_type the_type) {
    full_names : type_utilities.get_full_names(the_type);
    if (full_names.is_empty) {
      return the_type.to_string;
    } else {
      -- Use list.join()
      the_writer : string_writer.new();
      var boolean first : true;
      for (name : full_names) {
        if (first) {
          first = false;
        } else {
          the_writer.write('.');
        }
        the_writer.write_all(name.to_string());
      }
      return the_writer.elements();
    }
  }

  private string print_union_type(principal_type the_type) {
    parameters : type_utilities.get_union_parameters(the_type);
    assert parameters.size == 2;

    return print_value(parameters[0]) ++ " or " ++ print_value(parameters[1]);
  }

  private string print_parameters(parametrized_type the_type) {
    the_writer : string_writer.new();
    parameters : the_type.get_parameters().fixed_size_list();

    the_writer.write('[');
    var boolean first : true;
    for (parameter : parameters) {
      if (first) {
        first = false;
      } else {
        the_writer.write_all(", ");
      }
      the_writer.write_all(print_value(parameter));
    }
    the_writer.write(']');

    return the_writer.elements();
  }
}
