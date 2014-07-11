/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.comments.*;

import javax.annotation.Nullable;

/**
 * Generates cross-reference links that are inserted in the output by printers.
 */
public interface printer_assistant {
  @Nullable string make_link(construct the_construct);
  @Nullable documentation get_documentation(construct the_construct);
}
