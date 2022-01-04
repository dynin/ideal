/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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

  public static final simple_name XREF_NAME = simple_name.make("xref");
  public static final simple_name INDEX_NAME = simple_name.make("index");

  private printer_util() { }

  public static function1<string, simple_name> dash_renderer =
      new function1<string, simple_name>() {
        @Override
        public string call(simple_name name) {
          readonly_list<string> segments = name.segments;
          StringBuilder s = new StringBuilder();

          for (int i = 0; i < segments.size(); ++i) {
            s.append(utilities.s(segments.get(i)));
            if (i < segments.size() - 1) {
              s.append('-');
            }
          }

          return new base_string(s.toString());
        }
      };

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

  public static @Nullable construct unwrap_name(origin the_origin) {
    if (the_origin instanceof name_construct) {
      return (name_construct) the_origin;
    } else if (the_origin instanceof flavor_construct) {
      return unwrap_name(((flavor_construct) the_origin).expr);
    } else if (the_origin instanceof resolve_construct) {
      return (resolve_construct) the_origin;
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

  public static @Nullable comment_construct extract_summary(@Nullable annotation_set annotations,
      origin the_origin) {

    if (annotations == null) {
      return null;
    }

    @Nullable documentation the_documentation = annotations.the_documentation();
    if (the_documentation == null) {
      return null;
    }

    @Nullable text_fragment summary_text = the_documentation.section(documentation_section.SUMMARY);
    if (summary_text == null) {
      return null;
    }

    // TODO: handle text_fragment here.
    string summary = (base_string) summary_text;
    comment the_comment = new comment(comment_type.BLOCK_DOC_COMMENT, summary, summary);
    // TODO: use summary_text for the_text argument.
    return new comment_construct(the_comment, null, the_origin);
  }
}
