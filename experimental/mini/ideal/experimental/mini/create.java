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
      type_mapping.put(core_type.SET, "Set");

      action describable_action = the_analysis_context.get_action(top_type.instance,
          names.DESCRIBABLE_NAME);
      if (describable_action instanceof type_action) {
        describable_type = ((type_action) describable_action).the_type();
      } else {
        describable_type = null;
      }
    }

    protected @Nullable analysis_result get_binding(construct the_construct) {
      return the_analysis_context.get_binding(the_construct);
    }

    @Override
    public construct call_parameter_construct(parameter_construct the_parameter_construct) {
      origin the_origin = the_parameter_construct;

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
      @Nullable analysis_result main_action = get_binding(main);
      if (main_action instanceof type_action) {
        type the_type = ((type_action) main_action).the_type();
        if (the_type == core_type.LIST || the_type == core_type.SET) {
          grouping = grouping_type.ANGLE_BRACKETS;
        }
      }

      construct result = new parameter_construct(transformed_main, parameters, grouping,
          the_origin);

      @Nullable analysis_result the_result = get_binding(the_parameter_construct);
      if (the_result instanceof variable_action) {
        result = new parameter_construct(result, new ArrayList<construct>(), grouping_type.PARENS,
            the_origin);
      }

      return result;
    }

    @Override
    public variable_construct call_variable_construct(variable_construct the_variable_construct) {
      assert the_variable_construct.type() != null;
      origin the_origin = the_variable_construct;
      List<modifier_construct> modifiers = the_variable_construct.modifiers();
      construct type = transform(the_variable_construct.type());
      if (is_nullable(type)) {
        type = strip_nullable(type);
        modifiers = prepend_modifier(modifier_kind.NULLABLE, modifiers, the_origin);
      }
      // TODO: add variable_construct.has_initializer
      @Nullable construct initializer = the_variable_construct.initializer();
      if (initializer != null) {
        initializer = transform(initializer);
      }
      return new variable_construct(modifiers, type, the_variable_construct.name(), initializer,
          the_origin);
    }

    @Override
    public procedure_construct call_procedure_construct(
        procedure_construct the_procedure_construct) {
      origin the_origin = the_procedure_construct;
      List<modifier_construct> modifiers = the_procedure_construct.modifiers();

      assert the_procedure_construct.return_type() != null;
      construct return_type = transform(the_procedure_construct.return_type());
      if (is_nullable(return_type)) {
        return_type = strip_nullable(return_type);
        modifiers = prepend_modifier(modifier_kind.NULLABLE, modifiers, the_origin);
      }

      List<variable_construct> parameters = new ArrayList<variable_construct>();
      for (variable_construct parameter : the_procedure_construct.parameters()) {
        parameters.add(call_variable_construct(parameter));
      }

      @Nullable construct body = the_procedure_construct.body();
      if (body != null) {
        body = transform(body);
      }

      return new procedure_construct(modifiers, return_type, the_procedure_construct.name(),
          parameters, body, the_origin);
    }

    private static List<modifier_construct> prepend_modifier(modifier_kind the_modifier_kind,
        List<modifier_construct> modifiers, origin the_origin) {
      List<modifier_construct> result = new ArrayList<modifier_construct>();
      result.add(new modifier_construct(the_modifier_kind, the_origin));
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

    private static parameter_construct make_operator(operator_type the_operator_type,
        construct first_argument, construct second_argument, origin the_origin) {
      List<construct> arguments = new ArrayList<construct>();
      arguments.add(first_argument);
      arguments.add(second_argument);
      return new parameter_construct(new operator(the_operator_type, the_origin), arguments,
          grouping_type.OPERATOR, the_origin);
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

      origin the_origin = the_type_construct;

      String interface_name = the_type_construct.name();
      String implementation_name = declare_interface ?
          names.join_identifier(interface_name, "class") : interface_name;

      List<construct> interface_body = new ArrayList<construct>();
      List<construct> implementation_body = new ArrayList<construct>();

      if (declare_interface && declare_implementation) {
        List<construct> super_interface = new ArrayList<construct>();
        super_interface.add(new identifier(interface_name, the_origin));
        implementation_body.add(new supertype_construct(supertype_kind.IMPLEMENTS, super_interface,
            the_origin));
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
                type, name, new ArrayList<variable_construct>(), null, the_origin));
          }

          if (declare_implementation) {
            boolean has_initializer = the_variable_construct.initializer() != null;

            if (!has_initializer) {
              // Add instance variable
              List<modifier_construct> instance_variable_modifiers =
                  prepend_modifier(modifier_kind.PRIVATE,
                      prepend_modifier(modifier_kind.FINAL, modifiers, the_origin), the_origin);
              implementation_body.add(new variable_construct(instance_variable_modifiers, type,
                  name, null, the_origin));

              // Add constructor parameter
              ctor_parameters.add(new variable_construct(modifiers, type, name, null, the_origin));
              identifier variable_identifier = new identifier(name, the_origin);
              identifier this_identifier = new identifier(names.THIS_NAME, the_origin);
              construct this_access = make_operator(operator_type.DOT, this_identifier,
                  variable_identifier, the_origin);
              construct assignment = make_operator(operator_type.ASSIGN, this_access,
                  variable_identifier, the_origin);
              ctor_statements.add(assignment);

              // Add field description
              if (!has_dont_describe) {
                describe_fields.add(name);
              }
            }

            // Add accessor function
            List<modifier_construct> accessor_modifiers =
                prepend_modifier(modifier_kind.PUBLIC, modifiers, the_origin);
            if (declare_interface || has_override) {
                accessor_modifiers = prepend_modifier(modifier_kind.OVERRIDE, accessor_modifiers,
                    the_origin);
            }

            construct return_expression = has_initializer ? the_variable_construct.initializer() :
                new identifier(name, the_origin);
            construct accessor_return = new return_construct(return_expression, the_origin);
            List<construct> accessor_statements = new ArrayList<construct>();
            accessor_statements.add(accessor_return);
            construct accessor_body = new block_construct(accessor_statements, the_origin);
            procedure_construct accessor = new procedure_construct(accessor_modifiers,
                type, name, new ArrayList<variable_construct>(), accessor_body, the_origin);
            accessor_functions.add(accessor);
          }
        } else if (declare_enum && analysis.is_enum_declaration.call(the_construct)) {
          implementation_body.add(the_construct);
        } else if (the_construct instanceof procedure_construct) {
          procedure_construct the_procedure_construct =
              call_procedure_construct((procedure_construct) the_construct);
          interface_body.add(the_procedure_construct);
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
                the_origin);
        result.add(interface_type);
      }

      if (declare_implementation) {
        if (declare_singleton) {
          // TODO: signal an error
          assert ctor_parameters.isEmpty();

          List<modifier_construct> instance_modifiers = new ArrayList<modifier_construct>();
          instance_modifiers.add(new modifier_construct(modifier_kind.PUBLIC, the_origin));
          instance_modifiers.add(new modifier_construct(modifier_kind.STATIC, the_origin));
          instance_modifiers.add(new modifier_construct(modifier_kind.FINAL, the_origin));

          construct instance_ctor = new parameter_construct(
              make_new(implementation_name, the_origin),
              new ArrayList<construct>(),
              grouping_type.PARENS,
              the_origin);

          variable_construct instance_variable =
              new variable_construct(
                  instance_modifiers,
                  new identifier(implementation_name, the_origin),
                  names.INSTANCE_NAME,
                  instance_ctor,
                  the_origin);
          implementation_body.add(instance_variable);
        }

        if (!ctor_parameters.isEmpty()) {
          List<modifier_construct> ctor_modifiers = new ArrayList<modifier_construct>();
          if (!declare_enum) {
            ctor_modifiers.add(new modifier_construct(modifier_kind.PUBLIC, the_origin));
          }
          block_construct ctor_body = new block_construct(ctor_statements, the_origin);
          procedure_construct ctor_procedure =
              new procedure_construct(
                  ctor_modifiers,
                  null,
                  implementation_name,
                  ctor_parameters,
                  ctor_body,
                  the_origin);
          implementation_body.add(ctor_procedure);
        }

        implementation_body.addAll(accessor_functions);

        if (is_describable(the_declaration)) {
          implementation_body.add(generate_description(implementation_name, describe_fields,
              declare_enum, the_origin));
        }

        type_construct implementation_type =
            new type_construct(the_type_construct.modifiers(),
                declare_enum ? type_kind.ENUM : type_kind.CLASS,
                implementation_name,
                null,
                implementation_body,
                the_origin);
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
        boolean declare_enum, final origin the_origin) {

      construct name_literal = make_literal(type_name, the_origin);
      construct return_expression;

      if (fields.isEmpty()) {
        construct argument;
        if (declare_enum) {
          argument = new parameter_construct(
              new identifier("name", the_origin),
              new ArrayList<construct>(),
              grouping_type.PARENS,
              the_origin);
        } else {
          argument = name_literal;
        }
        List<construct> ctor_parameters = new ArrayList<construct>();
        ctor_parameters.add(argument);
        return_expression = new parameter_construct(
            make_new("text_string", the_origin),
            ctor_parameters,
            grouping_type.OPERATOR,
            the_origin);
      } else {
        List<construct> join_arguments = new ArrayList<construct>();
        join_arguments.add(name_literal);
        join_arguments.add(new identifier("START_OBJECT", the_origin));

        if (fields.size() != 1) {
          List<construct> field_calls = map(fields, new function<construct, String>() {
            @Override
            public construct call(String name) {
              construct name_literal = make_literal(name, the_origin);
              construct name_identifier = new identifier(name, the_origin);
              return call_function2("field_is", name_literal, name_identifier, the_origin);
            }
          });
          construct indent_call = new parameter_construct(new identifier("indent", the_origin),
              field_calls, grouping_type.PARENS, the_origin);

          join_arguments.add(new identifier("NEWLINE", the_origin));
          join_arguments.add(indent_call);
        } else {
          construct name_identifier = new identifier(fields.get(0), the_origin);

          join_arguments.add(new identifier("SPACE", the_origin));
          join_arguments.add(call_function1("describe", name_identifier, the_origin));
          join_arguments.add(new identifier("SPACE", the_origin));
        }

        join_arguments.add(new identifier("END_OBJECT", the_origin));
        return_expression = new parameter_construct(new identifier("join_fragments",
            the_origin), join_arguments, grouping_type.PARENS, the_origin);
      }

      construct description_return = new return_construct(return_expression, the_origin);
      List<construct> description_statements = new ArrayList<construct>();
      description_statements.add(description_return);
      block_construct description_body = new block_construct(description_statements, the_origin);

      return new procedure_construct(
          make_override_public(the_origin),
          new identifier("text", the_origin),
          "description",
          new ArrayList<variable_construct>(),
          description_body,
          the_origin);
    }

    public static List<modifier_construct> make_modifiers(modifier_kind modifier1,
        modifier_kind modifier2, origin the_origin) {
      List<modifier_construct> modifiers = new ArrayList<modifier_construct>();

      modifiers.add(new modifier_construct(modifier1, the_origin));
      modifiers.add(new modifier_construct(modifier2, the_origin));

      return modifiers;
    }

    public static List<modifier_construct> make_override_public(origin the_origin) {
      return make_modifiers(modifier_kind.OVERRIDE, modifier_kind.PUBLIC, the_origin);
    }

    public static construct make_literal(String value, origin the_origin) {
      // TODO: escape properly.
      return new string_literal(value, "\"" + value + "\"", the_origin);
    }

    public static construct make_new(String type_name, origin the_origin) {
      List<construct> new_parameters = new ArrayList<construct>();
      new_parameters.add(new identifier(type_name, the_origin));
      return new parameter_construct(
          new operator(operator_type.NEW, the_origin),
          new_parameters,
          grouping_type.OPERATOR,
          the_origin);
    }

    public static construct call_function1(String name, construct argument0, origin the_origin) {
      List<construct> arguments = new ArrayList<construct>();
      arguments.add(argument0);
      return new parameter_construct(new identifier(name, the_origin),
          arguments, grouping_type.PARENS, the_origin);
    }

    public static construct call_function2(String name, construct argument0, construct argument1,
        origin the_origin) {
      List<construct> arguments = new ArrayList<construct>();
      arguments.add(argument0);
      arguments.add(argument1);
      return new parameter_construct(new identifier(name, the_origin),
          arguments, grouping_type.PARENS, the_origin);
    }

    @Override
    public construct call_identifier(identifier the_identifier) {
      @Nullable analysis_result the_analysis_result = get_binding(the_identifier);
      if (the_analysis_result instanceof type_action) {
        type result = ((type_action) the_analysis_result).the_type();
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
        @Nullable analysis_result main_action = get_binding(main);
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
      origin the_origin = the_dispatch_construct;

      construct dispatch_type_construct = the_dispatch_construct.the_type();

      List<modifier_construct> type_modifiers = new ArrayList<modifier_construct>();
      type_modifiers.add(new modifier_construct(modifier_kind.ABSTRACT, the_origin));

      List<construct> type_body = new ArrayList<construct>();

      identifier result_identifier = new identifier(names.RESULT_NAME, the_origin);
      List<construct> function_parameters = new ArrayList<construct>();
      function_parameters.add(result_identifier);
      function_parameters.add(dispatch_type_construct);
      construct function_type = new parameter_construct(
          new identifier(names.FUNCTION_NAME, the_origin), function_parameters,
          grouping_type.ANGLE_BRACKETS, the_origin);

      type_body.add(new supertype_construct(supertype_kind.IMPLEMENTS,
          Collections.singletonList(function_type), the_origin));

      type_action the_type_action = (type_action) get_binding(dispatch_type_construct);
      assert the_type_action != null;
      type disptach_type = the_type_action.the_type();
      String dispatch_type_name = disptach_type.name();

      String parameter_name = names.join_identifier(names.THE_NAME, dispatch_type_name);
      String call_type_name = names.join_identifier(names.CALL_NAME, dispatch_type_name);
      construct parameter_identifier = new identifier(parameter_name, the_origin);
      List<construct> call_body = new ArrayList<construct>();

      variable_construct call_parameter = new variable_construct(
          Collections.<modifier_construct>emptyList(),
          dispatch_type_construct,
          parameter_name,
          null,
          the_origin);
      procedure_construct abstract_call_procedure = new procedure_construct(
          make_modifiers(modifier_kind.PUBLIC, modifier_kind.ABSTRACT, the_origin),
          result_identifier,
          call_type_name,
          Collections.singletonList(call_parameter),
          null,
          the_origin);
      type_body.add(abstract_call_procedure);

      Set<type> subtypes = the_analysis_context.get_direct_subtypes(disptach_type);
      for (type subtype : subtypes) {
        String subtype_name = subtype.name();
        String call_subtype_name = names.join_identifier(names.CALL_NAME, subtype_name);
        identifier subtype_identifier = new identifier(subtype_name, the_origin);

        construct subcall_construct = new parameter_construct(
            new identifier(call_subtype_name, the_origin),
            Collections.<construct>singletonList(
                make_operator(operator_type.AS, parameter_identifier, subtype_identifier,
                    the_origin)),
            grouping_type.PARENS,
            the_origin);
        construct subcall_return = new return_construct(subcall_construct, the_origin);
        construct if_construct = new conditional_construct(
            make_operator(operator_type.IS, parameter_identifier, subtype_identifier, the_origin),
            new block_construct(Collections.singletonList(subcall_return), the_origin),
            null,
            the_origin);
        call_body.add(if_construct);

        String subtype_name_with_the = names.join_identifier(names.THE_NAME, subtype_name);
        construct supercall_construct = new parameter_construct(
            new identifier(call_type_name, the_origin),
            Collections.<construct>singletonList(new identifier(subtype_name_with_the, the_origin)),
            grouping_type.PARENS,
            the_origin);
        construct supercall_return = new return_construct(supercall_construct, the_origin);
        variable_construct subtype_call_parameter = new variable_construct(
            Collections.<modifier_construct>emptyList(),
            subtype_identifier,
            subtype_name_with_the,
            null,
            the_origin);
        procedure_construct subcall_procedure = new procedure_construct(
            Collections.singletonList(new modifier_construct(modifier_kind.PUBLIC, the_origin)),
            result_identifier,
            call_subtype_name,
            Collections.singletonList(subtype_call_parameter),
            new block_construct(Collections.singletonList(supercall_return), the_origin),
            the_origin);
        type_body.add(subcall_procedure);
      }

      construct subcall_construct = new parameter_construct(
          new identifier(call_type_name, the_origin),
          Collections.<construct>singletonList(new identifier(parameter_name, the_origin)),
          grouping_type.PARENS,
          the_origin);
      construct subcall_return = new return_construct(subcall_construct, the_origin);
      call_body.add(subcall_return);

      procedure_construct call_procedure = new procedure_construct(
          make_override_public(the_origin),
          result_identifier,
          names.CALL_NAME,
          Collections.singletonList(call_parameter),
          new block_construct(call_body, the_origin),
          the_origin);
      type_body.add(0, call_procedure);

      type_construct result = new type_construct(type_modifiers, type_kind.CLASS,
          the_dispatch_construct.name(), Collections.<construct>singletonList(result_identifier),
          type_body, the_origin);

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
        new type_action_class(the_type, builtin_origin.instance));
  }

  public static analysis_context init_analysis_context() {
    analysis_context the_context = new analysis.base_analysis_context();

    add_core_type(the_context, core_type.VOID);
    add_core_type(the_context, core_type.INTEGER);
    add_core_type(the_context, core_type.STRING);
    add_core_type(the_context, core_type.LIST);
    add_core_type(the_context, core_type.SET);
    add_core_type(the_context, core_type.NULLABLE);

    the_context.add_action(top_type.instance, "null",
        new value_action_class(core_type.NULL, builtin_origin.instance));

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
    analysis.analyzer the_analyzer = new analysis.analyzer(the_context);

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
