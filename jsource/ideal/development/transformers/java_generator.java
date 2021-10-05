/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
  private final content_writer processor;
  private final java_printer printer;

  public java_generator(java_library java_adapter, content_writer processor) {
    this.java_adapter = java_adapter;
    this.processor = processor;
    this.printer = new java_printer(printer_mode.CURLY);
  }

  public void generate_for_type(principal_type the_type) {
    type_declaration the_type_declaration = get_type_declaration(the_type.get_declaration());
    generate_top_level(the_type, new base_list<declaration>(the_type_declaration),
        new empty<import_declaration>());
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

  public void generate_top_level(principal_type the_type, readonly_list<declaration> declarations,
      readonly_list<import_declaration> imports) {

    if (skip_type(the_type)) {
      return;
    }

    list<import_declaration> all_imports = new base_list<import_declaration>();
    all_imports.append_all(imports);

    if (the_type.get_kind().is_namespace() && has_subtypes(declarations)) {
      filter_imports(declarations, all_imports, false);
      for (int i = 0; i < declarations.size(); ++i) {
        declaration the_declaration = declarations.get(i);
        if (the_declaration instanceof type_declaration) {
          type_declaration the_type_declaration = (type_declaration) the_declaration;
          principal_type declared_type = the_type_declaration.get_declared_type();
          generate_top_level(declared_type, the_type_declaration.get_signature(), all_imports);
        } else if (the_declaration instanceof type_announcement) {
          type_announcement the_type_announcement = (type_announcement) the_declaration;
          generate_top_level(the_type_announcement.get_declared_type(),
              the_type_announcement.external_declarations(), all_imports);
        }
      }
    } else {
      filter_imports(declarations, all_imports, true);
      type_declaration the_declaration = get_type_declaration(the_type.get_declaration());
      assert the_declaration != null;
      generate_sources(the_type, the_declaration, all_imports);
    }
  }

  private boolean skip_type(principal_type the_type) {
    return the_type == common_types.operators_package();
  }

  private void filter_imports(readonly_list<declaration> declarations,
      list<import_declaration> imports, boolean recursive) {
    // TODO: reimplement using list.filter()
    for (int i = 0; i < declarations.size(); ++i) {
      declaration the_declaration = declarations.get(i);
      if (the_declaration instanceof import_declaration) {
        imports.append((import_declaration) the_declaration);
      } else if (recursive && the_declaration instanceof type_declaration) {
        filter_imports(((type_declaration) the_declaration).get_signature(), imports, recursive);
      }
    }
  }

  private boolean has_subtypes(readonly_list<declaration> declarations) {
    // TODO: reimplement using list.has()
    for (int i = 0; i < declarations.size(); ++i) {
      declaration the_declaration = declarations.get(i);
      if (the_declaration instanceof type_declaration ||
          the_declaration instanceof type_announcement) {
        return true;
      }
    }
    return false;
  }

  private void generate_sources(principal_type main_type, type_declaration main_decl,
      readonly_list<import_declaration> all_imports) {
    origin the_origin = main_decl;
    to_java_transformer to_java = new to_java_transformer(java_adapter);
    to_java.set_type_context(main_type, all_imports, the_origin);
    readonly_list<construct> generated = to_java.transform1(main_decl);

    for (int i = 0; i < generated.size(); ++i) {
      type_declaration_construct type_decl = (type_declaration_construct) generated.get(i);
      list<construct> statements = new base_list<construct>();
      statements.append_all(to_java.make_headers(type_decl));
      statements.append(type_decl);

      list<simple_name> full_name = new base_list<simple_name>();
      full_name.append_all(type_utilities.get_full_names(main_type.get_parent()));
      full_name.append((simple_name) type_decl.name);

      string content = text_utilities.to_plain_text(printer.print_statements(statements));
      processor.write(content, full_name, base_extension.JAVA_SOURCE);
    }
  }
}
