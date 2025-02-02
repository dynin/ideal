/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import ideal.library.graphs.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.kinds.*;
import ideal.development.declarations.*;

public class mismatch_reporter {

  public static final value_printer printer = base_value_printer.instance;

  private static readonly_list<action> filter_by_arity(readonly_list<action> candidates,
      int arity) {

    list<action> selected = new base_list<action>();
    // TODO: use filter.
    for (int i = 0; i < candidates.size(); ++i) {
      action candidate = candidates.get(i);
      type result_type = candidate.result().type_bound();
      if (common_types.is_procedure_type(result_type) &&
          common_types.is_valid_procedure_arity(result_type, arity)) {
        selected.append(candidate);
      }
    }

    return selected;
  }

  static error_signal signal_not_matching(readonly_list<action> candidates,
      action_parameters the_action_parameters, readonly_list<origin> action_origins,
      action_context context, origin the_origin) {

    //dump_dependencies(context.type_graph());
    assert candidates.is_not_empty();

    readonly_list<action> filtered_candidates = filter_by_arity(candidates,
        the_action_parameters.arity());

    if (filtered_candidates.is_empty()) {
      return new error_signal(new base_notification(
          new base_string("Can't find declarations with matching arity"),
          the_origin, notification_utilities.to_notifications(candidates, printer)), false);
    }

    if (filtered_candidates.size() > 1) {
      notification no_matching = new base_notification(
          new base_string("Can't find matching declaration for " +
              print_parameters(the_action_parameters) + " parameters"),
          the_origin, notification_utilities.to_notifications(filtered_candidates, printer));
      return new error_signal(no_matching, false);
    }

    assert filtered_candidates.size() == 1;
    return signal_mismatch(filtered_candidates.first(), the_action_parameters, action_origins,
        context, the_origin);
  }

  static error_signal signal_mismatch(action candidate, action_parameters the_action_parameters,
      readonly_list<origin> action_origins, action_context context, origin the_origin) {

    type failed_procedure_type = candidate.result().type_bound();
    if (!common_types.is_procedure_type(failed_procedure_type)) {
      return new error_signal(new base_string(messages.expression_not_parametrizable + ": " +
          printer.print_value(failed_procedure_type)), the_origin);
    }

    assert common_types.is_procedure_type(failed_procedure_type);

    immutable_list<action> supplied_arguments = the_action_parameters.parameters;

    if (!common_types.is_valid_procedure_arity(failed_procedure_type,
        supplied_arguments.size())) {
      return new error_signal(new base_string(supplied_arguments.size() +
          " parameter(s) not supported"), the_origin);
    }

    for (int i = 0; i < supplied_arguments.size(); ++i) {
      action supplied_action = supplied_arguments.get(i);
      abstract_value supplied_value = supplied_action.result();
      type declared_type = common_types.get_procedure_argument(
          failed_procedure_type, i).type_bound();

      if (!context.can_promote(supplied_action, declared_type)) {
        origin action_origin;
        if (action_origins != null && i < action_origins.size()) {
          action_origin = action_origins.get(i);
        } else {
          action_origin = the_origin;
        }
        return new error_signal(new base_string("Argument #" + i + ": expected " +
            printer.print_value(declared_type) + ", found " +
            printer.print_value(supplied_value)),
            action_origin);
        // TODO: origin that highlights argument while showing the full expression
        // TODO: continue and report other arg mismatches
      }
    }

    // This should never happen.
    new base_notification(new base_string("All arguments appear to match"), the_origin).report();
    for (int i = 0; i < supplied_arguments.size(); ++i) {
      abstract_value supplied_value = supplied_arguments.get(i).result();
      type declared_type = common_types.get_procedure_argument(
          failed_procedure_type, i).type_bound();
      log.debug("MM #" + i + ": expected " +
            printer.print_value(declared_type) + ", found " +
            printer.print_value(supplied_value) +
            " @ " + (((base_principal_type) supplied_value.type_bound().principal()).get_pass()));
    }

    utilities.panic("Unexpected: all procedure arguments match...");
    return new error_signal(new base_string("Bad arguments " + failed_procedure_type), the_origin);
  }

  static error_signal signal_lookup_failure(action_name the_name, type from_type,
      action_parameters the_action_parameters, origin source) {
    base_string error_message = new base_string(new base_string("Lookup failed: no "),
        the_name.to_string(), new base_string(" in "), printer.print_value(from_type));
    if (the_action_parameters != null) {
      error_message = new base_string(error_message, ", target parameters ",
          print_parameters(the_action_parameters));
    }
    return new error_signal(error_message, source);
  }

  private static string print_parameters(action_parameters the_action_parameters) {
    immutable_list<action> parameters = the_action_parameters.parameters;
    StringBuilder s = new StringBuilder();
    boolean first = true;
    s.append('(');

    if (parameters.is_not_empty()) {
      for (int i = 0; i < parameters.size(); ++i) {
        if (first) {
          first = false;
        } else {
          s.append(", ");
        }
        s.append(utilities.s(printer.print_value(parameters.get(i).result())));
      }
    }

    s.append(')');
    return new base_string(s.toString());
  }

  public static error_signal reference_expected(type the_type, origin the_origin) {
    return new error_signal(new base_string("Reference expected, got ",
        printer.print_value(the_type)), the_origin);
  }

  public static error_signal writable_reference_expected(type the_type, origin the_origin) {
    return new error_signal(new base_string("Writable reference expected, got ",
        printer.print_value(the_type)), the_origin);
  }

  // TODO: this is for debug only.
  public static void dump_dependencies(readonly_graph<principal_type, origin> the_type_graph) {
    readonly_list<principal_type> all_types = the_type_graph.vertices().elements();
    for (int i = 0; i < all_types.size(); ++i) {
      principal_type the_type = all_types.get(i);
      log.debug("" + the_type);
      readonly_list<principal_type> adjacent = the_type_graph.adjacent(the_type).elements();
      for (int j = 0; j < adjacent.size(); ++j) {
        log.debug("    " + adjacent.get(j));
      }
    }
  }
}
