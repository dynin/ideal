/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tools;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;
import ideal.development.analyzers.*;

public class create_util {

  public static boolean DEBUG_PROGRESS;

  public static void progress(String name) {
    if (DEBUG_PROGRESS) {
      // TODO: add timing info
      log.info(new base_string("============ ", name));
    }
  }

  public static void progress_loading(resource_identifier source_id) {
    if (DEBUG_PROGRESS) {
      log.info(new base_string("==== Loading ", source_id.to_string()));
    }
  }

  public static readonly_list<type_declaration> get_declared_types(
      type_declaration the_type_declaration) {

    readonly_list<declaration> signature = the_type_declaration.get_signature();
    list<type_declaration> result = new base_list<type_declaration>();

    for (int i = 0; i < signature.size(); ++i) {
      declaration the_declaration = signature.get(i);
      if (the_declaration instanceof type_declaration) {
        result.append((type_declaration) the_declaration);
      } else if (the_declaration instanceof type_announcement_analyzer) {
        // TODO: should this be in get_signature?
        result.append(((type_announcement_analyzer) the_declaration).get_type_declaration());
      }
    }

    return result;
  }
}
