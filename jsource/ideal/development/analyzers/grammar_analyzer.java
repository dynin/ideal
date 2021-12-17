/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.futures.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.flags.*;
import ideal.development.values.*;
import ideal.development.grammars.*;

public class grammar_analyzer extends declaration_analyzer<grammar_construct>
    implements declaration {

  public static class terminal_state {
    action_name the_name;
    type the_type;
    int index;

    public terminal_state(action_name the_name, type the_type, int index) {
      this.the_name = the_name;
      this.the_type = the_type;
      this.index = index;
    }
  }

  private principal_type grammar_parent;
  private int current_index;
  private dictionary<action_name, terminal_state> symbols;

  public grammar_analyzer(grammar_construct the_grammar_construct) {
    super(the_grammar_construct);
    current_index = 1;
    symbols = new hash_dictionary<action_name, terminal_state>();
  }

  public action_name short_name() {
    return source.name;
  }

  @Override
  public readonly_list<analyzable> children() {
    list<analyzable> result = new base_list<analyzable>();

    result.append(annotations());

    return result;
  }

  @Override
  public principal_type inner_type() {
    assert grammar_parent != null;
    return grammar_parent;
  }

  @Override
  protected signal do_multi_pass_analysis(analysis_pass pass) {
    if (pass == analysis_pass.METHOD_AND_VARIABLE_DECL) {
      origin the_origin = this;
      process_annotations(source.annotations, access_modifier.public_modifier);
      grammar_parent = make_block(short_name(), declared_in_type(), this);

      signal result = ok_signal.instance;
      readonly_list<construct> body = source.body;
      for (int i = 0; i < body.size(); ++i) {
        if (body.get(i) instanceof terminal_construct) {
          signal processed = process_terminal((terminal_construct) body.get(i), the_origin);
          if (processed instanceof error_signal && result instanceof ok_signal) {
            result = processed;
          }
        }
      }

      return result;
    }

    return ok_signal.instance;
  }

  private int next_index() {
    return current_index++;
  }

  private signal process_terminal(terminal_construct the_terminal_construct, origin the_origin) {
    analyzable symbol_type = make(the_terminal_construct.the_type);
    if (has_analysis_errors(symbol_type)) {
      return new error_signal(new base_string("Error in symbol type"), symbol_type, the_origin);
    }

    action action_type = action_not_error(symbol_type);
    type the_type;

    if (action_type instanceof type_action) {
      the_type = ((type_action) action_type).get_type();
    } else {
      return new error_signal(messages.type_expected, symbol_type);
    }

    for (int i = 0; i < the_terminal_construct.the_names.size(); ++i) {
      name_construct the_name_construct = the_terminal_construct.the_names.get(i);
      terminal_state the_terminal_state = new terminal_state(the_name_construct.the_name,
          the_type, next_index());
      // TODO: check for duplicate symbol names.
      symbols.put(the_terminal_state.the_name, the_terminal_state);
    }
    return ok_signal.instance;
  }

  @Override
  protected action do_get_result() {
    origin the_origin = this;
    return grammar_parent.to_action(this);
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}
