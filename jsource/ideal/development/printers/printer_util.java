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
import ideal.development.documenters.*;

public class printer_util {

  private printer_util() { }

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
}
