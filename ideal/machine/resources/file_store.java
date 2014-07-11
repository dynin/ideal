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

public class file_store extends base_resource_store {

  protected file_store(string path_prefix, boolean is_current) {
    super(path_prefix, is_current, is_current);
  }

  @Override
  public boolean exists(immutable_list<string> path) {
    return new File(utilities.s(build_name(path))).exists();
  }

  @Override
  public string read_string(immutable_list<string> path) {
    try {
      // TODO: cache content
      Reader reader =  new FileReader(utilities.s(build_name(path)));
      string result = io_util.read(reader);
      reader.close();
      return result;
    } catch (IOException e) {
      // TODO: propagate exception?
      io_util.io_error(e);
      return new base_string("");
    }
  }

  @Override
  public void write_string(immutable_list<string> path, string new_value) {
    try {
      Writer writer = new FileWriter(utilities.s(build_name(path)));
      writer.write(utilities.s(new_value));
      writer.close();
    } catch (IOException e) {
      io_util.io_error(e);
    }
  }

  @Override
  public void make_catalog(immutable_list<string> path) {
    // TODO: return error?...
    File directory = new File(utilities.s(build_name(path)));
    // Potential race condition here (directory can be deleted).
    // But we don't care.
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }
}
