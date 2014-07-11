/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.reflections;

import ideal.library.elements.*;
import ideal.library.texts.*;

import javax.annotation.Nullable;

/**
 * The framework for an ideal framework application hosted on AppEngine.
 */
public class translation_result {
  private final boolean success;
  private final @Nullable datastore_schema new_declaration;
  private final @Nullable text_fragment error_messages;

  public boolean is_success() {
    return success;
  }

  public datastore_schema get_new_declaration() {
    assert success && new_declaration != null;
    return new_declaration;
  }

  public text_fragment get_error_messages() {
    assert !success && error_messages != null;
    return error_messages;
  }

  public translation_result(datastore_schema new_declaration) {
    this.success = true;
    this.new_declaration = new_declaration;
    this.error_messages = null;
  }

  public translation_result(text_fragment error_messages) {
    this.success = false;
    this.new_declaration = null;
    this.error_messages = error_messages;
  }
}
