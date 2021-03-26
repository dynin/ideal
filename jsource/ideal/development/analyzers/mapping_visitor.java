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

public class mapping_visitor extends debuggable {

  private final dictionary<construct, analyzable> mapping =
      new hash_dictionary<construct, analyzable>();

  public void visit(analyzable the_analyzable) {
    origin the_origin = the_analyzable.deeper_origin();
    if (the_origin instanceof construct) {
      // System.out.println("A " + the_analyzable + " O " + the_origin);
      put_analyzable((construct) the_origin, the_analyzable);
    }

    readonly_list<analyzable> children = the_analyzable.children();

    for (int i = 0; i < children.size(); ++i) {
      analyzable child = children.get(i);
      if (child instanceof base_annotation_set) {
        visit_annotations(the_analyzable, (base_annotation_set) child);
      } else {
        visit(child);
      }
    }

    if (the_analyzable instanceof declaration_extension) {
      declaration_extension the_extension = (declaration_extension) the_analyzable;
      declaration the_declaration = the_extension.get_declaration();
      origin declaration_origin = the_declaration.deeper_origin();
      if (declaration_origin instanceof construct) {
        deep_map((construct) declaration_origin, the_declaration);
      }
    }
  }

  private void visit_annotations(analyzable the_analyzable, base_annotation_set annotations) {
    immutable_list<origin> origins =  annotations.origins();
    for (int i = 0; i < origins.size(); ++i) {
      origin the_origin = origins.get(i);
      if (the_origin instanceof construct) {
        put_analyzable((construct) the_origin, the_analyzable);
      }
    }
  }

  public @Nullable analyzable get_analyzable(construct the_construct) {
    return mapping.get(the_construct);
  }

  public void put_analyzable(construct the_construct, analyzable the_analyzable) {
    if (mapping.get(the_construct) != null) {
      if (mapping.get(the_construct) == the_analyzable) {
        return;
      }
      if (the_construct instanceof supertype_construct) {
        // TODO: handle multiple supertypes
      } else {
        utilities.panic("C " + the_construct + " OLD " + mapping.get(the_construct) + " NEW " +
            the_analyzable);
      }
    }

    mapping.put(the_construct, the_analyzable);
  }

  private void deep_map(construct the_construct, analyzable the_analyzable) {
    if (mapping.get(the_construct) != null) {
      return;
    }

    put_analyzable(the_construct, the_analyzable);

    readonly_list<construct> children = the_construct.children();
    for (int i = 0; i < children.size(); ++i) {
      deep_map(children.get(i), the_analyzable);
    }
  }

  @Override
  public string to_string() {
    return utilities.describe(this);
  }
}
