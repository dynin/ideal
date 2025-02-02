/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.tools;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;
import ideal.development.flags.*;
import ideal.development.analyzers.*;

public class create_util {

  public static void progress(String name) {
    if (debug.PROGRESS) {
      // TODO: add timing info
      log.info(new base_string("============ ", name));
    }
  }

  public static void progress_loading(resource_identifier source_id) {
    if (debug.PROGRESS) {
      log.info(new base_string("==== Loading ", source_id.to_string()));
    }
  }
}
