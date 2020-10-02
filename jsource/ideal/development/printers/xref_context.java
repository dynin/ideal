/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.library.texts.*;
import ideal.library.channels.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.runtime.logs.*;
import ideal.runtime.texts.*;
import ideal.runtime.resources.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.comments.*;
import ideal.development.constructs.*;
import ideal.development.declarations.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;

import javax.annotation.Nullable;

public class xref_context extends debuggable {
  private final static int num_modes = xref_mode.values().length;

  private final dictionary<declaration, list<declaration>>[] mapping;

  public xref_context() {
    mapping = new dictionary[num_modes * 2];
    for (int i = 0; i < mapping.length; ++i) {
      mapping[i] = new list_dictionary<declaration, list<declaration>>();
    }
  }

  public void add(declaration source, xref_mode the_xref_mode, declaration target) {
    add_mapping(source, the_xref_mode.ordinal(), target);
    add_mapping(target, num_modes + the_xref_mode.ordinal(), source);
  }

  public @Nullable declaration get_target(@Nullable declaration source, xref_mode the_xref_mode) {
    return get_mapping(source, the_xref_mode.ordinal());
  }

  public @Nullable declaration get_source(@Nullable declaration target, xref_mode the_xref_mode) {
    return get_mapping(target, num_modes + the_xref_mode.ordinal());
  }

  private void add_mapping(declaration source, int slot, declaration target) {
    dictionary<declaration, list<declaration>> the_dictionary = mapping[slot];
    list<declaration> the_list = the_dictionary.get(source);
    if (the_list == null) {
      the_list = new base_list<declaration>();
      the_dictionary.put(source, the_list);
    }
    the_list.append(target);
  }

  private @Nullable readonly_list<declaration> get_mapping_list(@Nullable declaration source,
      int slot) {
    if (source == null) {
      return null;
    }
    dictionary<declaration, list<declaration>> the_dictionary = mapping[slot];
    list<declaration> the_list = the_dictionary.get(source);
    if (the_list == null) {
      return null;
    }
    return the_list;
  }

  private @Nullable declaration get_mapping(@Nullable declaration source, int slot) {
    @Nullable readonly_list<declaration> declarations = get_mapping_list(source, slot);
    if (declarations == null) {
      return null;
    }
    assert declarations.size() <= 1;
    if (declarations.is_empty()) {
      return null;
    } else {
      return declarations.first();
    }
  }
}
