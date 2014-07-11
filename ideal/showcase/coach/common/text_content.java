/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.common;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;

/**
 * Text content returned by an HTTP request.
 */
public class text_content {
  public final string mime_type;
  public final string content;

  public text_content(string mime_type, string content) {
    this.mime_type = mime_type;
    this.content = content;
  }

  public String to_content_type() {
    return utilities.s(mime_type) + ";charset=" + utilities.s(resource_util.UTF_8);
  }
}
