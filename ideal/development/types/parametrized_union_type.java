/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;

public class parametrized_union_type extends parametrized_type {
  parametrized_union_type(master_union_type master) {
    super(master);
  }

  @Override
  public type get_flavored(type_flavor flavor) {
    immutable_list<abstract_value> parameters = get_parameters().fixed_size_list();
    list<abstract_value> new_parameters = new base_list<abstract_value>();
    for (int i = 0; i < parameters.size(); ++i) {
      abstract_value the_parameter = parameters.get(i);
      if (the_parameter instanceof type) {
        new_parameters.append(((type) the_parameter).get_flavored(flavor));
      } else {
        new_parameters.append(the_parameter);
      }
    }
    return get_master().bind_parameters(new type_parameters(new_parameters));
  }

  @Override
  public flavor_profile get_flavor_profile() {
    if (the_flavor_profile == null) {
      the_flavor_profile = default_flavor_profile();
    }
    return the_flavor_profile;
  }

  @Override
  public flavor_profile default_flavor_profile() {
    immutable_list<abstract_value> parameters = get_parameters().fixed_size_list();
    flavor_profile result = flavor_profiles.mutable_profile;

    for (int i = 0; i < parameters.size(); ++i) {
      flavor_profile profile = type_utilities.get_flavor_profile(
          parameters.get(i).type_bound().principal());
      result = flavor_profiles.combine(result, profile);
    }

    return result;
  }

  @Override
  protected void do_declare_actual(declaration_pass pass) {
    immutable_list<abstract_value> parameters = get_parameters().fixed_size_list();
    for (int i = 0; i < parameters.size(); ++i) {
      abstract_value the_parameter = parameters.get(i);
      type_utilities.prepare(the_parameter, pass);
    }
  }
}
