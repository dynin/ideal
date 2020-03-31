/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tools;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.machine.channels.standard_channels;
import ideal.machine.resources.filesystem;

import ideal.development.elements.*;
import ideal.development.components.*;
import ideal.development.actions.*;
import ideal.development.scanners.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.constructs.*;
import ideal.development.names.*;
import ideal.development.analyzers.*;
import ideal.development.parsers.*;
import ideal.development.transformers.*;
import ideal.development.printers.*;
import ideal.development.documenters.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;
import ideal.development.functions.*;

public class reflect_util {

  private final to_javascript_transformer transformer;
  private final printer method_printer;

  public reflect_util() {
    transformer = new to_javascript_transformer();
    method_printer = new javascript_printer(printer_mode.CURLY);
  }

  public static void start_reflect(string input_name) {
    resource_catalog project_catalog = filesystem.CURRENT_CATALOG;
    create_manager cm = new create_manager(project_catalog);
    cm.process_bootstrap();

    analysis_context context = cm.new_analysis_context();
    principal_type parent = cm.new_block(new base_string("reflect"), context);
    source_content input_source = new source_content(project_catalog.resolve(input_name));

    list<construct> decl = cm.process_source(input_source, parent, context);
    type_declaration world_decl = create_manager.get_declaration(decl, context);
    assert world_decl != null;

    output<text_fragment> out = new plain_formatter(standard_channels.stdout);
    reflect_util reflect = new reflect_util();
    out.write(reflect.print_world(world_decl));
    if (false) {
      out.write(reflect.render_world(world_decl));
    }
    out.sync();
  }

  private text_fragment print_world(type_declaration world_declaration) {
    String line = "World: " + world_declaration.short_name();
    text_fragment result = base_element.make(text_library.DIV, new base_string(line));

    readonly_list<type_declaration> subtypes = create_util.get_declared_types(world_declaration);
    for (int i = 0; i < subtypes.size(); ++i) {
      result = text_util.join(result, print_subtype(subtypes.get(i)));
    }

    return result;
  }

  private text_fragment print_subtype(type_declaration subtype_declaration) {
    String line = "Subtype: " + subtype_declaration.short_name();
    text_fragment result = base_element.make(text_library.DIV, new base_string(line));

    readonly_list<variable_declaration> fields =
        declaration_util.get_declared_variables(subtype_declaration);
    for (int i = 0; i < fields.size(); ++i) {
      result = text_util.join(result, print_field(fields.get(i)));
    }
    readonly_list<procedure_declaration> procedures =
        declaration_util.get_declared_procedures(subtype_declaration);
    for (int i = 0; i < procedures.size(); ++i) {
      result = text_util.join(result, print_method(procedures.get(i)));
    }

    return base_element.make(text_library.INDENT, result);
  }

  private text_fragment print_field(variable_declaration field) {
    String line = "Field: " + field.value_type() + " " + field.short_name();
    return base_element.make(text_library.INDENT, new base_string(line));
  }

  private text_fragment print_method(procedure_declaration procedure) {
    String line = "Method: " + procedure.get_procedure_type() + " " + procedure.short_name();
    text_fragment result = base_element.make(text_library.DIV, new base_string(line));

    procedure_construct pc = transformer.to_construct((procedure_analyzer) procedure);
    result = text_util.join(result, print_method_body(pc));

    return base_element.make(text_library.INDENT, result);
  }

  private text_fragment print_method_body(procedure_construct procedure) {
    return method_printer.print(procedure);
  }

  public text_fragment render_world(type_declaration world_declaration) {
    text_fragment result = text_util.EMPTY_FRAGMENT;

    readonly_list<type_declaration> subtypes = create_util.get_declared_types(world_declaration);
    for (int i = 0; i < subtypes.size(); ++i) {
      result = text_util.join(result, render_subtype(subtypes.get(i)));
    }

    return base_element.make(text_library.INDENT, result);
  }

  private text_fragment render_subtype(type_declaration subtype_declaration) {
    text_fragment result = text_util.EMPTY_FRAGMENT;

    readonly_list<procedure_declaration> procedures =
        declaration_util.get_declared_procedures(subtype_declaration);
    for (int i = 0; i < procedures.size(); ++i) {
      result = text_util.join(result, render_method(procedures.get(i)));
    }

    return result;
  }

  private text_fragment render_method(procedure_declaration procedure) {
    text_fragment result = text_util.EMPTY_FRAGMENT;

    procedure_construct pc = transformer.to_construct((procedure_analyzer) procedure);
    if (!has_errors(pc)) {
      result = text_util.join(result, method_printer.print(pc));
    }

    return result;
  }

  private static boolean has_errors(construct c) {
    if (c instanceof error_signal) {
      return true;
    }
    readonly_list<construct> children = c.children();
    for (int i = 0; i < children.size(); ++i) {
      if (has_errors(children.get(i))) {
        return true;
      }
    }
    return false;
  }
}
