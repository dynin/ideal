/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.scanners;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;

public class source_content extends debuggable
    implements deeply_immutable_data, position, stringable {

  public final identifier name;
  public final string content;

  public source_content(resource_identifier module_id) {
    this.name = module_id;
    this.content = module_id.access_string(null).content().get();
  }

  public source_content(identifier name, string content) {
    this.name = name;
    this.content = content;
  }

  public @Nullable position source_position() {
    return null;
  }

  public position make_position(int begin, int end) {
    return new text_position(this, begin, end);
  }

  // TODO: this can be optimized...
  public int line_number(text_position pos) {
    assert pos.source == this;
    assert pos.begin <= content.size();
    // TODO: use list.count()
    int count = 0;
    for (int i = 0; i < pos.begin; ++i) {
      if (content.get(i) == '\n') {
        ++count;
      }
    }
    // Note: we count lines starting from 1
    return count + 1;
  }

  @Override
  public string to_string() {
    return utilities.describe(this, name);
  }
}
