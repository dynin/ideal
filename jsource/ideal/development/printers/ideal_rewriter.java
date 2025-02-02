/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
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

public class ideal_rewriter extends debuggable implements naming_rewriter {

  public static final simple_name DOCUMENTATION_NAME = simple_name.make("documentation");
  public static final simple_name HOMEPAGE_NAME = simple_name.make("homepage");
  public static final simple_name SOURCE_NAME = simple_name.make("source");

  private final naming_rewriter downstream_rewriter;

  public ideal_rewriter(naming_rewriter downstream_rewriter) {
    this.downstream_rewriter = downstream_rewriter;
  }

  private @Nullable readonly_list<simple_name> rewrite_current_name(
      @Nullable readonly_list<simple_name> the_name) {
    if (the_name == null) {
      return null;
    }
    return rewrite_name(the_name);
  }

  private readonly_list<simple_name> rewrite_name(readonly_list<simple_name> the_name) {
    assert the_name.is_not_empty();
    readonly_list<simple_name> old_name = the_name;
    if (the_name.is_not_empty() && the_name.first() == common_names.ideal_name) {
      the_name = the_name.skip(1);
      if (the_name.is_empty()) {
        the_name = new base_list<simple_name>(SOURCE_NAME);
      } if (the_name.size() > 1 && the_name.first() == DOCUMENTATION_NAME) {
        the_name = the_name.skip(1);
        if (the_name.size() == 1) {
          simple_name documentation_name = the_name.first();
          if (documentation_name == HOMEPAGE_NAME) {
            the_name = new empty<simple_name>();
          } else if (documentation_name == printer_util.INDEX_NAME) {
            the_name = new base_list<simple_name>(DOCUMENTATION_NAME);
          }
        }
      }
    }

    return the_name;
  }

  @Override
  public base_string resource_path(@Nullable readonly_list<simple_name> current_name,
      readonly_list<simple_name> target_name, boolean is_xref, extension target_extension) {
    return downstream_rewriter.resource_path(rewrite_current_name(current_name),
        rewrite_name(target_name), is_xref, target_extension);
  }
}
