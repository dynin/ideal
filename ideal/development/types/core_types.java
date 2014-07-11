/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.types;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;

public class core_types {

  private final static master_type ROOT;
  private final static master_type ERROR;
  private final static master_type ANY_TYPE;
  private final static master_type UNREACHABLE;
  private final static master_type TARGET;
  private final static master_type UNDEFINED;
  private final static master_type UNION_MASTER;

  static {
    ROOT = new master_type(new special_name("root"), type_kinds.block_kind);
    ANY_TYPE = new master_type(new special_name("any_type"), type_kinds.block_kind);
    ERROR = new master_type(new special_name("error"), type_kinds.block_kind);
    UNREACHABLE = new master_type(new special_name("unreachable"), type_kinds.block_kind);
    TARGET = new master_type(new special_name("unreachable"), type_kinds.block_kind);
    UNDEFINED = new master_type(new special_name("undefined"), type_kinds.block_kind);
    UNION_MASTER = new master_union_type(new special_name("union"), type_kinds.union_kind);
  }

  public static principal_type root_type() {
    return ROOT;
  }

  public static principal_type any_type() {
    return ANY_TYPE;
  }

  public static type error_type() {
    return ERROR;
  }

  public static type unreachable_type() {
    return UNREACHABLE;
  }

  public static type target_type() {
    return TARGET;
  }

  public static type undefined_type() {
    return UNDEFINED;
  }

  public static master_type union_master_type() {
    return UNION_MASTER;
  }

  public static void set_context(type_declaration_context the_context) {
    ROOT.set_context(the_context);
    ERROR.set_context(the_context);
    UNREACHABLE.set_context(the_context);
    UNDEFINED.set_context(the_context);
    UNION_MASTER.set_context(the_context);
  }
}
