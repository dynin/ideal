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
import java.util.List;

public class create {

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

    constructs = new transform.to_java_transform(the_context).transform_all(constructs);

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
