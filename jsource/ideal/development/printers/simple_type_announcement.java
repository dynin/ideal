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
import ideal.development.declarations.*;
import ideal.development.extensions.*;
import ideal.development.documenters.*;

public class simple_type_announcement extends debuggable implements type_announcement, analyzable {
  private final type_declaration the_type_declaration;
  private final origin the_origin;

  public simple_type_announcement(type_declaration the_type_declaration, origin the_origin) {
    this.the_type_declaration = the_type_declaration;
    this.the_origin = the_origin;
  }

  @Override
  public type_declaration get_type_declaration() {
    return the_type_declaration;
  }

  @Override
  public action_name short_name() {
    return the_type_declaration.short_name();
  }

  @Override
  public kind get_kind() {
    return the_type_declaration.get_kind();
  }

  @Override
  public annotation_set annotations() {
    return the_type_declaration.annotations();
  }

  @Override
  public principal_type get_declared_type() {
    return the_type_declaration.get_declared_type();
  }

  @Override
  public void load_type() {
  }

  @Override
  public boolean has_errors() {
    return the_type_declaration.has_errors();
  }

  @Override
  public principal_type declared_in_type() {
    return the_type_declaration.declared_in_type();
  }

  @Override
  public analysis_result analyze() {
    return ok_signal.instance;
  }

  @Override
  public analyzable specialize(specialization_context context, principal_type new_parent) {
    return this;
  }

  @Override
  public origin deeper_origin() {
    return the_origin;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, short_name());
  }
}
