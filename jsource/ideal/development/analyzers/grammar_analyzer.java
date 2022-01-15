/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
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

  public static class symbol_state {
    action_name the_name;
    type the_type;
    int index;
    origin the_origin;

    public symbol_state(action_name the_name, type the_type, int index, origin the_origin) {
      this.the_name = the_name;
      this.the_type = the_type;
      this.index = index;
      this.the_origin = the_origin;
    }
  }

  public static class terminal_state extends symbol_state {
    public terminal_state(action_name the_name, type the_type, int index, origin the_origin) {
      super(the_name, the_type, index, the_origin);
    }
  }

  public static class nonterminal_state extends symbol_state {
    public nonterminal_state(action_name the_name, type the_type, int index, origin the_origin) {
      super(the_name, the_type, index, the_origin);
    }
  }

  private principal_type grammar_parent;
  private int current_index;
  private dictionary<action_name, symbol_state> symbols;
  private @Nullable error_signal result_error;

  public grammar_analyzer(grammar_construct the_grammar_construct) {
    super(the_grammar_construct);
    current_index = 1;
    symbols = new hash_dictionary<action_name, symbol_state>();
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
        construct the_construct = body.get(i);
        if (the_construct instanceof terminal_construct) {
          process_terminal((terminal_construct) the_construct, the_origin);
        } else if (the_construct instanceof nonterminal_construct) {
          process_nonterminal((nonterminal_construct) the_construct, the_origin);
        }
      }
    }

    return result_error == null ? ok_signal.instance : result_error;
  }

  private int next_index() {
    return current_index++;
  }

  private void set_error(error_signal the_error_signal) {
    if (result_error == null) {
      result_error = the_error_signal;
    }
  }

  private @Nullable type analyze_type(construct type_construct, origin the_origin) {
    analyzable symbol_type = make(type_construct);
    if (has_analysis_errors(symbol_type)) {
      set_error(new error_signal(new base_string("Error in symbol type"), symbol_type, the_origin));
      return null;
    }

    action action_type = symbol_type.analyze().to_action();

    if (action_type instanceof type_action) {
      return ((type_action) action_type).get_type();
    } else {
      notification type_expected = new base_notification(messages.type_expected, symbol_type);
      type_expected.report();
      set_error(new error_signal(type_expected, true));
      return null;
    }
  }

  private void report_duplicate(action_name the_name, origin the_origin) {
    notification first_symbol = new base_notification(new base_string("First symbol declaration"),
        symbols.get(the_name).the_origin);
    notification duplicate = new base_notification(
        new base_string("Duplicate symbol name: " + the_name),
        the_origin, new base_list<notification>(first_symbol));
    duplicate.report();
    set_error(new error_signal(duplicate, true));
  }

  private void process_terminal(terminal_construct the_terminal_construct, origin the_origin) {
    @Nullable type the_type = analyze_type(the_terminal_construct.the_type, the_origin);
    if (the_type == null) {
      return;
    }

    for (int i = 0; i < the_terminal_construct.the_names.size(); ++i) {
      name_construct the_name_construct = the_terminal_construct.the_names.get(i);
      action_name the_name = the_name_construct.the_name;
      if (symbols.contains_key(the_name)) {
        report_duplicate(the_name, the_name_construct);
        continue;
      }
      terminal_state the_terminal_state = new terminal_state(the_name, the_type, next_index(),
          the_name_construct);
      symbols.put(the_name, the_terminal_state);
    }
  }

  private void process_nonterminal(nonterminal_construct the_nonterminal_construct,
      origin the_origin) {
    @Nullable type the_type = analyze_type(the_nonterminal_construct.the_type, the_origin);
    if (the_type == null) {
      return;
    }

    for (int i = 0; i < the_nonterminal_construct.the_names.size(); ++i) {
      name_construct the_name_construct = the_nonterminal_construct.the_names.get(i);
      action_name the_name = the_name_construct.the_name;
      if (symbols.contains_key(the_name)) {
        report_duplicate(the_name, the_name_construct);
        continue;
      }
      nonterminal_state the_nonterminal_state = new nonterminal_state(the_name, the_type,
          next_index(), the_name_construct);
      symbols.put(the_name, the_nonterminal_state);
    }
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
