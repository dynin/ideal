/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.elements;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.library.messages.log_message;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

// TODO: importance/severity level.
// TODO: move from string to text.
public interface notification extends log_message {
  string message();
  position position();
  @Nullable readonly_list<notification> secondary();
  // TODO: pass a render_context that contains styles?
  text_fragment render_text(boolean prefix_with_source);
  void report();
}
