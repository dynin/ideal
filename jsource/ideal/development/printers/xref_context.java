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

  private final dictionary<origin, list<origin>>[] mapping;

  public xref_context() {
    mapping = new dictionary[num_modes * 2];
    for (int i = 0; i < mapping.length; ++i) {
      mapping[i] = new list_dictionary<origin, list<origin>>();
    }
  }

  public void add(origin source, xref_mode the_xref_mode, origin target) {
    add_mapping(source, the_xref_mode.ordinal(), target);
    add_mapping(target, num_modes + the_xref_mode.ordinal(), source);
  }

  public @Nullable origin get_target(@Nullable origin source, xref_mode the_xref_mode) {
    return get_mapping(source, the_xref_mode.ordinal());
  }

  public @Nullable origin get_source(@Nullable origin target, xref_mode the_xref_mode) {
    return get_mapping(target, num_modes + the_xref_mode.ordinal());
  }

  private void add_mapping(origin source, int slot, origin target) {
    dictionary<origin, list<origin>> the_dictionary = mapping[slot];
    list<origin> the_list = the_dictionary.get(source);
    if (the_list == null) {
      the_list = new base_list<origin>();
      the_dictionary.put(source, the_list);
    }
    the_list.append(target);
  }

  private @Nullable readonly_list<origin> get_mapping_list(@Nullable origin source,
      int slot) {
    if (source == null) {
      return null;
    }
    dictionary<origin, list<origin>> the_dictionary = mapping[slot];
    list<origin> the_list = the_dictionary.get(source);
    if (the_list == null) {
      return null;
    }
    return the_list;
  }

  private @Nullable origin get_mapping(@Nullable origin source, int slot) {
    @Nullable readonly_list<origin> origins = get_mapping_list(source, slot);
    if (origins == null) {
      return null;
    }
    assert origins.size() <= 1;
    if (origins.is_empty()) {
      return null;
    } else {
      return origins.first();
    }
  }
}
