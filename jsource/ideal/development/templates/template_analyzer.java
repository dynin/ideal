/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.templates;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
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
import ideal.development.analyzers.*;
import ideal.development.functions.*;

public class template_analyzer extends extension_analyzer implements declaration {

  private static final action_name BLOCK_NAME =
      new special_name(new base_string("template"), new base_string("template_analyzer"));
  private static final action_name RESULT_NAME =
      new special_name(new base_string("result"), new base_string("template_analyzer"));

  private static final simple_name ESCAPE_NAME = simple_name.make("escape_html");

  private final sexpression_construct body;
  private escape_fn escaper;
  private principal_type template_block;
  private analyzable result_access;

  public template_analyzer(template_construct source) {
    super(source);
    this.body = source.body;
  }

  @Override
  public principal_type inner_type() {
    assert template_block != null;
    return template_block;
  }

  @Override
  public analyzable do_expand() {
    if (escaper == null) {
      // TODO: move to library...
      escaper = new escape_fn(ESCAPE_NAME);
    }

    origin pos = this;
    template_block = make_block(BLOCK_NAME, this);
    local_variable_declaration result_decl = new local_variable_declaration(
        analyzer_utilities.PRIVATE_MODIFIERS, RESULT_NAME,
        flavor.mutable_flavor, library().immutable_string_type(),
        new analyzable_action(make_string_value(new base_string("")).to_action(pos)), pos);
    result_access = analyzable_action.from(result_decl.get_access(), pos);

    analyzable body_action = sexpr_to_analyzable(body);

    list<analyzable> body_actions = new base_list<analyzable>();
    body_actions.append(result_decl);
    body_actions.append(body_action);
    body_actions.append(new return_analyzer(result_access, pos));

    return new statement_list_analyzer(body_actions, pos);
  }

  public analyzable sexpr_to_analyzable(sexpression_construct sexpr) {
    analyzable result = do_sexpr_to_analyzable(sexpr);
    if (result instanceof error_signal) {
      maybe_report_error((error_signal) result);
    }
    return result;
  }

  private analyzable do_sexpr_to_analyzable(sexpression_construct sexpr) {
    if (sexpr.elements.is_empty()) {
      return make_error("Expected a non-empty s-expression", sexpr);
    }

    construct first = sexpr.elements.first();
    if (! (first instanceof name_construct)) {
      return make_error("Expected a symbol as the first element of s-expression", first);
    }

    action_name name = ((name_construct) first).the_name;
    @Nullable sexpression_handler handler = sexpression_library.lookup(name);

    if (handler == null) {
      return make_error("Unrecognized symbol at the start of s-expression", first);
    }

    readonly_list<construct> arguments = sexpr.elements.skip(1);
    return handler.to_analyzable(arguments, this, first);
  }

  private error_signal make_error(String message, origin pos) {
    return new error_signal(new base_string(message), pos);
  }

  analyzable process_template_expression(construct c) {
    if (c instanceof sexpression_construct) {
      return sexpr_to_analyzable((sexpression_construct) c);
    } else {
      origin pos = this;
      return make_appender(make_escaper(make(c), pos), pos);
    }
  }

  list<analyzable> process_constructs(readonly_list<construct> constructs) {
    list<analyzable> actions = new base_list<analyzable>();
    for (int i = 0; i < constructs.size(); ++i) {
      actions.append(process_template_expression(constructs.get(i)));
    }
    return actions;
  }

  // TODO: this needs to be made cleaner.
  boolean is_attribute_expression(construct c) {
    if (c instanceof sexpression_construct) {
      sexpression_construct sexpr = (sexpression_construct) c;

      if (sexpr.elements.is_not_empty()) {
        construct first = sexpr.elements.first();

        if (first instanceof name_construct) {
          action_name name = ((name_construct) first).the_name;
          @Nullable sexpression_handler handler = sexpression_library.lookup(name);

          return handler instanceof attribute_handler;
        }
      }
    }
    return false;
  }

  /*
  TODO: remove.
  private void check_string(analyzable string_action) {
    type result = string_action.to_action().result().type_bound();
    if (result != library().immutable_string_type()) {
      utilities.panic("Not string: " + result);
    }
  }
  */

  analyzable make_escaper(analyzable string_analyzable, origin pos) {
    // TODO: insert verification instead of check_string()
    // check_string(string_analyzable);
    // TODO: if it's a string_value, escape the constant at compile time...
    // (it is a good idea to make this optimization generic)
    list<analyzable> escape_params = new base_list<analyzable>(string_analyzable);
    return new parameter_analyzer(analyzable_action.from(escaper, pos), escape_params, pos);
  }

  analyzable make_appender(analyzable string_analyzable, origin pos) {
    list<analyzable> append_params = new base_list<analyzable>(result_access, string_analyzable);
    return new parameter_analyzer(new resolve_analyzer(operator.CONCATENATE_ASSIGN, pos),
        append_params, pos);
  }

  analyzable make_appender(string s, origin pos) {
    analyzable string_analyzable = analyzable_action.from(make_string_value(s), pos);
    return make_appender(string_analyzable, pos);
  }

  private abstract_value make_string_value(string s) {
    return new base_string_value(s, library().immutable_string_type());
  }
}
