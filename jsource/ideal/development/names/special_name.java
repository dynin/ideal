/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.names;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

// TODO: merge this with special_token_name.
public class special_name extends debuggable implements action_name {

  public static final special_name ROOT = new special_name("root");

  public static final special_name PROMOTION = new special_name("promotion");
  public static final special_name TYPE_ALIAS = new special_name("type_alias");
  public static final special_name THIS = new special_name(keyword.THIS);
  public static final special_name SUPER = new special_name(keyword.SUPER);
  public static final special_name NEW = new special_name(keyword.NEW);
  public static final special_name IMPLICIT_CALL = new special_name("implicit_call");
  public static final special_name SUPERTYPE = new special_name("supertype");
  public static final special_name CONSTRUCTOR = new special_name("constructor");

  public static final special_name THIS_CONSTRUCTOR = new special_name("this_constructor");
  public static final special_name SUPER_CONSTRUCTOR = new special_name("super_constructor");

  /* Used to a name the return pseudo-variable */
  public static final special_name RETURN = new special_name(keyword.RETURN);

  /* Used in a list constructor; (1, 2) gets exposed as <list>(1, 2) */
  public static final action_name SEQUENCE = new special_name("list");

  public final string name;

  public special_name(token_type ttype) {
    this.name = ttype.name();
  }

  public special_name(String name) {
    this.name = new base_string(name);
  }

  // TODO: retire this?
  public special_name(String name, Class type) {
    this(name);
  }

  public string to_string() {
    return name_utilities.in_brackets(name);
  }
}
