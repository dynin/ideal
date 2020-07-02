/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

public class declaration_extension extends multi_pass_analyzer implements syntax_extension {

  private static dictionary<Class, declaration_extension> extension_registry =
      new list_dictionary<Class, declaration_extension>();

  private final extension_modifier_kind the_modifier_kind;
  @dont_display private final Class this_class;
  private @Nullable declaration_analyzer the_declaration;

  public declaration_extension(String modifier_kind_name) {
    super(analyzer_utilities.UNINITIALIZED_POSITION);
    this_class = getClass();
    if (extension_registry.contains_key(this_class)) {
      declaration_extension master_declaration = extension_registry.get(this_class);
      the_modifier_kind = master_declaration.the_modifier_kind;
    } else {
      the_modifier_kind =
          new extension_modifier_kind(new base_string(modifier_kind_name), this_class);
    }
  }

  @Override
  public void register_syntax_extension(scanner_config the_scanner_config) {
    the_scanner_config.add_modifier(the_modifier_kind);
  }

  @Override
  public origin deeper_origin() {
    if (the_declaration != null) {
      return the_declaration;
    } else {
      return this.deeper_origin();
    }
  }

  declaration_analyzer get_declaration() {
    assert the_declaration != null;
    return the_declaration;
  }

  void set_declaration(declaration_analyzer the_declaration) {
    assert this.the_declaration == null;
    this.the_declaration = the_declaration;
  }

  public readonly_list<declaration> expanded_declarations() {
    return new base_list<declaration>(get_declaration());
  }

  protected @Nullable error_signal do_multi_pass_analysis(analysis_pass pass) {
    if (has_errors()) {
      return null;
    }

    assert the_declaration != null;
    if (the_declaration instanceof procedure_analyzer) {
      return process_procedure((procedure_analyzer) the_declaration, pass);
    }

    if (has_errors(get_declaration(), pass)) {
      return find_error(get_declaration());
    }

    return null;
  }

  protected @Nullable error_signal process_procedure(procedure_analyzer the_procedure,
      analysis_pass pass) {
    return new error_signal(new base_string("Extension doesn't support procedure"), this);
  }

  protected analysis_result do_get_result() {
    return library().void_instance().to_action(this);
  }
}
