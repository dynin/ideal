/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
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

  public static final simple_name IDEAL_NAME = simple_name.make("ideal");

  private final naming_rewriter downstream_rewriter;

  public ideal_rewriter(naming_rewriter downstream_rewriter) {
    this.downstream_rewriter = downstream_rewriter;
  }

  private @Nullable readonly_list<simple_name> rewrite_name(
      @Nullable readonly_list<simple_name> the_name) {
    if (the_name == null) {
      return null;
    }

    if (the_name.is_not_empty() && the_name.first() == common_library.ideal_name) {
      return the_name.skip(1);
    }

    return the_name;
  }

  @Override
  public base_string resource_path(@Nullable readonly_list<simple_name> current_catalog,
      readonly_list<simple_name> target_name, boolean is_xref, extension target_extension) {
    return downstream_rewriter.resource_path(rewrite_name(current_catalog),
        rewrite_name(target_name), is_xref, target_extension);
  }
}
