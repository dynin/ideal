/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.templates;

import ideal.library.elements.*;
import ideal.library.texts.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.texts.*;
import static ideal.runtime.texts.markup_formatter.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.constructs.*;
import ideal.development.extensions.*;
import ideal.development.flavors.*;
import ideal.development.modifiers.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;
import ideal.development.declarations.*;

public class iteration_handler implements sexpression_handler {

  public action_name name() {
    return keywords.FOR.keyword_name();
  }

  public analyzable to_analyzable(readonly_list<construct> arguments, template_analyzer template,
      origin source) {
    if (arguments.size() < 2) {
      return new error_signal(new base_string("Expected at least two arguments"), source);
    }
    construct var_arg = arguments.first();
    if (!(var_arg instanceof sexpression_construct)) {
      return new error_signal(new base_string("Expected an s-expression"), var_arg);
    }
    readonly_list<construct> var_decl = ((sexpression_construct) var_arg).elements;
    if (var_decl.size() != 2) {
      return new error_signal(new base_string("Expected two elements"), var_arg);
    }
    if (! (var_decl.first() instanceof name_construct)) {
      return new error_signal(new base_string("Expected an identifier"), var_decl.first());
    }
    name_construct name = ((name_construct) var_decl.first());
    construct list_construct = var_decl.get(1);
    analyzable init_action = template.make(list_construct);
    analyzable body_action =
        new statement_list_analyzer(template.process_constructs(arguments.skip(1)), source);

    list_iteration_analyzer iterator = new list_iteration_analyzer(
        annotation_library.PRIVATE_MODIFIERS, name.the_name, init_action, body_action, source);

    return iterator;
  }
}
