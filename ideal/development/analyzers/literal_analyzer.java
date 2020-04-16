/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.analyzers;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.literals.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.names.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import ideal.development.values.*;

public class literal_analyzer extends single_pass_analyzer {

  private static final boolean USE_JAVA_STRING_TYPE = false;

  public final literal the_literal;

  public literal_analyzer(literal_construct source) {
    super(source);
    the_literal = source.the_literal;
  }

  @Override
  protected action do_single_pass_analysis() {
    if (the_literal instanceof integer_literal) {
      int the_value = ((integer_literal) the_literal).the_value();
      type bound = the_value >= 0 ? library().immutable_nonnegative_type() :
          library().immutable_integer_type();
      return new integer_value(the_value, bound).to_action(this);
    } else if (the_literal instanceof quoted_literal) {
      quoted_literal the_quoted_literal = (quoted_literal) the_literal;
      string the_value = the_quoted_literal.the_value();
      type bound;
      if (USE_JAVA_STRING_TYPE && jinterop_library.is_enabled()) {
        bound = jinterop_library.get_instance().string_type().
            get_flavored(flavors.mutable_flavor);
      } else {
        if (the_quoted_literal.quote == punctuation.SINGLE_QUOTE) {
          bound = library().immutable_character_type();
        } else if (the_quoted_literal.quote == punctuation.DOUBLE_QUOTE) {
          bound = library().immutable_string_type();
        } else {
          utilities.panic("Unrecognized quote type: " + the_quoted_literal.quote);
          return null;
        }
      }
      return new base_string_value(the_value, bound).to_action(this);
    } else {
      utilities.panic("Unrecognized literal: " + the_literal);
      return null;
    }
  }
}
