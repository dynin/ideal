/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import ideal.library.texts.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.actions.*;
import ideal.development.declarations.*;
import ideal.development.elements.*;
import ideal.development.components.*;
import ideal.development.comments.*;
import ideal.development.names.*;
import ideal.development.literals.*;
import ideal.development.scanners.*;
import ideal.development.modifiers.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.documenters.*;

/**
 * Render the cross-references page.
 */
public class xref_printer {

  private final analysis_context the_analysis_context;
  private final xref_context the_xref_context;
  private final value_printer the_value_printer = new base_value_printer(null);

  public xref_printer(analysis_context the_analysis_context, xref_context the_xref_context) {
    this.the_analysis_context = the_analysis_context;
    this.the_xref_context = the_xref_context;
  }

  public text_fragment print_statements(readonly_list<? extends construct> statements) {
    list<text_fragment> fragments = new base_list<text_fragment>();
    for (int i = 0; i < statements.size(); ++i) {
      construct the_statement = statements.get(i);
      text_fragment printed = print(the_statement);
      if (printed != null) {
        fragments.append(printed);
      }
    }
    return text_util.join(fragments);
  }

  public @Nullable text_fragment print(construct c) {
    if (c instanceof type_declaration_construct) {
      return process_type_declaration((type_declaration_construct) c);
    }
    return null;
  }

  public text_fragment process_type_declaration(type_declaration_construct c) {
    analyzable a = the_analysis_context.get_analyzable(c);
    if (a instanceof type_declaration) {
      type_declaration the_declaration = (type_declaration) a;
      text_fragment title = new base_string(c.kind.to_string(), " ",
          the_value_printer.print_value(the_declaration.get_declared_type()));
      return styles.wrap(styles.xref_title_style, title);
    }
    return null;
  }
}
