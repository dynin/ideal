/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.experimental.mini;

import static ideal.experimental.mini.bootstrapped.*;
import static ideal.experimental.mini.library.*;

public class names {

  public static final String THIS_NAME = "this";

  public static final String INSTANCE_NAME = "instance";

  public static final String DESCRIBABLE_NAME = "describable";

  public static final String RESULT_NAME = "result";

  public static final String FUNCTION_NAME = "function";

  public static final String CALL_NAME = "call";

  public static final String THE_NAME = "the";

  public static String join_identifier(String first, String second) {
    return first + '_' + second;
  }
}
