/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.jparser;

import ideal.development.jparser.JavaParser.*;

public class JavaTreeVisitor extends JavaParserBaseVisitor<String> {
  @Override
  public String visitCompilationUnit(CompilationUnitContext ctx) {
    return "test";
  }
}
