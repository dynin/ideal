/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.library.channels.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.resources.*;
import ideal.runtime.texts.*;
import ideal.machine.elements.runtime_util;
import ideal.machine.channels.standard_channels;
import ideal.machine.resources.filesystem;
import ideal.machine.annotations.dont_display;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.modifiers.*;
import ideal.development.scanners.*;
import ideal.development.transformers.*;
import ideal.development.printers.*;

public class declaration_extension extends multi_pass_analyzer implements syntax_extension {

  private static final simple_name generated_prefix = simple_name.make("generated");

  private static dictionary<Class, declaration_extension> extension_registry =
      new list_dictionary<Class, declaration_extension>();

  public final extension_kind the_extension_kind;
  @dont_display private final Class this_class;
  private @Nullable declaration_analyzer the_declaration;
  private @Nullable modifier_construct the_modifier;
  private boolean is_expanded_set;
  private @Nullable declaration expanded;

  public declaration_extension(String modifier_kind_name) {
    super(analyzer_utilities.UNINITIALIZED_POSITION);
    this_class = getClass();
    if (extension_registry.contains_key(this_class)) {
      declaration_extension master_declaration = extension_registry.get(this_class);
      the_extension_kind = master_declaration.the_extension_kind;
    } else {
      the_extension_kind =
          new extension_kind(new base_string(modifier_kind_name), this_class);
    }
    is_expanded_set = false;
  }

  @Override
  public void register_syntax_extension(scanner_config the_scanner_config) {
    the_scanner_config.add_modifier(the_extension_kind);
  }

  @Override
  public origin deeper_origin() {
    if (the_modifier != null) {
      return the_modifier;
    } else {
      return super.deeper_origin();
    }
  }

  declaration_analyzer get_declaration() {
    assert the_declaration != null;
    return the_declaration;
  }

  void initialize(declaration_analyzer the_declaration, modifier_construct the_modifier) {
    assert this.the_declaration == null;
    this.the_declaration = the_declaration;
    this.the_modifier = the_modifier;
  }

  public void set_expanded(declaration expanded) {
    assert !is_expanded_set;
    is_expanded_set = true;
    this.expanded = expanded;
    if (expanded instanceof analyzable) {
      analysis_pass current_pass = analysis_pass.values()[last_pass.ordinal() + 1];
      analyze_and_ignore_errors((analyzable) expanded, current_pass);
    }
  }

  public @Nullable declaration expand() {
    if (has_errors()) {
      return null;
    } else if (is_expanded_set) {
      return expanded;
    } else {
      return get_declaration();
    }
  }

  @Override
  protected void traverse_children(analyzer_visitor the_visitor) {
    assert is_expanded_set;
    the_visitor.visit(expanded);
  }

  protected signal do_multi_pass_analysis(analysis_pass pass) {
    if (has_errors()) {
      return ok_signal.instance;
    }

    if (is_expanded_set) {
      if (expanded instanceof analyzable) {
        analyze_and_ignore_errors((analyzable) expanded, pass);
      }
    }

    assert the_declaration != null;
    if (the_declaration instanceof procedure_analyzer) {
      return process_procedure((procedure_analyzer) the_declaration, pass);
    } else if (the_declaration instanceof variable_analyzer) {
      return process_variable((variable_analyzer) the_declaration, pass);
    } else if (the_declaration instanceof type_declaration_analyzer) {
      return process_type_declaration((type_declaration_analyzer) the_declaration, pass);
    }

    if (has_analysis_errors(get_declaration(), pass)) {
      return find_error(get_declaration());
    }

    return ok_signal.instance;
  }

  public simple_name generated_name(simple_name name) {
    return name_utilities.join(generated_prefix, name);
  }

  public analyzable_action to_analyzable(abstract_value the_abstract_value) {
    origin the_origin = this;
    return base_analyzable_action.from(the_abstract_value, the_origin);
  }

  protected signal process_procedure(procedure_analyzer the_procedure,
      analysis_pass pass) {
    return new error_signal(new base_string("Extension doesn't support a procedure"), this);
  }

  protected signal process_variable(variable_analyzer the_variable,
      analysis_pass pass) {
    return new error_signal(new base_string("Extension doesn't support a variable"), this);
  }

  protected signal process_type_declaration(
      type_declaration_analyzer the_type_declaration, analysis_pass pass) {
    return new error_signal(
        new base_string("Extension doesn't support a type declaration"), this);
  }

  protected analysis_result do_get_result() {
    return library().noop(this);
  }

  protected void display_code(declaration code) {
    readonly_list<construct> constructs = new to_java_transformer(java_library.get_instance()).
        transform1(code);
    output<text_fragment> out = new plain_formatter(standard_channels.stdout);
    if (false) {
      out.write(runtime_util.display(constructs));
    }
    out.write(new java_printer(printer_mode.CURLY).print_statements(constructs));
  }
}
