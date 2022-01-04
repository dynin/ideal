-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

implicit import ideal.runtime.elements;
import ideal.machine.channels.string_writer;

--- Procedure parameters encapsulated in actions.
class action_parameters {
  extends debuggable;
  implements deeply_immutable data, stringable;

  static EMPTY : action_parameters.new(empty[action].new());

  immutable list[action] parameters;

  action_parameters(readonly list[action] parameters) {
    this.parameters = parameters.frozen_copy;
  }

  var nonnegative arity => parameters.size;

  override string to_string() {
    the_writer : string_writer.new();
    the_writer.write('[');
    -- TODO: list.join(), or range operator
    for (var nonnegative i : 0; i < parameters.size; i += 1) {
      the_writer.write_all(parameters[i].to_string);
      if (i != parameters.size - 1) {
        the_writer.write_all(", ");
      }
    }
    the_writer.write(']');
    return the_writer.elements;
  }
}
