/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import ideal.library.texts.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.development.elements.*;
import ideal.development.components.*;
import ideal.development.comments.*;
import ideal.development.names.*;
import ideal.development.literals.*;
import ideal.development.scanners.*;
import ideal.development.modifiers.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.declarations.*;
import ideal.development.extensions.*;
import ideal.development.documenters.*;

public class printer_util {

  private printer_util() { }

  // TODO: move to extensions?
  public static boolean has_not_yet_implemented(readonly_list<annotation_construct> annotations) {
    // TODO: use list.has()
    for (int i = 0; i < annotations.size(); ++i) {
      annotation_construct a = annotations.get(i);
      if (a instanceof modifier_construct &&
          ((modifier_construct) a).the_kind ==
              not_yet_implemented_extension.instance.the_modifier_kind) {
        return true;
      }
    }
    return false;
  }

  public static base_string print_simple_name(simple_name name, boolean is_stylish) {
    readonly_list<string> segments = name.segments;
    StringBuilder s = new StringBuilder();

    for (int i = 0; i < segments.size(); ++i) {
      s.append(utilities.s(segments.get(i)));
      if (i < segments.size() - 1) {
        // TODO: output nbsp's; more control?
        s.append(is_stylish ? ' ' : '_');
      }
    }

    return new base_string(s.toString());
  }

  public static @Nullable name_construct unwrap_name(origin the_origin) {
    if (the_origin instanceof name_construct) {
      return (name_construct) the_origin;
    } else if (the_origin instanceof flavor_construct) {
      return unwrap_name(((flavor_construct) the_origin).expr);
    } else if (the_origin instanceof resolve_construct) {
      return unwrap_name(((resolve_construct) the_origin).name);
    } else if (the_origin instanceof parameter_construct) {
      return unwrap_name(((parameter_construct) the_origin).main);
    } else {
      return null;
    }
  }

  public static @Nullable construct find_construct(@Nullable origin the_origin) {
    while (the_origin != null) {
      if (the_origin instanceof construct) {
        return (construct) the_origin;
      }
      the_origin = the_origin.deeper_origin();
    }
    return null;
  }

  public static @Nullable type_declaration to_type_declaration(@Nullable origin the_origin) {
    @Nullable type_declaration result = declaration_util.to_type_declaration(the_origin);
    if (result != null) {
      return result.master_declaration();
    } else {
      return null;
    }
  }
}
