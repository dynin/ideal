/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.extensions;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.components.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.values.*;
import ideal.development.analyzers.*;
import ideal.development.kinds.*;

public class package_construct extends extension_construct {
  public final construct package_name;

  public package_construct(construct package_name, origin pos) {
    super(pos);
    this.package_name = package_name;
  }

  @Override
  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>();

    result.append(package_name);

    return result;
  }

  @Override
  public analyzable to_analyzable() {
    return base_analyzable_action.nothing(this);
  }

  @Override
  public text_fragment print(printer p) {
    list<text_fragment> fragments = new base_list<text_fragment>();

    fragments.append(p.print_simple_name(type_kinds.package_kind.name()));
    fragments.append(p.print_space());
    fragments.append(p.print(package_name));

    // TODO: semicolon?..  Newline?
    return text_util.join(fragments);
  }

  @Override
  public boolean is_terminated() {
    return false;
  }

  @Override
  public construct transform(transformer t) {
    return this;
  }
}
