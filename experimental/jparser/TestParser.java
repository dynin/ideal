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

import ideal.runtime.elements.*;

public class TestParser {
  private static final String JAVA_SOURCE = "class foo {\n}\n";

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
    ParseTree compilationUnit = parser.compilationUnit();

    System.out.println(compilationUnit.toStringTree(parser));

    JavaTreeVisitor treeVisitor = new JavaTreeVisitor(parser);
    System.out.println(treeVisitor.visit(compilationUnit));
  }
}
