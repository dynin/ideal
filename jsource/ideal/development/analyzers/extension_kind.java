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
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.declarations.*;
import ideal.development.modifiers.*;

public class extension_kind extends base_modifier_kind {
  private final Class extension_class;

  public extension_kind(string name, Class extension_class) {
    super(name);
    this.extension_class = extension_class;
  }

  public declaration_extension make_extension(declaration_analyzer the_declaration,
      modifier_construct the_modifier) {
    declaration_extension instance = null;
    try {
      instance = (declaration_extension) extension_class.newInstance();
    } catch (InstantiationException e) {
      utilities.panic(e.toString());
    } catch (IllegalAccessException e) {
      utilities.panic(e.toString());
    }
    // TODO: drop extension modifier from modifiers
    instance.initialize(the_declaration, the_modifier);
    return instance;
  }
}
