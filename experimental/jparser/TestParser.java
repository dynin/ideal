/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.jparser;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class TestParser {
  private static final String JAVA_SOURCE = "class foo {\n}\n";

  public static void main(String[] args) {
    CharStream input = CharStreams.fromString(JAVA_SOURCE);

    JavaLexer lexer = new JavaLexer(input);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);

    JavaParser parser = new JavaParser(tokenStream);
    ParseTree compilationUnit = parser.compilationUnit();

    System.out.println(compilationUnit.toStringTree(parser));
  }
}
