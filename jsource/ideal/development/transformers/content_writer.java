/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.transformers;

import ideal.library.elements.*;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;
import ideal.runtime.logs.*;
import ideal.machine.channels.standard_channels;
import ideal.development.elements.*;
import ideal.development.scanners.*;

import javax.annotation.Nullable;

// TODO: refactor into two implementations: output_catalog and stdout
public class content_writer implements value {

  private final @Nullable resource_catalog output_catalog;
  private final @Nullable function1<string, simple_name> name_formatter;

  public content_writer(@Nullable resource_catalog output_catalog,
      @Nullable function1<string, simple_name> name_formatter) {
    this.output_catalog = output_catalog;
    this.name_formatter = name_formatter;
  }

  private resource_identifier make_output(readonly_list<simple_name> full_name,
      extension the_extension) {

    assert full_name.is_not_empty();

    resource_catalog catalog = output_catalog;
    for (int i = 0; i < full_name.size() - 1; ++i) {
      catalog = catalog.resolve(format_name(full_name.get(i))).access_catalog();
    }
    simple_name last_name = full_name.get(full_name.size() - 1);
    return catalog.resolve(format_name(last_name), the_extension);
  }

  private string format_name(simple_name the_name) {
    if (name_formatter == null) {
      return the_name.to_string();
    } else {
      return name_formatter.call(the_name);
    }
  }

  private void do_write(string content, @Nullable resource_identifier output) {
    if (output != null) {
      log.info(new base_string("Writing to ", output.to_string()));
      output.access_string(make_catalog_option.instance).content().set(content);
    } else {
      standard_channels.stdout.write_all(content);
      standard_channels.stdout.sync();
    }
  }

  public void write(string content, string resource_name) {
    if (output_catalog != null) {
      do_write(content, output_catalog.resolve(resource_name));
    } else {
      do_write(content, null);
    }
  }

  public void write(string content, readonly_list<simple_name> full_name,
      extension the_extension) {
    if (output_catalog != null) {
      do_write(content, make_output(full_name, the_extension));
    } else {
      do_write(content, null);
    }
  }
}
