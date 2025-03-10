/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.machine.resources;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.channels.*;
import ideal.runtime.resources.*;

import java.io.*;
import java.net.*;

import javax.annotation.Nullable;

public class network extends base_readonly_store {

  public static final resource_catalog NETWORK_CATALOG = new network().top();

  protected network() {
    super(new base_string("https:"), true, true);
  }

  protected InputStream get_stream(String name) throws IOException {
    return new URL(name).openStream();
  }

  @Override
  public @Nullable readonly_set<string> read_catalog(string scheme, immutable_list<string> path) {
    return null;
  }

  @Override
  protected string default_scheme() {
    return resource_util.HTTPS_SCHEME;
  }

  @Override
  public boolean allow_scheme(string scheme) {
    // TODO: make scheme an enum
    String s = utilities.s(scheme).toLowerCase();
    return s.equals("https") || s.equals("http");
  }

  @Override
  public string build_name(string scheme, immutable_list<string> path) {
    string path_only = super.build_name(scheme, path);
    return new base_string(scheme, ":", path_only);
  }

  public static resource_identifier url(string url_string) {
    return NETWORK_CATALOG.resolve(url_string);
  }
}
