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

public class mapping_visitor extends analyzer_visitor {

  private final dictionary<construct, analyzable> mapping =
      new hash_dictionary<construct, analyzable>();

  public void pre_visit(base_analyzer the_analyzable) {
    origin the_origin = the_analyzable.deeper_origin();
    if (!(the_origin instanceof construct)) {
      // System.out.println("A " + the_analyzable + " O " + the_origin);
      return;
    }
    put_analyzable((construct) the_origin, the_analyzable);
  }

  public void post_visit(base_analyzer the_analyzable) {
  }

  public void visit_annotations(base_analyzer the_analyzable, annotation_set the_annotation_set) {
    immutable_list<origin> origins =  the_annotation_set.origins();
    for (int i = 0; i < origins.size(); ++i) {
      origin the_origin = origins.get(i);
      if (the_origin instanceof construct) {
        put_analyzable((construct) the_origin, the_analyzable);
      }
    }
  }

  public @Nullable analyzable get_analyzable(construct c) {
    return mapping.get(c);
  }

  public void put_analyzable(construct c, analyzable a) {
    if (mapping.get(c) != null) {
      if (c instanceof supertype_construct) {
        // TODO: handle multiple supertypes
      } else {
        utilities.panic("C " + c + " OLD " + mapping.get(c) + " NEW " + a);
      }
    }
    mapping.put(c, a);
  }

  @Override
  public string to_string() {
    return utilities.describe(this);
  }
}
