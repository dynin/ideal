-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Format text for output as plain text.
abstract class text_formatter {
  implements output[text_fragment];
  extends text_visitor[void];

  implicit import ideal.runtime.patterns;

  protected static character NEWLINE : '\n';
  protected static NEWLINE_PATTERN : singleton_pattern[character].new(NEWLINE);

  protected output[character] the_output;
  protected string spaces;
  protected var nonnegative indent;
  protected var boolean first;

  protected text_formatter(output[character] the_output, string spaces) {
    this.the_output = the_output;
    this.spaces = spaces;
    indent = 0;
    first = true;
  }

  override void write(text_fragment fragment) {
    process(fragment);
  }

  override void write_all(readonly list[text_fragment] fragments) {
    for (var nonnegative i : 0; i < fragments.size; i += 1) {
      process(fragments[i]);
    }
  }

  override void sync() {
    the_output.sync();
  }

  override void close() {
    the_output.close();
  }

  override abstract void process_string(string s);

  override abstract void process_element(text_element element);

  override abstract void process_special(special_text t);

  override void process_nodes(list_text_node nodes) {
    process_all(nodes.nodes());
  }

  protected void process_all(readonly list[text_node] nodes) {
    -- TODO: this cast should be redundant.
    write_all(nodes as readonly list[text_fragment]);
  }

  protected void write_string(string the_string) {
    var nonnegative index : 0;
    while (index < the_string.size) {
      if (first) {
        do_write_indent();
      }
      newline_match : NEWLINE_PATTERN.find_in(the_string, index);
      if (newline_match is null) {
        do_write_string(the_string.skip(index));
        break;
      } else {
        newline_index : newline_match.begin;
        do_write_string(the_string.slice(index, newline_index));
        do_write_newline();
        index = newline_match.end;
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
    for (var nonnegative i : 0; i < indent; i += 1) {
      the_output.write_all(spaces);
    }
    first = false;
  }

  -- TODO: string should work here.
  protected void do_write_string(readonly list[character] s) {
    the_output.write_all(s);
  }
}
