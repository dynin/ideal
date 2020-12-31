/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tools;

import ideal.library.elements.*;

class create_options {
  public string top;
  public string input;
  public string output;
  public string target;

  public boolean RUN;
  public boolean PRINT;
  public boolean PRETTY_PRINT;
  public boolean GENERATE;

  public boolean CURE_UNDECLARED;
  public boolean HIDE_DECLARATIONS;

  public boolean DEBUG_CONSTRUCTS;
  public boolean DEBUG_PROGRESS;
  public boolean DEBUG_IMPORT;
  public boolean DEBUG_REFLECT;
}
