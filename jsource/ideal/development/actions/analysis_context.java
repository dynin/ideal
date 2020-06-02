/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

  readonly_list<action> resolve(type from, action_name name, @Nullable action_target target,
      origin pos);

  boolean can_promote(abstract_value from, type target);

  string print_value(abstract_value the_value);

  boolean is_subtype_of(abstract_value the_value, type the_type);

  @Nullable type find_supertype(abstract_value the_value, action_target target);

  @Nullable type find_supertype_procedure(abstract_value the_value);

  action promote(action from, type target, origin pos);

  graph<principal_type, origin> type_graph();

  @Nullable analyzable get_analyzable(construct c);

  void put_analyzable(construct c, analyzable a);

  @Nullable abstract_value lookup_constraint(declaration the_declaration);

  @Nullable readonly_list<construct> load_type_body(type_announcement_construct the_announcement);
}
