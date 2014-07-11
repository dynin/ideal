/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.appengine;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.channels.*;
import ideal.runtime.resources.*;
import ideal.machine.resources.*;

import java.io.*;
import javax.servlet.*;

public class servlet_resources extends base_readonly_store {

  private static final string PREFIX = new base_string("/WEB-INF/");

  private final ServletContext context;

  public servlet_resources(ServletContext context) {
    super(PREFIX);
    assert context != null;
    this.context = context;
  }

  @Override
  protected InputStream get_stream(String name) {
    return context.getResourceAsStream(name);
  }
}
