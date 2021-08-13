/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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

import javax.annotation.Nullable;

public class file_store extends base_resource_store {

  protected file_store(string path_prefix, boolean is_current) {
    super(path_prefix, is_current, is_current);
  }

  @Override
  public boolean allow_scheme(string scheme) {
    String s = utilities.s(scheme);
    return s.equals(utilities.s(resource_util.FILE_SCHEME));
  }

  @Override
  public boolean exists(string scheme, immutable_list<string> path) {
    return new File(utilities.s(build_name(scheme, path))).exists();
  }

  @Override
  public string read_string(string scheme, immutable_list<string> path) {
    try {
      // TODO: cache content
      Reader reader =  new FileReader(utilities.s(build_name(scheme, path)));
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
  public void write_string(string scheme, immutable_list<string> path, string new_value) {
    try {
      Writer writer = new FileWriter(utilities.s(build_name(scheme, path)));
      writer.write(utilities.s(new_value));
      writer.close();
    } catch (IOException e) {
      io_util.io_error(e);
    }
  }

  @Override
  public void make_catalog(string scheme, immutable_list<string> path) {
    // TODO: return error?...
    File directory = new File(utilities.s(build_name(scheme, path)));
    // Potential race condition here (directory can be deleted).
    // But we don't care.
    if (!directory.exists()) {
      directory.mkdirs();
    }
  }


  @Override
  public @Nullable readonly_set<string> read_catalog(string scheme, immutable_list<string> path) {
    String name = utilities.s(build_name(scheme, path));

    String[] filenames = new File(name).list();
    if (filenames == null) {
      return null;
    }

    hash_set<string> result = new hash_set<string>();

    for (int i = 0; i < filenames.length; ++i) {
      result.add(new base_string(filenames[i]));
    }

    return result;
  }

  @Override
  protected string default_scheme() {
    return resource_util.FILE_SCHEME;
  }
}
