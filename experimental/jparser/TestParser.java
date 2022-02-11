/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.jparser;

import java.io.IOException;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.library.channels.*;
import ideal.runtime.elements.*;
import ideal.runtime.texts.*;
import ideal.machine.channels.standard_channels;
import ideal.development.elements.*;
import ideal.development.printers.*;

import ideal.development.jparser.JavaParser.CompilationUnitContext;

public class TestParser {
  private static final String JAVA_SOURCE = "class foo {\n}\n";
  private static final boolean PRINT_TREE = false;

  public static void main(String[] args) {
    CharStream input;

    if (args.length == 1) {
      try {
        input = CharStreams.fromFileName(args[0]);
      } catch (IOException e) {
        utilities.panic(e.toString());
        return;
      }
    } else {
      input = CharStreams.fromString(JAVA_SOURCE);
    }

    JavaLexer lexer = new JavaLexer(input);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);

    JavaParser parser = new JavaParser(tokenStream);
    CompilationUnitContext compilationUnit = parser.compilationUnit();

    if (PRINT_TREE) {
      System.out.println(compilationUnit.toStringTree(parser));
    }

    JavaConstructBuilder constructBuilder = new JavaConstructBuilder(parser);
    readonly_list<construct> statements = constructBuilder.visitCompilationUnit(compilationUnit);
    output<text_fragment> out = new plain_formatter(standard_channels.stdout);
    out.write(new java_printer(printer_mode.CURLY).print_statements(statements));
  }
}
