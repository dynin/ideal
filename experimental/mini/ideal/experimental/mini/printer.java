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
import java.util.List;

public class printer {

  public static final text COMMA = new text_string(",");
  public static final text LESS_THAN = new text_string("<");
  public static final text GREATER_THAN = new text_string(">");
  public static final text OPEN_BRACE = new text_string("{");
  public static final text CLOSE_BRACE = new text_string("}");
  public static final text EQUALS = new text_string("=");
  public static final text SEMICOLON = new text_string(";");
  public static final text IF_KEYWORD = new text_string("if");
  public static final text RETURN_KEYWORD = new text_string("return");

  public static class base_printer extends construct_dispatch<text> {

    function<text, variable_construct> param_printer = new function<text, variable_construct>() {
      @Override
      public text call(variable_construct the_variable_construct) {
        return print_variable(the_variable_construct);
      }
    };

    function<text, construct> statement_printer = new function<text, construct>() {
      @Override
      public text call(construct the_construct) {
        text printed_construct = print(the_construct);
        // TODO: generalize
        if (the_construct instanceof conditional_construct) {
          return printed_construct;
        } else {
          return join_text(printed_construct, SEMICOLON, NEWLINE);
        }
      }
    };

    public text to_text(punctuation the_punctuation) {
      return new text_string(the_punctuation.symbol());
    }

    public text print(construct the_construct) {
      return call(the_construct);
    }

    public text print_all(List<? extends construct> constructs) {
      return new text_list(map(constructs, this));
    }

    @Override
    public text call_construct(construct the_construct) {
      panic("Printing unknown construct: " + the_construct);
      return null;
    }

    @Override
    public text call_identifier(identifier the_identifier) {
      return new text_string(the_identifier.name());
    }

    @Override
    public text call_string_literal(string_literal the_string_literal) {
      return new text_string(the_string_literal.with_quotes());
    }

    @Override
    public text call_parameter_construct(parameter_construct the_parameter_construct) {
      if (the_parameter_construct.main() instanceof operator) {
        return print_operator((operator) the_parameter_construct.main(),
            the_parameter_construct.parameters());
      }

      text main = print(the_parameter_construct.main());
      text parameters = fold_with_comma(the_parameter_construct.parameters(), this);

      if (the_parameter_construct.grouping() == grouping_type.ANGLE_BRACKETS) {
        return join_text(main, LESS_THAN, parameters, GREATER_THAN);
      } else {
        return join_text(main, to_text(punctuation.OPEN_PARENTHESIS), parameters,
            to_text(punctuation.CLOSE_PARENTHESIS));
      }
    }

    public text print_operator(operator the_operator, List<construct> arguments) {
      // TODO: add operator kind (prefix/postfix/prefix)
      if (arguments.size() == 2) {
        return print_infix(the_operator, arguments);
      } else {
        return print_prefix(the_operator, arguments);
      }
    }

    protected text print_operator_type(operator_type the_operator_type) {
      return new text_string(the_operator_type.symbol());
    }

    public text print_infix(operator the_operator, List<construct> arguments) {
      assert arguments.size() == 2;
      operator_type the_operator_type = the_operator.the_operator_type();
      text operator_text = print_operator_type(the_operator_type);
      if (the_operator_type != operator_type.DOT) {
        operator_text = join_text(SPACE, operator_text, SPACE);
      }
      return join_text(print(arguments.get(0)), operator_text, print(arguments.get(1)));
    }

    public text print_prefix(operator the_operator, List<construct> arguments) {
      assert arguments.size() == 1;
      operator_type the_operator_type = the_operator.the_operator_type();
      text operator_text = print_operator_type(the_operator_type);
      // TODO: no need for space for !NEW
      return join_text(operator_text, SPACE, print(arguments.get(0)));
    }

    @Override
    public text call_modifier_construct(modifier_construct the_modifier_construct) {
      return new text_string(to_lower_case(
          the_modifier_construct.the_modifier_kind().toString()));
    }

    @Override
    public text call_s_expression(s_expression the_s_expression) {
      return join_text(to_text(punctuation.OPEN_PARENTHESIS),
        join_text(map(the_s_expression.parameters(), this), SPACE),
        to_text(punctuation.CLOSE_PARENTHESIS));
    }

    @Override
    public text call_block_construct(block_construct the_block_construct) {
      return join_text(OPEN_BRACE, NEWLINE,
          indent(new text_list(map(the_block_construct.statements(), statement_printer))),
          CLOSE_BRACE);
    }

    @Override
    public text call_conditional_construct(conditional_construct the_conditional_construct) {
      // TODO: handle else.
      assert the_conditional_construct.else_branch() == null;
      return join_text(IF_KEYWORD, SPACE, to_text(punctuation.OPEN_PARENTHESIS),
          print(the_conditional_construct.conditional()), to_text(punctuation.CLOSE_PARENTHESIS),
          SPACE, print(the_conditional_construct.then_branch()), NEWLINE);
    }

    @Override
    public text call_return_construct(return_construct the_return_construct) {
      if (the_return_construct.expression() == null) {
        return RETURN_KEYWORD;
      } else {
        return join_text(RETURN_KEYWORD, SPACE, print(the_return_construct.expression()));
      }
    }

    protected text print_variable(variable_construct the_variable_construct) {
      text declaration = join_text(
        print_with_space(the_variable_construct.modifiers()),
        print(the_variable_construct.type()),
        SPACE,
        new text_string(the_variable_construct.name()));

      if (the_variable_construct.initializer() != null) {
        return join_text(declaration, SPACE, EQUALS, SPACE,
            print(the_variable_construct.initializer()));
      } else {
        return declaration;
      }
    }

    @Override
    public text call_variable_construct(variable_construct the_variable_construct) {
      List<text> result = new ArrayList<text>();
      result.add(print_variable(the_variable_construct));
      result.add(SEMICOLON);
      result.add(NEWLINE);
      return new text_list(result);
    }

    @Override
    public text call_procedure_construct(procedure_construct the_procedure_construct) {
      List<text> result = new ArrayList<text>();
      result.add(print_with_space(the_procedure_construct.modifiers()));
      if (the_procedure_construct.return_type() != null) {
        result.add(print(the_procedure_construct.return_type()));
        result.add(SPACE);
      }
      result.add(new text_string(the_procedure_construct.name()));
      result.add(to_text(punctuation.OPEN_PARENTHESIS));

      result.add(fold_with_comma(the_procedure_construct.parameters(), param_printer));
      result.add(to_text(punctuation.CLOSE_PARENTHESIS));
      if (the_procedure_construct.body() == null) {
        result.add(SEMICOLON);
      } else {
        // TODO: relax this constraint.
        assert the_procedure_construct.body() instanceof block_construct;
        result.add(SPACE);
        result.add(print(the_procedure_construct.body()));
      }
      result.add(NEWLINE);
      return new text_list(result);
    }

    @Override
    public text call_dispatch_construct(dispatch_construct the_dispatch_construct) {
      return join_text(to_text(punctuation.OPEN_PARENTHESIS),
        new text_string("dispatch"), SPACE,
        new text_string(the_dispatch_construct.name()), SPACE,
        print(the_dispatch_construct.the_type()),
        to_text(punctuation.CLOSE_PARENTHESIS), NEWLINE);
    }

    @Override
    public text call_supertype_construct(supertype_construct the_supertype_construct) {
      return join_text(
        new text_string(to_lower_case(
            the_supertype_construct.the_supertype_kind().toString())),
        SPACE,
        fold_with_comma(the_supertype_construct.supertypes(), this),
        SEMICOLON,
        NEWLINE);
    }

    @Override
    public text call_type_construct(type_construct the_type_construct) {
      // TODO: print parameters
      return join_text(
        print_with_space(the_type_construct.modifiers()),
        new text_string(to_lower_case(the_type_construct.the_type_kind().toString())),
        SPACE,
        new text_string(the_type_construct.name()),
        SPACE,
        OPEN_BRACE,
        NEWLINE,
        indent(print_all(the_type_construct.body())),
        CLOSE_BRACE,
        NEWLINE);
    }

    // TODO(dynin): use map().
    protected text print_with_space(List<? extends construct> constructs) {
      List<text> result = new ArrayList<text>();
      for (construct the_construct : constructs) {
        result.add(print(the_construct));
        result.add(SPACE);
      }
      return new text_list(result);
    }

    // TODO(dynin): make generic.
    protected <C> text fold_with_comma(List<C> constructs, function<text, C> printer) {
      if (constructs.size() == 0) {
        return EMPTY_TEXT;
      } else if (constructs.size() == 1) {
        return printer.call(constructs.get(0));
      } else {
        List<text> result = new ArrayList<text>();
        for (int i = 0; i < constructs.size(); ++i) {
          if (i > 0) {
            result.add(COMMA);
            result.add(SPACE);
          }
          result.add(printer.call(constructs.get(i)));
        }
        return new text_list(result);
      }
    }
  }

  public static predicate<construct> is_supertype = new predicate<construct>() {
    @Override
    public boolean call(construct the_construct) {
      return the_construct instanceof supertype_construct;
    }
  };

  public static predicate<construct> is_not_supertype = negate_predicate(is_supertype);

  public static predicate<construct> is_not_enum_declaration =
      negate_predicate(analysis.is_enum_declaration);

  public static class java_printer extends base_printer {

    private static final text INSTANCEOF_KEYWORD = new text_string("instanceof");

    @Override
    protected text print_operator_type(operator_type the_operator_type) {
      if (the_operator_type != operator_type.IS) {
        return super.print_operator_type(the_operator_type);
      } else {
        return INSTANCEOF_KEYWORD;
      }
    }

    public text print_infix(operator the_operator, List<construct> arguments) {
      assert arguments.size() == 2;
      if (the_operator.the_operator_type() != operator_type.AS) {
        return super.print_infix(the_operator, arguments);
      } else {
        return join_text(to_text(punctuation.OPEN_PARENTHESIS), print(arguments.get(1)),
            to_text(punctuation.CLOSE_PARENTHESIS), SPACE, print(arguments.get(0)));
      }
    }

    @Override
    public text call_modifier_construct(modifier_construct the_modifier_construct) {
      if (is_java_annotation(the_modifier_construct.the_modifier_kind())) {
        String name = the_modifier_construct.the_modifier_kind().name();
        String annotation_name = "@" + name.charAt(0) + to_lower_case(name.substring(1));
        return new text_string(annotation_name);
      } else {
        return super.call_modifier_construct(the_modifier_construct);
      }
    }

    @Override
    public text call_supertype_construct(supertype_construct the_supertype_construct) {
      // No trailing semicolon or newline.
      return join_text(
        new text_string(to_lower_case(the_supertype_construct.the_supertype_kind().toString())),
        SPACE,
        fold_with_comma(the_supertype_construct.supertypes(), this));
    }

    protected text print_type_parameters(List<construct> parameters) {
      text parameter_text = fold_with_comma(parameters, this);
      return join_text(LESS_THAN, parameter_text, GREATER_THAN);
    }

    @Override
    public text call_type_construct(type_construct the_type_construct) {
      List<text> result = new ArrayList<text>();
      result.add(print_with_space(the_type_construct.modifiers()));
      result.add(new text_string(to_lower_case(the_type_construct.the_type_kind().toString())));
      result.add(SPACE);
      result.add(new text_string(the_type_construct.name()));

      if (the_type_construct.parameters() != null) {
        result.add(print_type_parameters(the_type_construct.parameters()));
      }

      result.add(SPACE);
      List<construct> supertypes = filter(the_type_construct.body(), is_supertype);
      result.add(print_with_space(supertypes));
      result.add(OPEN_BRACE);
      result.add(NEWLINE);
      List<construct> filtered_body = filter(the_type_construct.body(), is_not_supertype);

      List<text> body = new ArrayList<text>();
      if (the_type_construct.the_type_kind() == type_kind.ENUM) {
        List<construct> enum_declarations = filter(filtered_body, analysis.is_enum_declaration);
        assert !enum_declarations.isEmpty();
        for (int i = 0; i < enum_declarations.size(); ++i) {
          body.add(print(enum_declarations.get(i)));
          body.add(i < enum_declarations.size() - 1 ? COMMA : SEMICOLON);
          body.add(NEWLINE);
        }
        filtered_body = filter(filtered_body, is_not_enum_declaration);
      }
      body.add(print_all(filtered_body));

      result.add(indent(new text_list(body)));
      result.add(CLOSE_BRACE);
      result.add(NEWLINE);
      return new text_list(result);
    }

    public boolean is_java_annotation(modifier_kind the_modifier_kind) {
      return the_modifier_kind == modifier_kind.OVERRIDE ||
             the_modifier_kind == modifier_kind.NULLABLE;
    }
  }
}
