/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.transformers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;

import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.scanners.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.constructs.*;
import ideal.development.names.*;
import ideal.development.analyzers.*;
import ideal.development.parsers.*;
import ideal.development.printers.*;
import ideal.development.documenters.*;
import ideal.development.declarations.*;

public class java_generator {

  private final java_library java_adapter;
  private final analysis_context context;
  private final content_writer processor;
  private final java_printer printer;

  public java_generator(java_library java_adapter, analysis_context context,
      content_writer processor) {
    this.java_adapter = java_adapter;
    this.context = context;
    this.processor = processor;
    this.printer = new java_printer(printer_mode.CURLY);
  }

  public void generate_for_type(principal_type the_type) {
    type_declaration the_type_declaration = get_type_declaration(the_type.get_declaration());
    type_declaration_construct the_construct =
        (type_declaration_construct) the_type_declaration.source_position();

    generate_top_level(the_type, new base_list<construct>(the_construct),
        new empty<import_construct>());
  }

  private type_declaration get_type_declaration(declaration the_declaration) {
    if (the_declaration instanceof type_announcement) {
      return ((type_announcement) the_declaration).get_type_declaration();
    } else if (the_declaration instanceof type_declaration) {
      return (type_declaration) the_declaration;
    } else {
      utilities.panic("Type declaration expected");
      return null;
    }
  }

  private void generate_top_level(principal_type the_type, readonly_list<construct> constructs,
      readonly_list<import_construct> imports) {

    if (skip_type(the_type)) {
      return;
    }

    list<import_construct> all_imports = new base_list<import_construct>();
    all_imports.append_all(imports);

    if (the_type.get_kind().is_namespace() && has_subtypes(constructs)) {
      filter_imports(constructs, all_imports, false);
      for (int i = 0; i < constructs.size(); ++i) {
        construct the_construct = constructs.get(i);
        if (the_construct instanceof type_declaration_construct) {
          type_declaration_construct the_declaration = (type_declaration_construct) the_construct;
          type_declaration_analyzer declaration_analyzer =
              (type_declaration_analyzer) context.get_analyzable(the_declaration);
          principal_type declared_type = declaration_analyzer.get_declared_type();
          generate_top_level(declared_type, the_declaration.body, all_imports);
        } else if (the_construct instanceof type_announcement_construct) {
          type_announcement_construct the_declaration =
              (type_announcement_construct) the_construct;
          type_announcement_analyzer announced_analyzer =
              (type_announcement_analyzer) context.get_analyzable(the_declaration);
          generate_top_level(announced_analyzer.get_declared_type(),
              announced_analyzer.get_external_body(), all_imports);
        }
      }
    } else {
      filter_imports(constructs, all_imports, true);
      type_declaration the_declaration = (type_declaration) the_type.get_declaration();
      assert the_declaration != null;
      type_declaration_construct the_declaration_construct =
          (type_declaration_construct) get_type_declaration(the_declaration).source_position();
      generate_sources(the_type, the_declaration_construct, all_imports);
    }
  }

  private boolean skip_type(principal_type the_type) {
    return the_type == common_library.get_instance().operators_package();
  }

  // TODO: this should use declarations and not constructs...
  private void filter_imports(readonly_list<construct> declarations, list<import_construct> imports,
      boolean recursive) {
    // TODO: reimplement using list.filter()
    for (int i = 0; i < declarations.size(); ++i) {
      construct the_declaration = declarations.get(i);
      if (the_declaration instanceof import_construct) {
        imports.append((import_construct) the_declaration);
      } else if (recursive && the_declaration instanceof type_declaration_construct) {
        filter_imports(((type_declaration_construct) the_declaration).body, imports, recursive);
      }
    }
  }

  private boolean has_subtypes(readonly_list<construct> declarations) {
    // TODO: reimplement using list.has()
    for (int i = 0; i < declarations.size(); ++i) {
      construct the_declaration = declarations.get(i);
      if (the_declaration instanceof type_declaration_construct ||
          the_declaration instanceof type_announcement_construct) {
        return true;
      }
    }
    return false;
  }

  private void generate_sources(principal_type main_type, type_declaration_construct main_decl,
      readonly_list<import_construct> all_imports) {
    to_java_transformer to_java = new to_java_transformer(java_adapter, context);
    to_java.set_type_context(main_type, all_imports, main_decl);
    readonly_list<construct> generated = to_java.transform1(main_decl);

    for (int i = 0; i < generated.size(); ++i) {
      type_declaration_construct type_decl = (type_declaration_construct) generated.get(i);
      list<construct> statements = new base_list<construct>();
      statements.append_all(to_java.make_headers(type_decl));
      statements.append(type_decl);

      list<simple_name> full_name = new base_list<simple_name>();
      full_name.append_all(type_utilities.get_full_names(main_type.get_parent()));
      full_name.append((simple_name) type_decl.name);

      string content = text_util.to_plain_text(printer.print_statements(statements));
      processor.write(content, full_name, base_extension.JAVA_SOURCE);
    }
  }
}
