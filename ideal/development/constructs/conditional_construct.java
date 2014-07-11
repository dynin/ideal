/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.constructs;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;

public class conditional_construct extends base_construct {
  public final construct cond_expr;
  public final construct then_expr;
  public final @Nullable construct else_expr;
  public final boolean is_statement;
  public conditional_construct(construct cond_expr,
                        construct then_expr,
		        @Nullable construct else_expr,
			boolean is_statement,
		        position pos) {
    super(pos);
    this.cond_expr = cond_expr;
    this.then_expr = then_expr;
    this.else_expr = else_expr;
    this.is_statement = is_statement;
  }

  public readonly_list<construct> children() {
    list<construct> result = new base_list<construct>(cond_expr, then_expr);
    if (else_expr != null) {
      result.append(else_expr);
    }
    return result;
  }
}
