/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.tools;

public class status {
  public final boolean is_ok;

  private status(boolean is_ok) {
    this.is_ok = is_ok;
  }

  public static final status ok = new status(true);

  public static final status error = new status(false);
}
