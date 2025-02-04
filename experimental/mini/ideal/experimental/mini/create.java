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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public class create {

  public enum analysis_pass {
    TYPE_PASS,
    MEMBER_PASS,
    BODY_PASS;
  }

  public static final String THIS_NAME = "this";

  public static final String INSTANCE_NAME = "instance";

  public static final String DESCRIBABLE_NAME = "describable";

  public static final String RESULT_NAME = "result";

  public static final String FUNCTION_NAME = "function";

  public static final String CALL_NAME = "call";

  public static final String THE_NAME = "the";

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
      variable_declaration this_declaration = new variable_declaration(declared_type, THIS_NAME,
          inner_frame, the_source);
      the_analysis_context.add_action(inner_frame, THIS_NAME, this_declaration);
    }

    @Override
    public action call_procedure_construct(procedure_construct the_procedure_construct) {
      return call_construct(the_procedure_construct);
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
          the_analysis_context.add_action(declared_type, INSTANCE_NAME, the_literal);
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

  public static abstract class base_transform extends construct_dispatch<construct> {

    public construct transform(construct the_construct) {
      return call(the_construct);
    }

    public List<construct> transform_all(List<construct> constructs) {
      // TODO: rewrite using map()
      List<construct> result = new ArrayList<construct>();
      for (construct the_construct : constructs) {
        if (the_construct instanceof type_construct) {
          result.addAll(transform_type((type_construct) the_construct));
        } else {
          result.add(transform(the_construct));
        }
      }
      return result;
    }

    @Override
    public construct call_construct(construct the_construct) {
      return the_construct;
    }

    public abstract List<construct> transform_type(type_construct the_type_construct);
  }

  public static predicate<construct> is_enum_declaration = new predicate<construct>() {
    @Override
    public boolean call(construct the_construct) {
      return the_construct instanceof identifier ||
             the_construct instanceof parameter_construct;
    }
  };

  public static class to_java_transform extends base_transform {

    private final analysis_context the_analysis_context;
    private final Map<principal_type, String> type_mapping;
    private final @Nullable type describable_type;

    public to_java_transform(analysis_context the_analysis_context) {
      this.the_analysis_context = the_analysis_context;
      this.type_mapping = new HashMap<principal_type, String>();
      type_mapping.put(core_type.INTEGER, "int");
      type_mapping.put(core_type.STRING, "String");
      type_mapping.put(core_type.LIST, "List");

      action describable_action = the_analysis_context.get_action(top_type.instance,
          DESCRIBABLE_NAME);
      if (describable_action instanceof type_action) {
        describable_type = ((type_action) describable_action).the_type();
      } else {
        describable_type = null;
      }
    }

    protected @Nullable action get_binding(construct the_construct) {
      return the_analysis_context.get_binding(the_construct);
    }

    @Override
    public construct call_parameter_construct(parameter_construct the_parameter_construct) {
      source the_source = the_parameter_construct;

      construct main = the_parameter_construct.main();
      construct transformed_main = transform(main);

      List<construct> parameters = new ArrayList<construct>();
      for (construct the_parameter : the_parameter_construct.parameters()) {
        Object transformed = call(the_parameter);
        if (transformed instanceof construct) {
          parameters.add((construct) transformed);
        } else {
          panic("Parameter transform error: " + the_parameter);
        }
      }

      grouping_type grouping = the_parameter_construct.grouping();
      @Nullable action main_action = get_binding(main);
      if (main_action instanceof type_action &&
          ((type_action) main_action).the_type() == core_type.LIST) {
        grouping = grouping_type.ANGLE_BRACKETS;
      }

      construct result = new parameter_construct(transformed_main, parameters, grouping,
          the_source);

      @Nullable action the_action = get_binding(the_parameter_construct);
      if (the_action instanceof variable_declaration) {
        result = new parameter_construct(result, new ArrayList<construct>(), grouping_type.PARENS,
            the_source);
      }

      return result;
    }

    @Override
    public variable_construct call_variable_construct(variable_construct the_variable_construct) {
      assert the_variable_construct.type() != null;
      source the_source = the_variable_construct;
      List<modifier_construct> modifiers = the_variable_construct.modifiers();
      construct type = transform(the_variable_construct.type());
      if (is_nullable(type)) {
        type = strip_nullable(type);
        modifiers = prepend_modifier(modifier_kind.NULLABLE, modifiers, the_source);
      }
      // TODO: add variable_construct.has_initializer
      @Nullable construct initializer = the_variable_construct.initializer();
      if (initializer != null) {
        initializer = transform(initializer);
      }
      return new variable_construct(modifiers, type, the_variable_construct.name(), initializer,
          the_source);
    }

    private static List<modifier_construct> prepend_modifier(modifier_kind the_modifier_kind,
        List<modifier_construct> modifiers, source the_source) {
      List<modifier_construct> result = new ArrayList<modifier_construct>();
      result.add(new modifier_construct(the_modifier_kind, the_source));
      result.addAll(modifiers);
      return result;
    }

    private static List<modifier_construct> filter_modifier(final modifier_kind the_modifier_kind,
        List<modifier_construct> modifiers) {
      return filter(modifiers, new predicate<modifier_construct>() {
        @Override
        public boolean call(modifier_construct the_modifier_construct) {
          return the_modifier_construct.the_modifier_kind() != the_modifier_kind;
        }
      });
    }

    private static String join_identifier(String first, String second) {
      return first + '_' + second;
    }

    private static parameter_construct make_operator(operator_type the_operator_type,
        construct first_argument, construct second_argument, source the_source) {
      List<construct> arguments = new ArrayList<construct>();
      arguments.add(first_argument);
      arguments.add(second_argument);
      return new parameter_construct(new operator(the_operator_type, the_source), arguments,
          grouping_type.OPERATOR, the_source);
    }

    @Override
    public construct call_type_construct(type_construct the_type_construct) {
      List<construct> transformed = transform_type(the_type_construct);

      if (transformed.size() != 1) {
        panic("One transformed type expected");
      }

      return transformed.get(0);
    }

    @Override
    public List<construct> transform_type(type_construct the_type_construct) {
      type_declaration the_declaration = (type_declaration) get_binding(the_type_construct);
      assert the_declaration != null;

      type_kind the_type_kind = the_type_construct.the_type_kind();

      boolean declare_interface =
          the_type_kind == type_kind.INTERFACE ||
          the_type_kind == type_kind.DATATYPE;

      boolean declare_implementation =
          the_type_kind == type_kind.DATATYPE ||
          the_type_kind == type_kind.ENUM ||
          the_type_kind == type_kind.CLASS ||
          the_type_kind == type_kind.SINGLETON;

      boolean declare_enum = the_type_kind == type_kind.ENUM;

      boolean declare_singleton = the_type_kind == type_kind.SINGLETON;

      source the_source = the_type_construct;

      String interface_name = the_type_construct.name();
      String implementation_name = declare_interface ? join_identifier(interface_name, "class") :
          interface_name;

      List<construct> interface_body = new ArrayList<construct>();
      List<construct> implementation_body = new ArrayList<construct>();

      if (declare_interface && declare_implementation) {
        List<construct> super_interface = new ArrayList<construct>();
        super_interface.add(new identifier(interface_name, the_source));
        implementation_body.add(new supertype_construct(supertype_kind.IMPLEMENTS, super_interface,
            the_source));
      }
      List<variable_construct> ctor_parameters = new ArrayList<variable_construct>();
      List<construct> ctor_statements = new ArrayList<construct>();
      List<construct> accessor_functions = new ArrayList<construct>();

      List<String> describe_fields = new ArrayList<String>();

      for (construct the_construct : the_type_construct.body()) {
        if (the_construct instanceof supertype_construct) {
          // TODO: implement supertype transformation.
          supertype_construct the_supertype_construct =
              (supertype_construct) transform(the_construct);
          if (declare_interface) {
            interface_body.add(the_supertype_construct);
          } else {
            implementation_body.add(the_supertype_construct);
          }
        } else if (the_construct instanceof variable_construct) {
          variable_construct the_variable_construct =
              call_variable_construct((variable_construct) the_construct);
          List<modifier_construct> modifiers = the_variable_construct.modifiers();

          boolean has_override = has_modifier(modifiers, modifier_kind.OVERRIDE);
          if (has_override) {
            modifiers = filter_modifier(modifier_kind.OVERRIDE, modifiers);
          }
          boolean has_dont_describe = has_modifier(modifiers, modifier_kind.DONT_DESCRIBE);
          if (has_dont_describe) {
            modifiers = filter_modifier(modifier_kind.DONT_DESCRIBE, modifiers);
          }

          construct type = the_variable_construct.type();
          String name = the_variable_construct.name();

          // Add accessor declaration to the interface
          if (declare_interface) {
            interface_body.add(new procedure_construct(modifiers,
                type, name, new ArrayList<variable_construct>(), null, the_source));
          }

          if (declare_implementation) {
            boolean has_initializer = the_variable_construct.initializer() != null;

            if (!has_initializer) {
              // Add instance variable
              List<modifier_construct> instance_variable_modifiers =
                  prepend_modifier(modifier_kind.PRIVATE,
                      prepend_modifier(modifier_kind.FINAL, modifiers, the_source), the_source);
              implementation_body.add(new variable_construct(instance_variable_modifiers, type,
                  name, null, the_source));

              // Add constructor parameter
              ctor_parameters.add(new variable_construct(modifiers, type, name, null, the_source));
              identifier variable_identifier = new identifier(name, the_source);
              identifier this_identifier = new identifier(THIS_NAME, the_source);
              construct this_access = make_operator(operator_type.DOT, this_identifier,
                  variable_identifier, the_source);
              construct assignment = make_operator(operator_type.ASSIGN, this_access,
                  variable_identifier, the_source);
              ctor_statements.add(assignment);

              // Add field description
              if (!has_dont_describe) {
                describe_fields.add(name);
              }
            }

            // Add accessor function
            List<modifier_construct> accessor_modifiers =
                prepend_modifier(modifier_kind.PUBLIC, modifiers, the_source);
            if (declare_interface || has_override) {
                accessor_modifiers = prepend_modifier(modifier_kind.OVERRIDE, accessor_modifiers,
                    the_source);
            }

            construct return_expression = has_initializer ? the_variable_construct.initializer() :
                new identifier(name, the_source);
            construct accessor_return = new return_construct(return_expression, the_source);
            List<construct> accessor_statements = new ArrayList<construct>();
            accessor_statements.add(accessor_return);
            construct accessor_body = new block_construct(accessor_statements, the_source);
            procedure_construct accessor = new procedure_construct(accessor_modifiers,
                type, name, new ArrayList<variable_construct>(), accessor_body, the_source);
            accessor_functions.add(accessor);
          }
        } else if (declare_enum && is_enum_declaration.call(the_construct)) {
          implementation_body.add(the_construct);
        } else {
          // TODO: handle other constructs.
          panic("In type declaration: " + the_construct);
        }
      }

      List<construct> result = new ArrayList<construct>();

      if (declare_interface) {
        type_construct interface_type =
            new type_construct(the_type_construct.modifiers(),
                type_kind.INTERFACE,
                interface_name,
                null,
                interface_body,
                the_source);
        result.add(interface_type);
      }

      if (declare_implementation) {
        if (declare_singleton) {
          // TODO: signal an error
          assert ctor_parameters.isEmpty();

          List<modifier_construct> instance_modifiers = new ArrayList<modifier_construct>();
          instance_modifiers.add(new modifier_construct(modifier_kind.PUBLIC, the_source));
          instance_modifiers.add(new modifier_construct(modifier_kind.STATIC, the_source));
          instance_modifiers.add(new modifier_construct(modifier_kind.FINAL, the_source));

          construct instance_ctor = new parameter_construct(
              make_new(implementation_name, the_source),
              new ArrayList<construct>(),
              grouping_type.PARENS,
              the_source);

          variable_construct instance_variable =
              new variable_construct(
                  instance_modifiers,
                  new identifier(implementation_name, the_source),
                  INSTANCE_NAME,
                  instance_ctor,
                  the_source);
          implementation_body.add(instance_variable);
        }

        if (!ctor_parameters.isEmpty()) {
          List<modifier_construct> ctor_modifiers = new ArrayList<modifier_construct>();
          if (!declare_enum) {
            ctor_modifiers.add(new modifier_construct(modifier_kind.PUBLIC, the_source));
          }
          block_construct ctor_body = new block_construct(ctor_statements, the_source);
          procedure_construct ctor_procedure =
              new procedure_construct(
                  ctor_modifiers,
                  null,
                  implementation_name,
                  ctor_parameters,
                  ctor_body,
                  the_source);
          implementation_body.add(ctor_procedure);
        }

        implementation_body.addAll(accessor_functions);

        if (is_describable(the_declaration)) {
          implementation_body.add(generate_description(implementation_name, describe_fields,
              declare_enum, the_source));
        }

        type_construct implementation_type =
            new type_construct(the_type_construct.modifiers(),
                declare_enum ? type_kind.ENUM : type_kind.CLASS,
                implementation_name,
                null,
                implementation_body,
                the_source);
        result.add(implementation_type);
      }

      return result;
    }

    private boolean is_describable(type_declaration the_declaration) {
      return describable_type != null &&
          the_analysis_context.get_all_supertypes(the_declaration.declared_type()).
              contains(describable_type);
    }

    private procedure_construct generate_description(String type_name, List<String> fields,
        boolean declare_enum, final source the_source) {

      construct name_literal = make_literal(type_name, the_source);
      construct return_expression;

      if (fields.isEmpty()) {
        construct argument;
        if (declare_enum) {
          argument = new parameter_construct(
              new identifier("name", the_source),
              new ArrayList<construct>(),
              grouping_type.PARENS,
              the_source);
        } else {
          argument = name_literal;
        }
        List<construct> ctor_parameters = new ArrayList<construct>();
        ctor_parameters.add(argument);
        return_expression = new parameter_construct(
            make_new("text_string", the_source),
            ctor_parameters,
            grouping_type.OPERATOR,
            the_source);
      } else {
        List<construct> join_arguments = new ArrayList<construct>();
        join_arguments.add(name_literal);
        join_arguments.add(new identifier("START_OBJECT", the_source));

        if (fields.size() != 1) {
          List<construct> field_calls = map(fields, new function<construct, String>() {
            @Override
            public construct call(String name) {
              construct name_literal = make_literal(name, the_source);
              construct name_identifier = new identifier(name, the_source);
              return call_function2("field_is", name_literal, name_identifier, the_source);
            }
          });
          construct indent_call = new parameter_construct(new identifier("indent", the_source),
              field_calls, grouping_type.PARENS, the_source);

          join_arguments.add(new identifier("NEWLINE", the_source));
          join_arguments.add(indent_call);
        } else {
          construct name_identifier = new identifier(fields.get(0), the_source);

          join_arguments.add(new identifier("SPACE", the_source));
          join_arguments.add(call_function1("describe", name_identifier, the_source));
          join_arguments.add(new identifier("SPACE", the_source));
        }

        join_arguments.add(new identifier("END_OBJECT", the_source));
        return_expression = new parameter_construct(new identifier("join_fragments",
            the_source), join_arguments, grouping_type.PARENS, the_source);
      }

      construct description_return = new return_construct(return_expression, the_source);
      List<construct> description_statements = new ArrayList<construct>();
      description_statements.add(description_return);
      block_construct description_body = new block_construct(description_statements, the_source);

      return new procedure_construct(
          make_override_public(the_source),
          new identifier("text", the_source),
          "description",
          new ArrayList<variable_construct>(),
          description_body,
          the_source);
    }

    public static List<modifier_construct> make_modifiers(modifier_kind modifier1,
        modifier_kind modifier2, source the_source) {
      List<modifier_construct> modifiers = new ArrayList<modifier_construct>();

      modifiers.add(new modifier_construct(modifier1, the_source));
      modifiers.add(new modifier_construct(modifier2, the_source));

      return modifiers;
    }

    public static List<modifier_construct> make_override_public(source the_source) {
      return make_modifiers(modifier_kind.OVERRIDE, modifier_kind.PUBLIC, the_source);
    }

    public static construct make_literal(String value, source the_source) {
      // TODO: escape properly.
      return new string_literal(value, "\"" + value + "\"", the_source);
    }

    public static construct make_new(String type_name, source the_source) {
      List<construct> new_parameters = new ArrayList<construct>();
      new_parameters.add(new identifier(type_name, the_source));
      return new parameter_construct(
          new operator(operator_type.NEW, the_source),
          new_parameters,
          grouping_type.OPERATOR,
          the_source);
    }

    public static construct call_function1(String name, construct argument0, source the_source) {
      List<construct> arguments = new ArrayList<construct>();
      arguments.add(argument0);
      return new parameter_construct(new identifier(name, the_source),
          arguments, grouping_type.PARENS, the_source);
    }

    public static construct call_function2(String name, construct argument0, construct argument1,
        source the_source) {
      List<construct> arguments = new ArrayList<construct>();
      arguments.add(argument0);
      arguments.add(argument1);
      return new parameter_construct(new identifier(name, the_source),
          arguments, grouping_type.PARENS, the_source);
    }

    @Override
    public construct call_identifier(identifier the_identifier) {
      @Nullable action the_action = get_binding(the_identifier);
      if (the_action instanceof type_action) {
        type result = ((type_action) the_action).the_type();
        if (result instanceof principal_type) {
          @Nullable String mapping = type_mapping.get((principal_type) result);
          if (mapping != null) {
            return new identifier(mapping, the_identifier);
          }
        }
      }
      return the_identifier;
    }

    private boolean is_nullable(construct the_construct) {
      if (the_construct instanceof parameter_construct) {
        construct main = ((parameter_construct) the_construct).main();
        @Nullable action main_action = get_binding(main);
        return main_action instanceof type_action &&
            ((type_action) main_action).the_type() == core_type.NULLABLE;
      }
      return false;
    }

    private construct strip_nullable(construct the_construct) {
      assert is_nullable(the_construct);
      List<construct> parameters = ((parameter_construct) the_construct).parameters();
      // TODO: validate number of parameters in is_nullable()?
      assert parameters.size() == 1;
      return parameters.get(0);
    }

    // TODO: needs to be rewritte using has() and pushed to the library
    private boolean has_modifier(List<modifier_construct> modifiers,
        modifier_kind the_modifier_kind) {
      for (modifier_construct modifier : modifiers) {
        if (modifier.the_modifier_kind() == the_modifier_kind) {
          return true;
        }
      }
      return false;
    }

    @Override
    public construct call_dispatch_construct(dispatch_construct the_dispatch_construct) {
      source the_source = the_dispatch_construct;

      construct dispatch_type_construct = the_dispatch_construct.the_type();

      List<modifier_construct> type_modifiers = new ArrayList<modifier_construct>();
      type_modifiers.add(new modifier_construct(modifier_kind.ABSTRACT, the_source));

      List<construct> type_body = new ArrayList<construct>();

      identifier result_identifier = new identifier(RESULT_NAME, the_source);
      List<construct> function_parameters = new ArrayList<construct>();
      function_parameters.add(result_identifier);
      function_parameters.add(dispatch_type_construct);
      construct function_type = new parameter_construct(
          new identifier(FUNCTION_NAME, the_source), function_parameters,
          grouping_type.ANGLE_BRACKETS, the_source);

      type_body.add(new supertype_construct(supertype_kind.IMPLEMENTS,
          Collections.singletonList(function_type), the_source));

      type_action the_type_action = (type_action) get_binding(dispatch_type_construct);
      assert the_type_action != null;
      type disptach_type = the_type_action.the_type();
      String dispatch_type_name = disptach_type.name();

      String parameter_name = join_identifier(THE_NAME, dispatch_type_name);
      String call_type_name = join_identifier(CALL_NAME, dispatch_type_name);
      construct parameter_identifier = new identifier(parameter_name, the_source);
      List<construct> call_body = new ArrayList<construct>();

      variable_construct call_parameter = new variable_construct(
          Collections.<modifier_construct>emptyList(),
          dispatch_type_construct,
          parameter_name,
          null,
          the_source);
      procedure_construct abstract_call_procedure = new procedure_construct(
          make_modifiers(modifier_kind.PUBLIC, modifier_kind.ABSTRACT, the_source),
          result_identifier,
          call_type_name,
          Collections.singletonList(call_parameter),
          null,
          the_source);
      type_body.add(abstract_call_procedure);

      Set<type> subtypes = the_analysis_context.get_direct_subtypes(disptach_type);
      for (type subtype : subtypes) {
        String subtype_name = subtype.name();
        String call_subtype_name = join_identifier(CALL_NAME, subtype_name);
        identifier subtype_identifier = new identifier(subtype_name, the_source);

        construct subcall_construct = new parameter_construct(
            new identifier(call_subtype_name, the_source),
            Collections.<construct>singletonList(
                make_operator(operator_type.AS, parameter_identifier, subtype_identifier,
                    the_source)),
            grouping_type.PARENS,
            the_source);
        construct subcall_return = new return_construct(subcall_construct, the_source);
        construct if_construct = new conditional_construct(
            make_operator(operator_type.IS, parameter_identifier, subtype_identifier, the_source),
            new block_construct(Collections.singletonList(subcall_return), the_source),
            null,
            the_source);
        call_body.add(if_construct);

        String subtype_name_with_the = join_identifier(THE_NAME, subtype_name);
        construct supercall_construct = new parameter_construct(
            new identifier(call_type_name, the_source),
            Collections.<construct>singletonList(new identifier(subtype_name_with_the, the_source)),
            grouping_type.PARENS,
            the_source);
        construct supercall_return = new return_construct(supercall_construct, the_source);
        variable_construct subtype_call_parameter = new variable_construct(
            Collections.<modifier_construct>emptyList(),
            subtype_identifier,
            subtype_name_with_the,
            null,
            the_source);
        procedure_construct subcall_procedure = new procedure_construct(
            Collections.singletonList(new modifier_construct(modifier_kind.PUBLIC, the_source)),
            result_identifier,
            call_subtype_name,
            Collections.singletonList(subtype_call_parameter),
            new block_construct(Collections.singletonList(supercall_return), the_source),
            the_source);
        type_body.add(subcall_procedure);
      }

      construct subcall_construct = new parameter_construct(
          new identifier(call_type_name, the_source),
          Collections.<construct>singletonList(new identifier(parameter_name, the_source)),
          grouping_type.PARENS,
          the_source);
      construct subcall_return = new return_construct(subcall_construct, the_source);
      call_body.add(subcall_return);

      procedure_construct call_procedure = new procedure_construct(
          make_override_public(the_source),
          result_identifier,
          CALL_NAME,
          Collections.singletonList(call_parameter),
          new block_construct(call_body, the_source),
          the_source);
      type_body.add(0, call_procedure);

      type_construct result = new type_construct(type_modifiers, type_kind.CLASS,
          the_dispatch_construct.name(), Collections.<construct>singletonList(result_identifier),
          type_body, the_source);

      return result;
    }
  }

  private static final boolean DEBUG_TOKENIZER = false;

  private static final boolean DEBUG_PARSER = false;

  public static parser.postprocessor init_postprocessor() {
    parser.common_postprocessor result = new parser.common_postprocessor();
    result.add(modifier_kind.PUBLIC);
    result.add(modifier_kind.OVERRIDE);
    result.add(modifier_kind.DONT_DESCRIBE);
    return result;
  }

  private static void add_core_type(analysis_context the_context, core_type the_type) {
    the_context.add_action(top_type.instance, to_lower_case(the_type.name()),
        new type_action_class(the_type, builtin_source.instance));
  }

  public static analysis_context init_analysis_context() {
    analysis_context the_context = new analysis_context();

    add_core_type(the_context, core_type.INTEGER);
    add_core_type(the_context, core_type.STRING);
    add_core_type(the_context, core_type.LIST);
    add_core_type(the_context, core_type.NULLABLE);

    the_context.add_action(top_type.instance, "null",
        new value_action_class(core_type.NULL, builtin_source.instance));

    return the_context;
  }

  public static void debug_describe(String info, describable the_describable) {
    System.err.println(info + ": " + render_text(describe(the_describable)));
  }

  public static parser.parser_config init_parser() {
    parser.common_parser the_parser = new parser.common_parser();

    the_parser.add_kind("datatype", type_kind.DATATYPE);
    the_parser.add_kind("interface", type_kind.INTERFACE);
    the_parser.add_kind("enum", type_kind.ENUM);
    the_parser.add_kind("class", type_kind.CLASS);
    the_parser.add_kind("singleton", type_kind.SINGLETON);

    the_parser.add_supertype_kind("extends", supertype_kind.EXTENDS);
    the_parser.add_supertype_kind("implements", supertype_kind.IMPLEMENTS);

    return the_parser;
  }

  public static void create(source_text the_source, boolean analyze) {
    List<token> tokens = parser.postprocess(tokenizer.tokenize(the_source), init_postprocessor());
    if (DEBUG_TOKENIZER) {
      System.out.println(render_text(describe(tokens)));
    }

    List<construct> constructs = parser.parse(tokens, init_parser());
    if (DEBUG_PARSER) {
      System.out.println(render_text(describe(constructs)));
    }

    if (feedback.has_errors) {
      return;
    }

    analysis_context the_context = init_analysis_context();
    analyzer the_analyzer = new analyzer(the_context);

    for (analysis_pass pass : analysis_pass.values()) {
      the_analyzer.analyze_all(constructs, top_type.instance, pass);
    }

    if (feedback.has_errors) {
      return;
    }

    constructs = new to_java_transform(the_context).transform_all(constructs);

    System.out.print(render_text(new printer.java_printer().print_all(constructs)));
  }

  public static void main(String[] args) {
    assert args.length > 0 && args.length <= 2;
    boolean analyze = args.length == 2;
    String file_name = args[analyze ? 1 : 0];
    String file_content = "";

    try {
      file_content = read_file(file_name);
    } catch (IOException e) {
      System.err.println("Can't read " + file_name);
      System.exit(1);
    }

    create(new source_text_class(file_name, file_content), analyze);
  }

  private static String read_file(String file_name) throws IOException {
    byte[] encoded = Files.readAllBytes(Paths.get(file_name));
    return new String(encoded, StandardCharsets.UTF_8);
  }
}
