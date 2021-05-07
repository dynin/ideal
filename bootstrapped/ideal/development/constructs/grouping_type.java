// Autogenerated from development/constructs/grouping_type.i

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.runtime.logs.*;
import ideal.development.names.punctuation;

public class grouping_type implements identifier, readonly_displayable {
  public final string name;
  public final token_type start;
  public final token_type end;
  private grouping_type(final string name, final token_type start, final token_type end) {
    this.name = name;
    this.start = start;
    this.end = end;
  }
  public @Override string to_string() {
    return this.name;
  }
  public @Override string display() {
    return this.to_string();
  }
  public static final grouping_type PARENS = new grouping_type(new base_string("parens"), punctuation.OPEN_PARENTHESIS, punctuation.CLOSE_PARENTHESIS);
  public static final grouping_type BRACKETS = new grouping_type(new base_string("brackets"), punctuation.OPEN_BRACKET, punctuation.CLOSE_BRACKET);
  public static final grouping_type ANGLE_BRACKETS = new grouping_type(new base_string("angle_brackets"), punctuation.LESS_THAN, punctuation.GREATER_THAN);
  public static final grouping_type BRACES = new grouping_type(new base_string("braces"), punctuation.OPEN_BRACE, punctuation.CLOSE_BRACE);
}