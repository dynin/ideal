/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.experimental.mini;

import static ideal.experimental.mini.bootstrapped.*;
import static ideal.experimental.mini.library.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public class analysis {

  public static class analysis_context {
    private final Map<type, type_context> type_contexts;
    private final Map<construct, action> bindings;

    public analysis_context() {
      type_contexts = new HashMap<type, type_context>();
      bindings = new HashMap<construct, action>();
    }

    public void add_action(type the_type, String name, action the_action) {
      type_context the_type_context = get_or_create_context(the_type);
      @Nullable action old_action = the_type_context.action_table.put(name, the_action);
      // actions can't be overriden.
      // TODO: signal error during analysis
      assert old_action == null : "Duplicate action for " + name + " in " + the_type;
    }

    public @Nullable action get_action(type the_type, String name) {
      do {
        @Nullable type_context the_type_context = type_contexts.get(the_type);
        if (the_type_context != null) {
          @Nullable action result = the_type_context.action_table.get(name);
          if (result != null) {
            return result;
          }
        }
        if (the_type instanceof principal_type) {
          the_type = ((principal_type) the_type).parent();
        } else {
          break;
        }
      } while (the_type != null);

      return null;
    }

    public void add_supertype(type subtype, type supertype) {
      type_context the_subtype_context = get_or_create_context(subtype);
      boolean unique_super = the_subtype_context.supertypes.add(supertype);
      type_context the_supertype_context = get_or_create_context(supertype);
      boolean unique_sub = the_supertype_context.subtypes.add(subtype);
      // TODO: signal error during analysis
      if (!unique_super || !unique_sub) {
        panic("Duplicate: subtype " + describe_s(subtype) + ", supertype " + describe_s(supertype));
      }
    }

    public Set<type> get_all_supertypes(type the_type) {
      Set<type> result = new HashSet<type>();
      add_supertypes_helper(the_type, result);
      return result;
    }

    // TODO: the recursion can take up a lot of stack space.
    public void add_supertypes_helper(type the_type, Set<type> result) {
      @Nullable type_context the_type_context = type_contexts.get(the_type);
      if (the_type_context != null) {
        for (type candidate : the_type_context.supertypes) {
          if (!result.contains(candidate)) {
            result.add(candidate);
            add_supertypes_helper(candidate, result);
          }
        }
      }
    }

    public Set<type> get_direct_subtypes(type the_type) {
      @Nullable type_context the_type_context = type_contexts.get(the_type);
      if (the_type_context != null) {
        return the_type_context.subtypes;
      } else {
        return Collections.emptySet();
      }
    }

    public void add_binding(construct the_construct, action the_action) {
      action old_action = bindings.put(the_construct, the_action);
      assert old_action == null;
    }

    public @Nullable action get_binding(construct the_construct) {
      return bindings.get(the_construct);
    }

    private type_context get_or_create_context(type the_type) {
      @Nullable type_context the_type_context = type_contexts.get(the_type);
      if (the_type_context == null) {
        the_type_context = new type_context();
        type_contexts.put(the_type, the_type_context);
      }
      return the_type_context;
    }

    // Implementation detail of analysis_context.
    private static class type_context {
      final Map<String, action> action_table;
      final Set<type> supertypes;
      final Set<type> subtypes;

      public type_context() {
        action_table = new HashMap<String, action>();
        supertypes = new HashSet<type>();
        subtypes = new LinkedHashSet<type>();
      }
    }
  }

  public static class analyzer extends construct_dispatch<action> {
    private final analysis_context the_analysis_context;
    private principal_type parent;
    private analysis_pass pass;

    public analyzer(analysis_context the_analysis_context) {
      this.the_analysis_context = the_analysis_context;
    }

    public action analyze(construct the_construct, principal_type parent, analysis_pass pass) {
      principal_type old_parent = this.parent;
      analysis_pass old_pass = this.pass;

      this.parent = parent;
      this.pass = pass;

      action result = call(the_construct);

      this.parent = old_parent;
      this.pass = old_pass;

      return result;
    }

    public void analyze_all(List<construct> constructs, principal_type parent, analysis_pass pass) {
      for (construct the_construct : constructs) {
        analyze(the_construct, parent, pass);
      }
    }

    @Override
    public action call_construct(construct the_construct) {
      error_signal error_result =
        new error_signal(
          new notification_message_class(notification_type.ANALYSIS_ERROR,
              "Can't handle " + describe_type(the_construct) + " in " + pass),
          the_construct);

      feedback.report(error_result);

      return error_result;
    }

    @Override
    public action call_identifier(identifier the_identifier) {
      @Nullable action result = the_analysis_context.get_binding(the_identifier);
      if (result != null) {
        return result;
      }

      result = resolve(parent, the_identifier);

      the_analysis_context.add_binding(the_identifier, result);
      return result;
    }

    @Override
    public action call_operator(operator the_operator) {
      return call_construct(the_operator);
    }

    @Override
    public action call_string_literal(string_literal the_string_literal) {
      return the_string_literal;
    }

    public action resolve(type the_type, identifier the_identifier) {
      String name = the_identifier.name();
      @Nullable action result = the_analysis_context.get_action(the_type, name);

      if (result != null) {
        return result;
      } else {
        error_signal error_result = new error_signal(
            new notification_message_class(notification_type.SYMBOL_LOOKUP_FAILED,
                "Symbol lookup failed for '" + name  + "'"),
            the_identifier);
        feedback.report(error_result);
        return error_result;
      }
    }

    public action analyze_resolve(construct qualifier, construct name) {
      action qualifier_action = analyze(qualifier, parent, pass);
      if (qualifier_action instanceof error_signal) {
        return qualifier_action;
      }

      if (!(name instanceof identifier)) {
        error_signal result = new error_signal(notification_type.IDENTIFIER_EXPECTED, name);
        feedback.report(result);
        return result;
      }

      identifier the_identifier = (identifier) name;
      return resolve(qualifier_action.result(), the_identifier);
    }

    @Override
    public action call_parameter_construct(parameter_construct the_parameter_construct) {
      @Nullable action result = the_analysis_context.get_binding(the_parameter_construct);
      if (result != null) {
        return result;
      }

      result = do_call_parameter_construct(the_parameter_construct);

      assert result != null;
      the_analysis_context.add_binding(the_parameter_construct, result);

      return result;
    }

    private action do_call_parameter_construct(parameter_construct the_parameter_construct) {
      if (the_parameter_construct.main() instanceof operator) {
        operator_type the_operator_type = ((operator) the_parameter_construct.main()).
            the_operator_type();
        if (the_operator_type == operator_type.DOT) {
          List<construct> parameters = the_parameter_construct.parameters();
          assert parameters.size() == 2;
          return analyze_resolve(parameters.get(0), parameters.get(1));
        }
      }

      action main_action = analyze(the_parameter_construct.main(), parent, pass);
      if (main_action instanceof error_signal) {
        return main_action;
      }

      List<construct> parameters = the_parameter_construct.parameters();
      List<action> parameter_actions = new ArrayList<action>();

      for (int i = 0; i < parameters.size(); ++i) {
        action parameter_action = analyze(parameters.get(i), parent, pass);
        // TODO: process more parameters
        if (parameter_action instanceof error_signal) {
          return parameter_action;
        }
        parameter_actions.add(parameter_action);
      }

      if (!is_parametrizable(main_action)) {
        error_signal result = new error_signal(notification_type.NOT_PARAMETRIZABLE,
            the_parameter_construct.main());
        feedback.report(result);
        return result;
      }

      principal_type main_principal = (principal_type) ((type_action) main_action).the_type();

      if (parameter_actions.size() != 1) {
        error_signal result = new error_signal(notification_type.WRONG_ARITY,
            the_parameter_construct);
        feedback.report(result);
        return result;
      }

      action parameter_action = parameter_actions.get(0);
      if (! (parameter_action instanceof type_action)) {
        error_signal result = new error_signal(notification_type.TYPE_EXPECTED,
            the_parameter_construct.main());
        feedback.report(result);
        return result;
      }

      type parameter_type = ((type_action) parameter_action).the_type();
      List<type> parameter_types = new ArrayList<type>();
      parameter_types.add(parameter_type);
      parametrized_type result_type = new parametrized_type(main_principal, parameter_types);

      return new type_action_class(result_type, the_parameter_construct);
    }

    private static boolean is_parametrizable(action main_action) {
      if (main_action instanceof type_action) {
        type main_type = ((type_action) main_action).the_type();
        return main_type == core_type.NULLABLE || main_type == core_type.LIST;
      } else {
        return false;
      }
    }

    @Override
    public action call_modifier_construct(modifier_construct the_modifier_construct) {
      return call_construct(the_modifier_construct);
    }

    @Override
    public action call_s_expression(s_expression the_s_expression) {
      return call_construct(the_s_expression);
    }

    @Override
    public action call_block_construct(block_construct the_block_construct) {
      return call_construct(the_block_construct);
    }

    @Override
    public action call_return_construct(return_construct the_return_construct) {
      return call_construct(the_return_construct);
    }

    @Override
    public action call_variable_construct(variable_construct the_variable_construct) {
      if (pass == analysis_pass.TYPE_PASS) {
        return null;
      }

      if (pass == analysis_pass.MEMBER_PASS) {
        @Nullable construct type_construct = the_variable_construct.type();
        assert type_construct != null; // TODO: signal error otherwise
        action the_type_action = analyze(type_construct, parent, pass);
        if (!(the_type_action instanceof type_action)) {
          error_signal result = new error_signal(notification_type.TYPE_EXPECTED,
              type_construct);
          feedback.report(result);
          return result;
        }
        type result_type = ((type_action) the_type_action).the_type();
        String name = the_variable_construct.name();
        variable_declaration the_declaration = new variable_declaration(result_type, name, parent,
            type_construct);
        // TODO: handle readonly flavor.
        the_analysis_context.add_action(parent, name, the_declaration);
        return the_declaration;
      }

      assert pass == analysis_pass.BODY_PASS;
      @Nullable construct initializer = the_variable_construct.initializer();
      if (initializer != null) {
        principal_type init_frame = new principal_type_class(the_variable_construct.name(),
            type_kind.BLOCK, parent);
        add_this_variable(parent, init_frame, the_variable_construct);
        analyze(initializer, init_frame, pass);
      }
      return null;
    }

    private void add_this_variable(principal_type declared_type, principal_type inner_frame,
        source the_source) {
      variable_declaration this_declaration = new variable_declaration(declared_type,
          create.THIS_NAME, inner_frame, the_source);
      the_analysis_context.add_action(inner_frame, create.THIS_NAME, this_declaration);
    }

    @Override
    public action call_procedure_construct(procedure_construct the_procedure_construct) {
      if (pass == analysis_pass.TYPE_PASS) {
        return null;
      }

      if (pass == analysis_pass.MEMBER_PASS) {
        @Nullable construct return_type_construct = the_procedure_construct.return_type();
        if (return_type_construct == null) {
          error_signal result = new error_signal(notification_type.TYPE_EXPECTED,
              the_procedure_construct);
          feedback.report(result);
          return result;
        }
        action return_type_action = analyze(return_type_construct, parent, pass);
        if (!(return_type_action instanceof type_action)) {
          error_signal result = new error_signal(notification_type.TYPE_EXPECTED,
              return_type_construct);
          feedback.report(result);
          return result;
        }
        type return_type = ((type_action) return_type_action).the_type();
        String name = the_procedure_construct.name();

        principal_type parameter_frame = new principal_type_class(name, type_kind.BLOCK, parent);
        List<variable_declaration> parameters = new ArrayList<variable_declaration>();
        for (variable_construct the_parameter : the_procedure_construct.parameters()) {
          analyze(the_parameter, parameter_frame, pass);
        }

        procedure_declaration the_declaration = new procedure_declaration(return_type, name,
            parameters, parent, the_procedure_construct);
        // the_analysis_context.add_action(parent, name, the_declaration);
        return the_declaration;
      }

      assert pass == analysis_pass.BODY_PASS;
      return null;
    }

    @Override
    public action call_dispatch_construct(dispatch_construct the_dispatch_construct) {
      if (pass != analysis_pass.MEMBER_PASS) {
        return null;
      }

      action the_type_action = analyze(the_dispatch_construct.the_type(), parent, pass);
      if (!(the_type_action instanceof type_action)) {
        error_signal result = new error_signal(notification_type.TYPE_EXPECTED,
            the_dispatch_construct.the_type());
        feedback.report(result);
      }

      return null;
    }

    @Override
    public action call_supertype_construct(supertype_construct the_supertype_construct) {
      if (pass != analysis_pass.MEMBER_PASS) {
        return null;
      }

      for (construct supertype : the_supertype_construct.supertypes()) {
        action supertype_action = analyze(supertype, parent, pass);
        if (!(supertype_action instanceof type_action)) {
          error_signal result = new error_signal(notification_type.TYPE_EXPECTED, supertype);
          feedback.report(result);
          continue;
        }
        type the_supertype = ((type_action) supertype_action).the_type();
        the_analysis_context.add_supertype(parent, the_supertype);
      }

      return null;
    }

    @Override
    public action call_type_construct(type_construct the_type_construct) {
      principal_type declared_type;
      type_declaration the_type_declaration;
      type_kind the_type_kind = the_type_construct.the_type_kind();

      if (pass == analysis_pass.TYPE_PASS) {
        declared_type = new principal_type_class(the_type_construct.name(), the_type_kind, parent);
        the_type_declaration = new type_declaration(declared_type, the_type_kind,
            the_type_construct);
        the_analysis_context.add_binding(the_type_construct, the_type_declaration);
        the_analysis_context.add_action(parent, the_type_construct.name(),
            new type_action_class(declared_type, the_type_declaration));
      } else {
        the_type_declaration = (type_declaration)
            the_analysis_context.get_binding(the_type_construct);
        assert the_type_declaration != null;
        declared_type = the_type_declaration.declared_type();
      }

      if (the_type_kind != type_kind.ENUM) {
        analyze_all(the_type_construct.body(), declared_type, pass);
        if (the_type_kind == type_kind.SINGLETON && pass == analysis_pass.MEMBER_PASS) {
          singleton_literal the_literal = new singleton_literal(declared_type,
              the_type_declaration);
          the_analysis_context.add_action(declared_type, create.INSTANCE_NAME, the_literal);
        }
      } else {
        int enum_ordinal = 0;
        for (construct the_construct : the_type_construct.body()) {
          if (is_enum_declaration.call(the_construct)) {
            if (pass != analysis_pass.MEMBER_PASS) {
              continue;
            }

            identifier the_identifier;
            if (the_construct instanceof identifier) {
              the_identifier = (identifier) the_construct;
            } else {
              parameter_construct the_parameter_construct = (parameter_construct) the_construct;
              construct main = the_parameter_construct.main();
              if (main instanceof identifier) {
                the_identifier = (identifier) main;
              } else {
                feedback.report(new error_signal(notification_type.IDENTIFIER_EXPECTED, main));
                continue;
              }
            }
            String name = the_identifier.name();
            enum_literal the_literal = new enum_literal(name, enum_ordinal, declared_type,
                the_construct);
            the_analysis_context.add_action(declared_type, name, the_literal);
            enum_ordinal += 1;
          } else {
            analyze(the_construct, declared_type, pass);
          }
        }
      }

      return the_type_declaration;
    }
  }

  public static predicate<construct> is_enum_declaration = new predicate<construct>() {
    @Override
    public boolean call(construct the_construct) {
      return the_construct instanceof identifier ||
             the_construct instanceof parameter_construct;
    }
  };
}
