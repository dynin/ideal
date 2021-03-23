/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import javax.annotation.Nullable;
import ideal.machine.annotations.dont_display;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.kinds.*;
import ideal.development.modifiers.*;
import ideal.development.declarations.*;
import ideal.development.comments.*;

public abstract class analyzer_visitor extends debuggable {
  public void visit(@Nullable analyzable the_analyzable) {
    if (the_analyzable instanceof base_analyzer) {
      ((base_analyzer) the_analyzable).traverse(this);
    }
  }

  public abstract void pre_visit(base_analyzer the_analyzable);

  public void visit_all(@Nullable readonly_list<analyzable> analyzables) {
    if (analyzables == null) {
      return;
    }

    for (int i = 0; i < analyzables.size(); ++i) {
      visit(analyzables.get(i));
    }
  }

  public abstract void visit_annotations(base_analyzer the_analyzable,
      annotation_set the_annotation_set);

  @Override
  public string to_string() {
    return utilities.describe(this);
  }
}
