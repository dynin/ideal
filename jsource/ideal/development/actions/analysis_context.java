/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
import ideal.development.constructs.*;
import ideal.development.declarations.*;
import ideal.development.types.*;
import ideal.development.notifications.error_signal;

public interface analysis_context extends type_declaration_context, value_printer, value {

  semantics language();

  readonly_list<action> lookup(type from, action_name name);

  void add(type from, action_name name, action the_action);

  void add_supertype(type subtype, type supertype);

  readonly_list<action> resolve(type from, action_name name, origin pos);

  boolean can_promote(action from, type target);

  string print_value(abstract_value the_value);

  boolean is_subtype_of(abstract_value the_value, type the_type);

  @Nullable type find_supertype_procedure(abstract_value the_value);

  action promote(action from, type target, origin pos);

  graph<principal_type, origin> type_graph();

  @Nullable readonly_list<construct> load_resource(type_announcement_construct the_announcement);
}
