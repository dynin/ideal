/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
import ideal.development.values.*;

public class declaration_extension extends multi_pass_analyzer implements syntax_extension {

  private static final simple_name generated_prefix = simple_name.make("generated");

  private static dictionary<Class, declaration_extension> extension_registry =
      new list_dictionary<Class, declaration_extension>();

  public final extension_kind the_extension_kind;
  @dont_display private final Class this_class;
  private @Nullable declaration_analyzer the_declaration;
  private @Nullable modifier_construct the_modifier;
  private @Nullable immutable_list<declaration> expanded;
  public @Nullable readonly_list<analyzable> analyzable_parameters;

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

  public declaration_analyzer get_declaration() {
    assert the_declaration != null;
    return the_declaration;
  }

  void initialize(declaration_analyzer the_declaration, modifier_construct the_modifier) {
    assert this.the_declaration == null;
    this.the_declaration = the_declaration;
    this.the_modifier = the_modifier;
  }

  public boolean supports_parameters() {
    return false;
  }

  public void set_expanded(readonly_list<declaration> expanded) {
    assert this.expanded == null;
    assert expanded != null;
    this.expanded = expanded.frozen_copy();
  }

  public readonly_list<declaration> expand_to_list() {
    if (expanded != null) {
      return expanded;
    } else {
      return new base_list<declaration>(get_declaration());
    }
  }

  @Override
  public readonly_list<analyzable> children() {
    if (expanded != null) {
      return (immutable_list<analyzable>) (immutable_list) expanded;
    } else {
      return new base_list<analyzable>(get_declaration());
    }
  }

  protected final signal do_multi_pass_analysis(analysis_pass pass) {
    if (pass == analysis_pass.TYPE_DECL) {
      if (the_modifier.parameters != null) {
        if (!supports_parameters()) {
          return new error_signal(new base_string("Extension doesn't support parameters"), this);
        }
        analyzable_parameters = make_list(the_modifier.parameters.the_elements);
      }
    }

    if (has_errors()) {
      return ok_signal.instance;
    }

    if (expanded != null) {
      for (int i = 0; i < expanded.size(); ++i) {
        @Nullable error_signal result = find_error(expanded.get(i), pass);
        if (result != null) {
          return result;
        }
      }
    }

    signal result = process_declaration(pass);

    if (result instanceof ok_signal &&
        expanded == null &&
        has_analysis_errors(the_declaration, pass)) {
      return find_error(the_declaration);
    }

    return result;
  }

  protected final signal process_declaration(analysis_pass pass) {
    assert the_declaration != null;
    if (the_declaration instanceof procedure_analyzer) {
      return process_procedure((procedure_analyzer) the_declaration, pass);
    } else if (the_declaration instanceof variable_analyzer) {
      return process_variable((variable_analyzer) the_declaration, pass);
    } else if (the_declaration instanceof type_declaration_analyzer) {
      return process_type_declaration((type_declaration_analyzer) the_declaration, pass);
    } else {
      utilities.panic("Unrecognized declaration: " + the_declaration);
      return null;
    }
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
    return common_values.nothing(this);
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
