// Autogenerated from isource/runtime/texts/text_formatter.i

package ideal.runtime.texts;

import ideal.library.elements.*;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.library.channels.output;
import ideal.runtime.patterns.*;

import javax.annotation.Nullable;

public abstract class text_formatter extends text_visitor<Void> implements output<text_fragment> {
  protected static final char NEWLINE = '\n';
  protected static final singleton_pattern<Character> NEWLINE_PATTERN = new singleton_pattern<Character>(NEWLINE);
  protected final output<Character> the_output;
  protected final string spaces;
  protected int indent;
  protected boolean first;
  protected text_formatter(final output<Character> the_output, final string spaces) {
    this.the_output = the_output;
    this.spaces = spaces;
    indent = 0;
    first = true;
  }
  public @Override void write(final text_fragment fragment) {
    process(fragment);
  }
  public @Override void write_all(final readonly_list<text_fragment> fragments) {
    for (int i = 0; i < fragments.size(); i += 1) {
      process(fragments.get(i));
    }
  }
  public @Override void sync() {
    the_output.sync();
  }
  public @Override void close() {
    the_output.close();
  }
  public @Override abstract Void process_string(string s);
  public @Override abstract Void process_element(text_element element);
  public @Override abstract Void process_special(special_text t);
  public @Override Void process_nodes(final list_text_node nodes) {
    process_all(nodes.nodes());
    return null;
  }
  protected void process_all(final readonly_list<text_node> nodes) {
    write_all((readonly_list<text_fragment>) (readonly_list) nodes);
  }
  protected void write_string(final string the_string) {
    int index = 0;
    while (index < the_string.size()) {
      if (first) {
        do_write_indent();
      }
      final @Nullable range newline_match = NEWLINE_PATTERN.find_in(the_string, index);
      if (newline_match == null) {
        do_write_string(the_string.slice(index));
        break;
      } else {
        final int newline_index = newline_match.begin();
        do_write_string(the_string.slice(index, newline_index));
        do_write_newline();
        index = newline_match.end();
        first = true;
      }
    }
  }
  protected void write_newline() {
    do_write_newline();
    first = true;
    the_output.sync();
  }
  protected void do_write_newline() {
    the_output.write(NEWLINE);
  }
  protected void do_write_indent() {
    for (int i = 0; i < indent; i += 1) {
      the_output.write_all(spaces);
    }
    first = false;
  }
  protected void do_write_string(final readonly_list<Character> s) {
    the_output.write_all(s);
  }
}