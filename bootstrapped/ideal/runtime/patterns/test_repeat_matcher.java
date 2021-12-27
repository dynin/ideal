// Autogenerated from runtime/patterns/test_repeat_matcher.i

package ideal.runtime.patterns;

import ideal.library.elements.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;

public class test_repeat_matcher implements value {
  private boolean match_a(final char c) {
    return c == 'a' || c == 'A';
  }
  private boolean match_b(final char c) {
    return c == 'b' || c == 'B';
  }
  private boolean match_c(final char c) {
    return c == 'c' || c == 'C';
  }
  private string as_string(final readonly_list<Character> char_list) {
    return (string) char_list.frozen_copy();
  }
  private string join_list(final readonly_list<string> strings) {
    string result = new base_string("");
    {
      final readonly_list<string> the_string_list = strings;
      for (Integer the_string_index = 0; the_string_index < the_string_list.size(); the_string_index += 1) {
        final string the_string = the_string_list.get(the_string_index);
        if (result.is_empty()) {
          result = the_string;
        } else {
          result = ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(result, new base_string("/")), the_string);
        }
      }
    }
    return result;
  }
  public pattern<Character> make_matcher(final function1<Boolean, Character> the_predicate) {
    return ((pattern<Character>) (Object) new procedure_matcher<Character, string>(new repeat_element<Character>(the_predicate, false), new procedure1<string, readonly_list<Character>>() {
      public @Override string call(readonly_list<Character> first) {
        return test_repeat_matcher.this.as_string(first);
      }
    }));
  }
  public string match_procedure(final readonly_list<Object> the_list) {
    string result = new base_string("");
    {
      final readonly_list<Object> element_list = the_list;
      for (Integer element_index = 0; element_index < element_list.size(); element_index += 1) {
        final Object element = element_list.get(element_index);
        assert element instanceof string;
        result = ideal.machine.elements.runtime_util.concatenate(ideal.machine.elements.runtime_util.concatenate(result, new base_string("-")), ((string) element));
      }
    }
    return result;
  }
  public matcher<Character, string> make_pattern(final boolean do_match_empty) {
    final immutable_list<pattern<Character>> matcher_list = new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ this.make_matcher(new function1<Boolean, Character>() {
      public @Override Boolean call(Character first) {
        return test_repeat_matcher.this.match_a(first);
      }
    }), this.make_matcher(new function1<Boolean, Character>() {
      public @Override Boolean call(Character first) {
        return test_repeat_matcher.this.match_b(first);
      }
    }), this.make_matcher(new function1<Boolean, Character>() {
      public @Override Boolean call(Character first) {
        return test_repeat_matcher.this.match_c(first);
      }
    }) }));
    return ((matcher<Character, string>) (Object) new repeat_matcher<Character, string, string>(new sequence_matcher<Character, string>(matcher_list, new procedure1<string, readonly_list<Object>>() {
      public @Override string call(readonly_list<Object> first) {
        return test_repeat_matcher.this.match_procedure(first);
      }
    }), do_match_empty, new procedure1<string, readonly_list<string>>() {
      public @Override string call(readonly_list<string> first) {
        return test_repeat_matcher.this.join_list(first);
      }
    }));
  }
  public void test_match() {
    final matcher<Character, string> the_matcher = this.make_pattern(true);
    assert ((function1<Boolean, readonly_list<Character>>) (Object) the_matcher).call(new base_string("abc"));
    assert ((function1<Boolean, readonly_list<Character>>) (Object) the_matcher).call(new base_string("AbCabc"));
    assert ((function1<Boolean, readonly_list<Character>>) (Object) the_matcher).call(new base_string("AaaBbCccABC"));
    assert ((function1<Boolean, readonly_list<Character>>) (Object) the_matcher).call(new base_string("AaaBbCccABCabc"));
    assert ((function1<Boolean, readonly_list<Character>>) (Object) the_matcher).call(new base_string("AaaBBBCcc"));
    assert !((function1<Boolean, readonly_list<Character>>) (Object) the_matcher).call(new base_string("bac"));
    assert !((function1<Boolean, readonly_list<Character>>) (Object) the_matcher).call(new base_string("aabb"));
    assert !((function1<Boolean, readonly_list<Character>>) (Object) the_matcher).call(new base_string("aaca"));
  }
  public void test_parse() {
    final matcher<Character, string> the_matcher = this.make_pattern(true);
    assert ideal.machine.elements.runtime_util.values_equal(the_matcher.parse(new base_string("abc")), new base_string("-a-b-c"));
    assert ideal.machine.elements.runtime_util.values_equal(the_matcher.parse(new base_string("AbC")), new base_string("-A-b-C"));
    assert ideal.machine.elements.runtime_util.values_equal(the_matcher.parse(new base_string("AaaBbCcc")), new base_string("-Aaa-Bb-Ccc"));
    assert ideal.machine.elements.runtime_util.values_equal(the_matcher.parse(new base_string("AaaBBBCcc")), new base_string("-Aaa-BBB-Ccc"));
    assert ideal.machine.elements.runtime_util.values_equal(the_matcher.parse(new base_string("abcABC")), new base_string("-a-b-c/-A-B-C"));
    assert ideal.machine.elements.runtime_util.values_equal(the_matcher.parse(new base_string("AbCaBc")), new base_string("-A-b-C/-a-B-c"));
    assert ideal.machine.elements.runtime_util.values_equal(the_matcher.parse(new base_string("AABBCCabcABC")), new base_string("-AA-BB-CC/-a-b-c/-A-B-C"));
  }
  public test_repeat_matcher() { }
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test(new base_string("test_repeat_matcher.test_match"));
    this.test_match();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test(new base_string("test_repeat_matcher.test_parse"));
    this.test_parse();
    ideal.machine.elements.runtime_util.end_test();
  }
}
