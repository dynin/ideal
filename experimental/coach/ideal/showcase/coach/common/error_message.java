/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.common;

/**
 * Encapsulates an error message that should be displayed to the user.
 */
public class error_message {
  public final String content;

  public error_message(String content) {
    this.content = content;
  }

  public String to_content_type() {
    return "error: " + content;
  }
}
