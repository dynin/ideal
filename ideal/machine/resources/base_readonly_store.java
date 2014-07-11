/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.machine.resources;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.channels.*;
import ideal.runtime.resources.*;

import java.io.*;

public abstract class base_readonly_store extends base_resource_store {

  protected base_readonly_store(string path_prefix) {
    super(path_prefix, false, false);
  }

  protected abstract InputStream get_stream(String name);

  @Override
  public boolean exists(immutable_list<string> path) {
    InputStream input = get_stream(utilities.s(build_name(path)));
    if (input != null) {
      try {
        input.close();
      } catch (IOException e) {
        // whatever.
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public string read_string(immutable_list<string> path) {
    try {
      Reader reader = new InputStreamReader(get_stream(utilities.s(build_name(path))),
          utilities.s(resource_util.UTF_8));
      string result = io_util.read(reader);
      reader.close();
      return result;
    } catch (IOException e) {
      io_util.io_error(e);
      return new base_string("");
    }
  }


  @Override
  public void write_string(immutable_list<string> path, string new_value) {
    utilities.panic("Attempted base_readonly_store.write_string()");
  }

  @Override
  public void make_catalog(immutable_list<string> path) {
    utilities.panic("Attempted base_readonly_store.make_catalog()");
  }
}
