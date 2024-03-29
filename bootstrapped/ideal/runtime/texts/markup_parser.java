// Autogenerated from runtime/texts/markup_parser.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;

public class markup_parser implements value {
  public final markup_grammar the_markup_grammar;
  public final procedure1<Void, string> error_reporter;
  public markup_parser(final markup_grammar the_markup_grammar, final procedure1<Void, string> error_reporter) {
    this.the_markup_grammar = the_markup_grammar;
    this.error_reporter = error_reporter;
  }
  public void report_error(final string error_message) {
    this.error_reporter.call(error_message);
  }
  public text_element parse(final string text) {
    return this.the_markup_grammar.parse(text, this);
  }
}
