-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

implicit import ideal.runtime.texts;
import ideal.runtime.patterns.singleton_pattern;
import ideal.machine.channels.string_writer;

--- <div>Helper method for summary from a doc comment.</div>
--- TODO: this should produce |text_fragment|, not |string|.
class summary_extractor {
  extends text_visitor[string];

  static string get_summary(text_fragment the_text) {
    return instance.process(the_text);
  }

  -- TODO: infer construced type.
  public static summary_extractor instance : summary_extractor.new();

  private static dot : '.';
  private static dot_pattern : singleton_pattern[character].new(dot);

  private summary_extractor() { }

  -- TODO: inherit protected access modifier from supertype.
  protected override string process_string(string the_string) {
    range : dot_pattern.find_in(the_string, 0);
    if (range is null) {
      return the_string;
    } else {
      return the_string.slice(0, range.end);
    }
  }

  private string process_list(immutable list[text_node] nodes) {
    result : string_writer.new();

    -- TODO: use list iteration construct
    for (var nonnegative i : 0; i < nodes.size; i += 1) {
      s : process(nodes[i]);
--    for (node : nodes) {
--      s : process(node);
      result.write_all(s);

      if (s.is_not_empty && s.last == dot) {
        break;
      }
    }

    return result.extract_elements();
  }

  protected override string process_element(text_element element) {
    children : element.children;
    if (children is_not null) {
      return process(children);
    } else {
      return "";
    }
  }

  protected override string process_nodes(list_text_node nodes_list) {
    return process_list(nodes_list.nodes);
  }

  protected override string process_special(special_text t) {
    return t.to_plain_text;
  }
}
