/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.printers;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.library.texts.*;
import ideal.library.channels.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.runtime.logs.*;
import ideal.runtime.texts.*;
import ideal.runtime.resources.*;
import ideal.machine.channels.string_writer;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.comments.*;
import ideal.development.constructs.*;
import ideal.development.declarations.*;
import ideal.development.actions.*;
import ideal.development.analyzers.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;

import javax.annotation.Nullable;

/**
 * Prepare the site files for deployment.
 * Rule in .htaccess:
 * RewriteRule "(.*)/xref$" "$1/xref.html"
 */
public class website_rewriter extends debuggable implements naming_rewriter {

  // TODO: test.
  @Override
  public base_string resource_path(@Nullable readonly_list<simple_name> current_name,
      readonly_list<simple_name> target_name, boolean is_xref, extension target_extension) {

    boolean is_file_name = current_name == null;
    boolean is_html = target_extension == base_extension.HTML;
    string_writer result = new string_writer();

    for (int i = 0; i < target_name.size(); ++i) {
      if (i > 0 || !is_file_name) {
        result.write_all(resource_util.PATH_SEPARATOR);
      }
      result.write_all(printer_util.dash_renderer.call(target_name.get(i)));
    }

    if (is_html) {
      if (target_name.is_not_empty() || !is_file_name) {
        result.write_all(resource_util.PATH_SEPARATOR);
      }

      if (is_file_name) {
        if (is_xref) {
          result.write_all(printer_util.dash_renderer.call(printer_util.XREF_NAME));
        } else {
          result.write_all(printer_util.dash_renderer.call(printer_util.INDEX_NAME));
        }
        result.write_all(base_extension.HTML.dot_name());
      } else {
        if (is_xref) {
          result.write_all(printer_util.dash_renderer.call(printer_util.XREF_NAME));
        }
      }
    } else {
      assert target_name.is_not_empty();
      result.write_all(target_extension.dot_name());
    }

    return (base_string) result.elements();
  }
}
