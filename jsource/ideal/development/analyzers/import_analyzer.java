/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.modifiers.*;

public class import_analyzer extends declaration_analyzer<import_construct> {

  private final analyzable type_analyzable;
  private @Nullable action_name the_name;

  public import_analyzer(import_construct source) {
    super(source);
    type_analyzable = make(source.type);
  }

  @Override
  protected @Nullable error_signal do_multi_pass_analysis(analysis_pass pass) {

    if (pass == analysis_pass.TYPE_DECL) {
      process_annotations(source.annotations, access_modifier.private_modifier);

      if (is_implicit()) {
        the_name = special_name.PROMOTION;
      } else {
        the_name = short_name();
        if (the_name == null) {
          return new error_signal(messages.name_expected, source.type);
        }
        get_context().add(parent(), the_name, new import_type_action(this));
      }

    } else if (pass == analysis_pass.IMPORT_AND_TYPE_VAR_DECL) {

      if (has_errors(type_analyzable)) {
        error_signal result = new error_signal(new base_string("Error in import type"),
            type_analyzable, this);
        add_error(parent(), the_name, result);
        return result;
      }

      action the_action = action_not_error(type_analyzable);
      if (! (the_action instanceof type_action)) {
        // TODO: add an action?
        return new error_signal(messages.type_expected, source.type);
      }

      if (is_implicit()) { // TODO: refactor code
        type imported_type = ((type_action) the_action).get_type();
        get_context().add(parent(), the_name, imported_type.to_action(this));
      }
    }

    return null;
  }

  private boolean is_implicit() {
    return annotations().has(general_modifier.implicit_modifier);
  }

  private @Nullable action_name short_name() {
    return get_action_name(source.type);
  }

  private type resolve_type() {
    if (!has_errors(type_analyzable)) {
      action the_action = action_not_error(type_analyzable);
      if (the_action instanceof type_action) {
        return ((type_action) the_action).get_type();
      }
    }
    return core_types.error_type();
  }

  private static @Nullable action_name get_action_name(construct c) {
    if (c instanceof name_construct) {
      return ((name_construct) c).the_name;
    } else if (c instanceof resolve_construct) {
      return get_action_name(((resolve_construct) c).name);
    } else {
      return null;
    }
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }

  private static class import_type_action extends type_action {

    private final import_analyzer the_import;
    private @Nullable type the_type;

    import_type_action(import_analyzer the_import) {
      super(the_import);
      this.the_import = the_import;
    }

    @Override
    public type get_type() {
      if (the_type == null) {
        the_type = the_import.resolve_type();
        assert the_type != null;
      }
      return the_type;
    }
  }
}
