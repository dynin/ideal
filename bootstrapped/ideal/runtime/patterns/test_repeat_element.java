// Autogenerated from runtime/patterns/test_repeat_element.i

package ideal.runtime.patterns;

import ideal.library.elements.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public class test_repeat_element {
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test("test_repeat_element.test_match");
    test_match();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_repeat_element.test_viable_prefix");
    test_viable_prefix();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_repeat_element.test_match_prefix");
    test_match_prefix();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_repeat_element.test_find_first");
    test_find_first();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_repeat_element.test_find_last");
    test_find_last();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test("test_repeat_element.test_split");
    test_split();
    ideal.machine.elements.runtime_util.end_test();
  }
  private boolean test_predicate(final char c) {
    return c == 'a' || c == 'b' || c == 'c';
  }
  public void test_match() {
    final repeat_element<Character> zero_or_more = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, true);
    final repeat_element<Character> one_or_more = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, false);
    assert zero_or_more.call(new base_string(""));
    assert zero_or_more.call(new base_string("a"));
    assert zero_or_more.call(new base_string("abca"));
    assert !zero_or_more.call(new base_string("abcda"));
    assert !zero_or_more.call(new base_string("y"));
    assert !zero_or_more.call(new base_string("xab"));
    assert !one_or_more.call(new base_string(""));
    assert one_or_more.call(new base_string("a"));
    assert one_or_more.call(new base_string("abca"));
    assert !one_or_more.call(new base_string("abcda"));
    assert !one_or_more.call(new base_string("y"));
    assert !one_or_more.call(new base_string("xab"));
  }
  public void test_viable_prefix() {
    final repeat_element<Character> zero_or_more = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, true);
    final repeat_element<Character> one_or_more = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, false);
    assert zero_or_more.is_viable_prefix(new base_string(""));
    assert zero_or_more.is_viable_prefix(new base_string("a"));
    assert zero_or_more.is_viable_prefix(new base_string("ab"));
    assert !zero_or_more.is_viable_prefix(new base_string("y"));
    assert !zero_or_more.is_viable_prefix(new base_string("ay"));
    assert one_or_more.is_viable_prefix(new base_string(""));
    assert one_or_more.is_viable_prefix(new base_string("a"));
    assert one_or_more.is_viable_prefix(new base_string("ab"));
    assert !one_or_more.is_viable_prefix(new base_string("y"));
    assert !one_or_more.is_viable_prefix(new base_string("ay"));
  }
  public void test_match_prefix() {
    final repeat_element<Character> zero_or_more = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, true);
    final repeat_element<Character> one_or_more = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, false);
    assert zero_or_more.match_prefix(new base_string("")) == 0;
    assert zero_or_more.match_prefix(new base_string("a")) == 1;
    assert zero_or_more.match_prefix(new base_string("abc")) == 3;
    assert zero_or_more.match_prefix(new base_string("abcdef")) == 3;
    assert zero_or_more.match_prefix(new base_string("x")) == 0;
    assert zero_or_more.match_prefix(new base_string("xabc")) == 0;
    assert zero_or_more.match_prefix(new base_string("abcabc")) == 6;
    assert one_or_more.match_prefix(new base_string("")) == null;
    assert one_or_more.match_prefix(new base_string("a")) == 1;
    assert one_or_more.match_prefix(new base_string("abc")) == 3;
    assert one_or_more.match_prefix(new base_string("abcdef")) == 3;
    assert one_or_more.match_prefix(new base_string("x")) == null;
    assert one_or_more.match_prefix(new base_string("xabc")) == null;
    assert one_or_more.match_prefix(new base_string("abcabc")) == 6;
  }
  public void test_find_first() {
    final repeat_element<Character> the_pattern = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, false);
    assert the_pattern.find_first(new base_string(""), 0) == null;
    assert the_pattern.find_first(new base_string("foo"), 0) == null;
    assert the_pattern.find_first(new base_string("bfoo"), 1) == null;
    final @Nullable range match = the_pattern.find_first(new base_string("a"), 0);
    assert match != null;
    assert match.begin() == 0;
    assert match.end() == 1;
    final @Nullable range match2 = the_pattern.find_first(new base_string("-abc-"), 0);
    assert match2 != null;
    assert match2.begin() == 1;
    assert match2.end() == 4;
    final @Nullable range match3 = the_pattern.find_first(new base_string("ayzzybacy"), 2);
    assert match3 != null;
    assert match3.begin() == 5;
    assert match3.end() == 8;
    final repeat_element<Character> the_pattern2 = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, true);
    final @Nullable range match4 = the_pattern2.find_first(new base_string(""), 0);
    assert match4 != null;
    assert match4.begin() == 0;
    assert match4.end() == 0;
    final @Nullable range match5 = the_pattern2.find_first(new base_string("xyz"), 2);
    assert match5 != null;
    assert match5.begin() == 2;
    assert match5.end() == 2;
    final @Nullable range match6 = the_pattern2.find_first(new base_string("xabcd"), 1);
    assert match6 != null;
    assert match6.begin() == 1;
    assert match6.end() == 4;
    final @Nullable range match7 = the_pattern2.find_first(new base_string("ayzzybacy"), 2);
    assert match7 != null;
    assert match7.begin() == 2;
    assert match7.end() == 2;
  }
  public void test_find_last() {
    final repeat_element<Character> the_pattern = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, false);
    assert the_pattern.find_last(new base_string(""), null) == null;
    assert the_pattern.find_last(new base_string("foo"), null) == null;
    assert the_pattern.find_last(new base_string("foo"), 3) == null;
    assert the_pattern.find_last(new base_string("fooc"), 3) == null;
    final @Nullable range match = the_pattern.find_last(new base_string("c"), 1);
    assert match != null;
    assert match.begin() == 0;
    assert match.end() == 1;
    final @Nullable range match2 = the_pattern.find_last(new base_string("ayzzzby"), 6);
    assert match2 != null;
    assert match2.begin() == 5;
    assert match2.end() == 6;
    final @Nullable range match3 = the_pattern.find_last(new base_string("abczyby"), 4);
    assert match3 != null;
    assert match3.begin() == 0;
    assert match3.end() == 3;
    final @Nullable range match4 = the_pattern.find_last(new base_string("ayzzyabcy"), null);
    assert match4 != null;
    assert match4.begin() == 5;
    assert match4.end() == 8;
    final repeat_element<Character> the_pattern2 = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, true);
    final @Nullable range match5 = the_pattern2.find_last(new base_string(""), null);
    assert match5 != null;
    assert match5.begin() == 0;
    assert match5.end() == 0;
    final @Nullable range match6 = the_pattern2.find_last(new base_string("foobar"), 2);
    assert match6 != null;
    assert match6.begin() == 2;
    assert match6.end() == 2;
    final @Nullable range match7 = the_pattern2.find_last(new base_string("foobar"), 5);
    assert match7 != null;
    assert match7.begin() == 3;
    assert match7.end() == 5;
    final @Nullable range match8 = the_pattern2.find_last(new base_string("ayzzyabcy"), null);
    assert match8 != null;
    assert match8.begin() == 9;
    assert match8.end() == 9;
  }
  public void test_split() {
    final repeat_element<Character> the_pattern = new repeat_element<Character>(new function1<Boolean, Character>() {
      @Override public Boolean call(Character first) {
        return test_repeat_element.this.test_predicate(first);
      }
    }, false);
    final immutable_list<immutable_list<Character>> split0 = the_pattern.split(new base_string("foo"));
    assert split0.size() == 1;
    assert this.equals(split0.get(0), new base_string("foo"));
    final immutable_list<immutable_list<Character>> split1 = the_pattern.split(new base_string("fooabcxyzc"));
    assert split1.size() == 3;
    assert this.equals(split1.get(0), new base_string("foo"));
    assert this.equals(split1.get(1), new base_string("xyz"));
    assert this.equals(split1.get(2), new base_string(""));
    final immutable_list<immutable_list<Character>> split2 = the_pattern.split(new base_string("ab1bc2ca3"));
    assert split2.size() == 4;
    assert this.equals(split2.get(0), new base_string(""));
    assert this.equals(split2.get(1), new base_string("1"));
    assert this.equals(split2.get(2), new base_string("2"));
    assert this.equals(split2.get(3), new base_string("3"));
  }
  public boolean equals(final immutable_list<Character> s0, final string s1) {
    return ideal.machine.elements.runtime_util.values_equal(((string) s0), s1);
  }
}