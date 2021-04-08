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

public class ideal_rewriter extends debuggable implements naming_rewriter {

  // TODO: test.
  @Override
  public base_string resource_path(immutable_list<simple_name> current_catalog,
      readonly_list<simple_name> target_name, extension target_extension) {
    int shared_prefix = 0;

    while (shared_prefix < (current_catalog.size() - 1) &&
           shared_prefix < (target_name.size() - 2) &&
           current_catalog.get(shared_prefix + 1) == target_name.get(shared_prefix + 1)) {
      ++shared_prefix;
    }

    StringBuilder result = new StringBuilder();
    int parent_count = current_catalog.size() - shared_prefix;

    for (int i = 0; i < parent_count; ++i) {
      result.append(utilities.s(resource_util.PARENT_CATALOG));
      result.append(utilities.s(resource_util.PATH_SEPARATOR));
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
