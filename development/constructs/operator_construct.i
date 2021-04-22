-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public class operator_construct extends base_construct {
  public final operator the_operator;
  public final readonly_list<construct> arguments;

  public operator_construct(operator the_operator, readonly_list<construct> arguments,
      origin source) {
    super(source);
    assert arguments.size() == the_operator.the_operator_type.arity;
    this.the_operator = the_operator;
    this.arguments = arguments;
  }

  public operator_construct(operator the_operator, construct argument, origin source) {
    this(the_operator, new base_list<construct>(argument), source);
  }

  public operator_construct(operator the_operator, construct left, construct right,
      origin source) {
    this(the_operator, new base_list<construct>(left, right), source);
  }

  public readonly_list<construct> children() {
    return arguments;
  }
}
