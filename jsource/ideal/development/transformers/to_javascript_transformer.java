/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.transformers;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.runtime.reflections.*;
import ideal.development.elements.*;
import ideal.development.literals.*;
import ideal.development.actions.*;
import ideal.development.analyzers.*;
import ideal.development.declarations.*;
import ideal.development.notifications.*;
import ideal.development.constructs.*;
import ideal.development.modifiers.*;
import ideal.development.flavors.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.extensions.*;
import ideal.development.templates.*;

public class to_javascript_transformer {

  private static final simple_name METHOD_PREFIX = simple_name.make("method");
  private static final simple_name GET_FIELD = simple_name.make("get_field");
  private static final simple_name THIS_DATA = simple_name.make("this_data");
  /*
  private static final simple_name LENGTH = simple_name.make("length");
  private static final simple_name ELEMENTS = simple_name.make("elements");
  private static final string LIST_PREFIX = new base_string("list");
  private static final string INDEX_PREFIX = new base_string("index");
  */

  private int name_index = 0;
  private dictionary<variable_declaration, simple_name> var_names =
      new hash_dictionary<variable_declaration, simple_name>();

  public construct to_construct_action(action the_action) {
    assert the_action != null;
    if (the_action.result() instanceof error_signal) {
      return signal((error_signal) the_action.result());
    }


    if (the_action instanceof bound_procedure) {
      return to_construct((bound_procedure) the_action);
    } else if (the_action instanceof data_value_action) {
      return to_construct((data_value_action) the_action);
    } else if (the_action instanceof variable_declaration) {
      return to_construct((variable_declaration) the_action);
    } else if (the_action instanceof variable_initializer) {
      return to_construct((variable_initializer) the_action);
    } else if (the_action instanceof local_variable) {
      return to_construct((local_variable) the_action);
    } else if (the_action instanceof chain_action) {
      chain_action the_chain_action = (chain_action) the_action;
      if (the_chain_action.second instanceof instance_variable) {
        return process_instance_variable(the_chain_action.first,
            (instance_variable) the_chain_action.second);
      } else if (the_chain_action.second instanceof dispatch_action) {
        return process_dispatch_action(the_chain_action.first,
            (dispatch_action) the_chain_action.second);
      } else if (the_chain_action.second instanceof dereference_action) {
        return to_construct_action(the_chain_action.first);
      }
    } else if (the_action instanceof return_action) {
      return to_construct((return_action) the_action);
    } else if (the_action instanceof extension_action) {
      return to_construct((extension_action) the_action);
    } else if (the_action instanceof promotion_action) {
      return to_construct((promotion_action) the_action);
    } else if (the_action instanceof list_action) {
      return to_construct((list_action) the_action);
    }

    return signal(new error_signal(new base_string("Unimplemented: " + the_action), the_action));
  }

  private static construct signal(error_signal e) {
    e.cause.report();
    return new literal_construct(new quoted_literal(e.to_string(), punctuation.DOUBLE_QUOTE), e);
  }

  public @Nullable construct to_construct_or_null(@Nullable action the_action) {
    if (the_action == null) {
      return null;
    } else {
      return to_construct_action(the_action);
    }
  }

  public @Nullable construct to_construct_analyzer(@Nullable analyzable the_analyzable) {
    if (the_analyzable == null) {
      return null;
    } else {
      return to_construct_action((action) the_analyzable.analyze());
    }
  }

  public list<construct> to_param_list(readonly_list<action> actions) {
    list<construct> result = new base_list<construct>();
    for (int i = 0; i < actions.size(); ++i) {
      result.append(to_construct_action(actions.get(i)));
    }
    return result;
  }

  public @Nullable construct to_body(action the_action) {
    if (the_action == null) {
      return null;
    }

    list<construct> result = new base_list<construct>();
    append_action(the_action, result);

    if (result.is_empty()) {
      return null;
    }

    return new block_construct(result, the_action);
  }

  private void append_action(action the_action, list<construct> result) {
    if (the_action instanceof error_signal) {
      return;
    }

    if (the_action instanceof list_action) {
      readonly_list<action> subactions = ((list_action) the_action).elements();
      for (int i = 0; i < subactions.size(); ++i) {
        append_action(subactions.get(i), result);
      }
    } else {
      result.append(to_construct_action(the_action));
    }
  }

  public variable_construct to_construct(variable_declaration variable) {
    return make_variable(variable, variable.init_action(), variable);
  }

  public variable_construct make_variable(variable_declaration variable, @Nullable action init,
      origin pos) {

    list<annotation_construct> annotations = make_var_annotation(pos);
    action_name name = variable.short_name();

    if (name instanceof special_name) {
      if (name == special_name.THIS) {
        name = THIS_DATA;
      } else {
        // TODO: handle other special names...
        simple_name new_name = generate_unique_name(((special_name) name).name);
        var_names.put(variable, new_name);
        name = new_name;
      }
    }

    @Nullable construct init_construct = to_construct_or_null(init);

    return new variable_construct(annotations, null, name, new empty<annotation_construct>(),
        init_construct, pos);
  }

  public variable_construct to_construct(variable_initializer initializer) {
    return make_variable(initializer.the_variable_action.the_declaration, initializer.init,
        initializer);
  }

  public variable_construct make_this_declaration(type this_type, origin pos) {
    readonly_list<annotation_construct> annotations = new empty<annotation_construct>();

    return new variable_construct(annotations, null, THIS_DATA,  new empty<annotation_construct>(),
        null, pos);
  }

  private action_name make_proc_name(procedure_analyzer procedure) {
    simple_name type_name = (simple_name) procedure.declared_in_type().short_name();
    simple_name proc_name = procedure.original_name();
    return name_utilities.join(name_utilities.join(METHOD_PREFIX, type_name), proc_name);
  }

  public procedure_construct to_construct(procedure_analyzer procedure) {
    origin pos = procedure;
    readonly_list<annotation_construct> annotations = new empty<annotation_construct>();
    action_name name = make_proc_name(procedure);
    list<construct> parameters = new base_list<construct>();
    // TODO: handle static methods.
    parameters.append(make_this_declaration(procedure.declared_in_type(), pos));
    parameters.append_all(parameters_to_constructs(procedure.get_parameter_variables(), pos));
    readonly_list<annotation_construct> post_annotations = new empty<annotation_construct>();
    @Nullable construct body = to_body(procedure.get_body_action());

    return new procedure_construct(annotations, null, name, parameters,
        post_annotations, body, pos);
  }

  public return_construct to_construct(return_action the_action) {
    return new return_construct(to_construct_or_null(the_action.expression), the_action);
  }

  public operator map_operator(operator the_operator) {
    if (the_operator == operator.CONCATENATE) {
      return operator.ADD;
    } else if (the_operator == operator.CONCATENATE_ASSIGN) {
      return operator.ADD_ASSIGN;
    } else {
      return the_operator;
    }
  }

  public construct to_construct(bound_procedure bp) {
    origin pos = bp;
    action proc_action = bp.the_procedure_action;
    procedure_value proc;
    if (proc_action instanceof data_value_action) {
      value_wrapper the_value = ((data_value_action) proc_action).the_value;
      proc = (base_procedure) the_value;
      //return to_construct((data_value_action) proc_action);
    } else if (proc_action instanceof procedure_value) {
      // TODO: handle other procedure value.
      proc = (procedure_value) proc_action;
    } else if (proc_action instanceof dispatch_action) {
      // TODO: handle other procedure value.
      action primary = ((dispatch_action) proc_action).get_primary();
      value_wrapper the_value = ((data_value_action) primary).the_value;
      proc = (base_procedure) the_value;
    } else {
      return signal(new error_signal(
          new base_string("Unsupported procedure: " + proc_action), pos));
    }

    action_name the_name = proc.name();
    if (the_name == special_name.IMPLICIT_CALL) {
      the_name = simple_name.make("get");
    }

    list<construct> params = to_param_list(bp.parameters.params());

    if (the_name instanceof operator) {
      return new operator_construct(map_operator((operator) the_name), params, pos);
    }

    construct main = new name_construct(the_name, pos);
    if (proc instanceof procedure_with_this) {
      procedure_with_this pt = (procedure_with_this) proc;
      if (pt.this_action != null) {
        main = new resolve_construct(to_construct_action(pt.this_action), the_name, pos);
      }
    }

    return new parameter_construct(main, params, grouping_type.PARENS, pos);
  }

  public construct process_dispatch_action(action from_action,
      dispatch_action the_dispatch_action) {
    action primary = the_dispatch_action.get_primary();
    if (from_action != null) {
      primary = action_utilities.combine(from_action, primary, the_dispatch_action);
    }
    return to_construct_action(primary);
  }

  private static final boolean ACCESS_FIELDS_DIRECTLY = false;

  public construct process_instance_variable(action from_action, instance_variable fa) {
    origin pos = fa;
    assert from_action != null;
    construct from = to_construct_action(from_action);
    simple_name the_name = (simple_name) fa.short_name();

    if (ACCESS_FIELDS_DIRECTLY) {
      return new resolve_construct(from, the_name, pos);
    } else {
      construct get_field = new resolve_construct(from, GET_FIELD, pos);
      string field_name = the_name.to_string();
      construct field_value = new literal_construct(new quoted_literal(field_name,
          punctuation.SINGLE_QUOTE), pos);
      return new parameter_construct(get_field, new base_list<construct>(field_value),
          grouping_type.PARENS, pos);
    }
  }

  public construct to_construct(local_variable lv) {
    origin pos = lv;
    variable_declaration decl = lv.the_declaration;
    return new name_construct(process_name(decl), pos);
  }

  public construct to_construct(extension_action the_extension_action) {
    action extended_action = the_extension_action.extended_action;
    extension_analyzer analyzer = the_extension_action.get_extension();
    origin the_origin = the_extension_action;

    if (analyzer instanceof list_iteration_analyzer) {
      return to_construct_action(extended_action);
    } else if (analyzer instanceof for_analyzer) {
      for_analyzer the_for_analyzer = (for_analyzer) analyzer;
      return new for_construct(
          to_construct_analyzer(the_for_analyzer.init),
          to_construct_analyzer(the_for_analyzer.condition),
          to_construct_analyzer(the_for_analyzer.update),
          to_construct_analyzer(the_for_analyzer.body),
          the_origin);
    } else {
      return signal(new error_signal(
          new base_string("Unknown extension: " + analyzer), the_origin));
    }
  }

  public construct to_construct(list_action the_list_action) {
    origin the_origin = the_list_action;
    return new block_construct(to_param_list(the_list_action.elements()), the_origin);
  }

  /*
  public construct to_construct(list_iteration_action iteration_action) {
    list_iteration_analyzer iteration = iteration_action.source;
    origin pos = iteration_action;

    list<annotation_construct> annotations = make_var_annotation(pos);
    simple_name list_name = generate_unique_name(LIST_PREFIX);
    name_construct list_name_construct = new name_construct(list_name, pos);
    construct init = to_construct_action(iteration.init_action);
    init = new resolve_construct(init, new name_construct(ELEMENTS, pos), pos);
    variable_construct list_var =
        new variable_construct(annotations, null, list_name, new empty<annotation_construct>(),
            init, pos);

    simple_name index_name = generate_unique_name(INDEX_PREFIX);
    name_construct index_name_construct = new name_construct(index_name, pos);
    action_name loop_var = iteration.var_name;
    variable_construct index_init = new variable_construct(annotations, null, index_name,
         new empty<annotation_construct>(), new literal_construct(new integer_literal(0), pos),
         pos);
    construct list_length = new resolve_construct(list_name_construct,
        new name_construct(LENGTH, pos), pos);
    construct index_cond = new operator_construct(operator.LESS, index_name_construct,
        list_length, pos);
    construct index_update = new operator_construct(operator.PRE_INCREMENT, index_name_construct,
        pos);

    list<construct> body_constructs = new base_list<construct>();
    construct get_element = new parameter_construct(list_name_construct,
        new list_construct(new base_list<construct>(index_name_construct),
            grouping_type.BRACKETS, false, pos), pos);
    variable_construct element_var =
        new variable_construct(annotations, null, loop_var, new empty<annotation_construct>(),
        get_element, pos);
    body_constructs.append(element_var);
    append_action(iteration.body_action(), body_constructs);
    construct loop_body = new block_construct(body_constructs, pos);

    construct for_loop = new for_construct(index_init, index_cond, index_update, loop_body, pos);

    list<construct> iteration_body = new base_list<construct>();
    iteration_body.append(list_var);
    iteration_body.append(for_loop);

    return new block_construct(iteration_body, pos);
  }
  */

  private type immutable_string_type() {
    return common_library.get_instance().immutable_string_type();
  }

  private type immutable_integer_type() {
    return common_library.get_instance().immutable_integer_type();
  }

  public construct to_construct(data_value_action the_action) {
    origin pos = the_action;
    value_wrapper the_value = the_action.the_value;

    if (the_value instanceof string_value) {
      return new literal_construct(
          new quoted_literal(((string_value) the_value).unwrap(), punctuation.SINGLE_QUOTE), pos);
    } else if (the_value instanceof integer_value) {
      return new literal_construct(
          new integer_literal(((integer_value) the_value).unwrap()), pos);
    }

    return signal(new error_signal(new base_string("Unsupported value: " + the_value.getClass()),
          pos));
  }

  public construct to_construct(promotion_action the_action) {
    @Nullable action unwrapped = the_action.get_action();
    assert unwrapped != null;
    return to_construct_action(unwrapped);
  }

  private action_name process_name(variable_declaration decl) {
    action_name name = decl.short_name();
    if (name instanceof special_name) {
      if (name == special_name.THIS) {
        return THIS_DATA;
      }
      name = var_names.get(decl);
      if (name == null) {
        utilities.panic("Unknown special name in " + decl);
      }
    }
    return name;
  }

  private simple_name generate_unique_name(string prefix) {
    return simple_name.make("$" + utilities.s(prefix) + (name_index++));
  }

  private list<annotation_construct> make_var_annotation(origin pos) {
    list<annotation_construct> result = new base_list<annotation_construct>();
    result.append(new modifier_construct(general_modifier.var_modifier, pos));
    return result;
  }

  private list<construct> parameters_to_constructs(
      readonly_list<variable_declaration> params_actions, origin pos) {
    list<construct> result = new base_list<construct>();
    for (int i = 0; i < params_actions.size(); ++i) {
      result.append(to_construct(params_actions.get(i)));
    }
    return result;
  }
}
