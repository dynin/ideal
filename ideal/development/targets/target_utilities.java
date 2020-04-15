/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.targets;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;
import ideal.development.analyzers.*;

public class target_utilities {

  public static readonly_list<type_declaration> get_declared_types(
      type_declaration the_type_declaration) {

    readonly_list<declaration> signature = the_type_declaration.get_signature();
    list<type_declaration> result = new base_list<type_declaration>();

    for (int i = 0; i < signature.size(); ++i) {
      declaration the_declaration = signature.get(i);
      if (the_declaration instanceof type_declaration) {
        result.append((type_declaration) the_declaration);
      } else if (the_declaration instanceof type_announcement_analyzer) {
        // TODO: should this be in get_signature?
        result.append(((type_announcement_analyzer) the_declaration).get_type_declaration());
      }
    }

    return result;
  }
}
