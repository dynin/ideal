/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.appengine;

import ideal.library.elements.*;
import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.development.scanners.source_content;
import ideal.showcase.coach.reflections.*;
public interface user_state {
  string app_name();
  source_content js_runtime();
  void clear_world_cache();
  datastore_state get_world_state();
  void set_world_state(datastore_state world);
  void checkpoint_world_state(datastore_state world);
  translation_result translate_source(string new_source);
  void reset_source();
}
