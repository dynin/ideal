/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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

import javax.annotation.Nullable;

public class servlet_resources extends base_readonly_store {

  private static final string PREFIX = new base_string("/WEB-INF/");

  private final ServletContext context;

  @Override
  protected string default_scheme() {
    return resource_util.FILE_SCHEME;
  }

  public servlet_resources(ServletContext context) {
    super(PREFIX, false, false);
    assert context != null;
    this.context = context;
  }

  @Override
  public boolean allow_scheme(string scheme) {
    String s = utilities.s(scheme);
    return s.equals(utilities.s(resource_util.FILE_SCHEME));
  }

  @Override
  public @Nullable readonly_set<string> read_catalog(string scheme, immutable_list<string> path) {
    return null;
  }

  @Override
  protected InputStream get_stream(String name) {
    return context.getResourceAsStream(name);
  }
}
