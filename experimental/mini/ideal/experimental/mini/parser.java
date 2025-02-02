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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

class parser {

  public interface identifier_processor {
    token process(identifier the_identifier);
  }

  public interface postprocessor {
    @Nullable identifier_processor get_processor(String name);
  }

  public static class modifier_processor implements identifier_processor {
    private final modifier_kind the_modifier_kind;

    public modifier_processor(modifier_kind the_modifier_kind) {
      this.the_modifier_kind = the_modifier_kind;
    }

    @Override
    public token process(identifier the_identifier) {
      return new modifier_construct(the_modifier_kind, the_identifier);
    }
  }

  public static class common_postprocessor implements postprocessor {
    private Map<String, identifier_processor> processors;

    public common_postprocessor() {
      processors = new HashMap<String, identifier_processor>();
    }

    public void add(modifier_kind the_modifier_kind) {
      processors.put(to_lower_case(the_modifier_kind.name()),
          new modifier_processor(the_modifier_kind));
    }

    public @Nullable identifier_processor get_processor(String name) {
      return processors.get(name);
    }
  }

  public static List<token> postprocess(List<token> tokens, postprocessor the_postprocessor) {
    List<token> result = new ArrayList<token>();

    for (token the_token : tokens) {
      if (the_token.the_token_type() == core_token_type.WHITESPACE ||
          the_token.the_token_type() == core_token_type.COMMENT) {
        continue;
      }
      if (the_token instanceof identifier) {
        identifier the_identifier = (identifier) the_token;
        @Nullable identifier_processor the_identifier_processor =
            the_postprocessor.get_processor(the_identifier.name());
        if (the_identifier_processor != null) {
          result.add(the_identifier_processor.process(the_identifier));
          continue;
        }
      }
      result.add(the_token);
    }

    return result;
  }

  public static int parse_sublist(List<token> tokens, int start, List<construct> result,
      parser_context context) {
    int index = start;
    while (index < tokens.size()) {
      token the_token = tokens.get(index);
      index += 1;
      if (the_token instanceof construct) {
        result.add((construct) the_token);
      } else if (the_token.the_token_type() == punctuation.OPEN_PARENTHESIS) {
        List<construct> parameters = new ArrayList<construct>();
        int end = parse_sublist(tokens, index, parameters, context);
        if (end >= tokens.size()) {
          feedback.report(new error_signal(notification_type.CLOSE_PAREN_NOT_FOUND, the_token));
        } else if (tokens.get(end).the_token_type() != punctuation.CLOSE_PARENTHESIS) {
          feedback.report(new error_signal(notification_type.CLOSE_PAREN_NOT_FOUND,
              tokens.get(end)));
        } else {
          end += 1;
        }
        index = end;
        if (!parameters.isEmpty()) {
          construct first = parameters.get(0);
          List<construct> rest = parameters.subList(1, parameters.size());
          if (first instanceof identifier) {
            String name = ((identifier) first).name();
            @Nullable special_parser the_parser = context.get_parser(name);
            if (the_parser != null) {
              @Nullable construct parsed = the_parser.parse(rest);
              if (parsed != null) {
                result.add(parsed);
              } else {
                feedback.report(new error_signal(notification_type.PARSE_ERROR, first));
              }
            } else {
              result.add(new parameter_construct(first, rest, grouping_type.PARENS, the_token));
            }
            continue;
          } else if (first instanceof operator) {
            result.add(new parameter_construct(first, rest, grouping_type.OPERATOR, the_token));
            continue;
          } else if (first instanceof parameter_construct) {
            result.add(new parameter_construct(first, rest, grouping_type.PARENS, the_token));
            continue;
          }
        }
        result.add(new s_expression(parameters, the_token));
      } else if (the_token.the_token_type() == punctuation.CLOSE_PARENTHESIS) {
        return index - 1;
      } else {
        feedback.report(new error_signal(notification_type.PARSE_ERROR, the_token));
      }
    }

    return index;
  }

  public interface special_parser {
    @Nullable construct parse(List<construct> parameters);
  }

  public interface parser_context {
    @Nullable special_parser get_parser(String name);
  }

  public static final special_parser VARIABLE_PARSER = new special_parser() {
    @Override
    public @Nullable construct parse(List<construct> parameters) {
      if (parameters.size() < 2 || parameters.size() > 4) {
        return null;
      }


      List<modifier_construct> modifiers;
      int type_index;
      if (parameters.size() == 2) {
        modifiers = new ArrayList<modifier_construct>();
        type_index = 0;
      } else {
        modifiers = parse_modifiers(parameters.get(0));
        type_index = 1;
      }

      construct type = parameters.get(type_index);

      construct name_construct = parameters.get(type_index + 1);
      if (!(name_construct instanceof identifier)) {
        return null;
      }
      String name = ((identifier) name_construct).name();

      @Nullable construct initializer = (parameters.size() == type_index + 3) ?
          parameters.get(type_index + 2) : null;

      return new variable_construct(modifiers, type, name, initializer, name_construct);
    }
  };

  public static final special_parser DISPATCH_PARSER = new special_parser() {
    @Override
    public @Nullable construct parse(List<construct> parameters) {
      if (parameters.size() != 2) {
        return null;
      }


      construct name_construct = parameters.get(0);
      if (!(name_construct instanceof identifier)) {
        return null;
      }
      String name = ((identifier) name_construct).name();

      construct type = parameters.get(1);

      return new dispatch_construct(name, type, name_construct);
    }
  };

  public static class type_parser implements special_parser {
    public final type_kind the_type_kind;

    public type_parser(type_kind the_type_kind) {
      this.the_type_kind = the_type_kind;
    }

    @Override
    public @Nullable construct parse(List<construct> parameters) {
      if (parameters.size() < 1) {
        return null;
      }
      if (!(parameters.get(0) instanceof identifier)) {
        return null;
      }

      String name = ((identifier) parameters.get(0)).name();
      // TODO: handle parameters
      List<construct> body = parameters.subList(1, parameters.size());
      source the_source = parameters.get(0);

      return new type_construct(new ArrayList<modifier_construct>(), the_type_kind, name, null,
          body, the_source);
    }
  };

  public static class supertype_parser implements special_parser {
    public final supertype_kind the_supertype_kind;

    public supertype_parser(supertype_kind the_supertype_kind) {
      this.the_supertype_kind = the_supertype_kind;
    }

    @Override
    public @Nullable construct parse(List<construct> parameters) {
      if (parameters.isEmpty()) {
        return null;
      }

      // TODO: should this be a parameter to parse()?
      source the_source = parameters.get(0);
      return new supertype_construct(the_supertype_kind, parameters, the_source);
    }
  };

  private static List<modifier_construct> parse_modifiers(construct the_construct) {
    // TODO(dynin): implement actual modifier parsing.
    if (!(the_construct instanceof s_expression)) {
      feedback.report(new error_signal(notification_type.PARSE_ERROR, the_construct));
    }

    List<construct> parameters = ((s_expression) the_construct).parameters();
    List<modifier_construct> result = new ArrayList<modifier_construct>();
    for (construct parameter : parameters) {
      if (parameter instanceof modifier_construct) {
        result.add((modifier_construct) parameter);
      } else {
        feedback.report(new error_signal(notification_type.MODIFIER_EXPECTED, parameter));
      }
    }
    return result;
  }
}
