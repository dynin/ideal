/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
import ideal.development.analyzers.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;

import javax.annotation.Nullable;

public class file_rewriter extends debuggable implements naming_rewriter {

  private readonly_list<simple_name> make_xref_target(readonly_list<simple_name> target) {
    assert target.is_not_empty();
    list<simple_name> xref_names = new base_list<simple_name>();
    xref_names.append_all(target.slice(0, target.size() - 1));

    simple_name last_name = target.last();
    xref_names.append(name_utilities.join(last_name, printer_util.XREF_NAME));

    return xref_names;
  }

  // TODO: test.
  @Override
  public base_string resource_path(@Nullable readonly_list<simple_name> current_name,
      readonly_list<simple_name> target_name, boolean is_xref, extension target_extension) {

    if (target_name.is_empty()) {
      target_name = new base_list<simple_name>(printer_util.INDEX_NAME);
    }

    if (is_xref) {
      target_name = make_xref_target(target_name);
    }

    int shared_prefix = 0;
    StringBuilder result = new StringBuilder();

    if (current_name != null && current_name.size() > 1) {
      while (shared_prefix < (current_name.size() - 1) &&
             shared_prefix < (target_name.size() - 1) &&
             current_name.get(shared_prefix) == target_name.get(shared_prefix)) {
        ++shared_prefix;
      }

      int parent_count = current_name.size() - shared_prefix - 1;

      for (int i = 0; i < parent_count; ++i) {
        result.append(utilities.s(resource_util.PARENT_CATALOG));
        result.append(utilities.s(resource_util.PATH_SEPARATOR));
      }
    }

    for (int i = shared_prefix; i < target_name.size(); ++i) {
      result.append(utilities.s(printer_util.dash_renderer.call(target_name.get(i))));
      if (i == target_name.size() - 1) {
        result.append(utilities.s(target_extension.dot_name()));
      } else {
        result.append(utilities.s(resource_util.PATH_SEPARATOR));
      }
    }

    return new base_string(result.toString());
  }
}
