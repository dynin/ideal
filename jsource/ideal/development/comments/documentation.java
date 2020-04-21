/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.comments;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public interface documentation extends readonly_data {
  @Nullable text_fragment section(documentation_section the_section);
}
