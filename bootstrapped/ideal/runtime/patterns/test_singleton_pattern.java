// Autogenerated from runtime/patterns/test_singleton_pattern.i

package ideal.runtime.patterns;

import ideal.library.elements.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public class test_singleton_pattern {
  public void test_match() {
    final singleton_pattern<Character> the_pattern = new singleton_pattern<Character>('x');
    assert the_pattern.call(new base_string("x"));
    assert !the_pattern.call(new base_string("y"));
    assert !the_pattern.call(new base_string("xx"));
  }
  public void test_viable_prefix() {
    final singleton_pattern<Character> the_pattern = new singleton_pattern<Character>('x');
    assert the_pattern.is_viable_prefix(new base_string(""));
    assert the_pattern.is_viable_prefix(new base_string("x"));
    assert !the_pattern.is_viable_prefix(new base_string("y"));
    assert !the_pattern.is_viable_prefix(new base_string("xx"));
  }
  public void test_match_prefix() {
    final singleton_pattern<Character> the_pattern = new singleton_pattern<Character>('x');
    assert the_pattern.match_prefix(new base_string("")) == null;
    assert the_pattern.match_prefix(new base_string("x")) == 1;
    assert the_pattern.match_prefix(new base_string("y")) == null;
    assert the_pattern.match_prefix(new base_string("xx")) == 1;
  }
  public void test_find_first() {
    final singleton_pattern<Character> the_pattern = new singleton_pattern<Character>('x');
    assert the_pattern.find_first(new base_string(""), 0) == null;
    assert the_pattern.find_first(new base_string("foo"), 0) == null;
    assert the_pattern.find_first(new base_string("xfoo"), 1) == null;
    final @Nullable range match = the_pattern.find_first(new base_string("x"), 0);
    assert match != null;
    assert match.begin() == 0;
    assert match.end() == 1;
    final @Nullable range match2 = the_pattern.find_first(new base_string("xyzzyxy"), 2);
    assert match2 != null;
    assert match2.begin() == 5;
    assert match2.end() == 6;
  }
  public void test_find_last() {
    final singleton_pattern<Character> the_pattern = new singleton_pattern<Character>('x');
    assert the_pattern.find_last(new base_string(""), null) == null;
    assert the_pattern.find_last(new base_string("foo"), null) == null;
    assert the_pattern.find_last(new base_string("foo"), 3) == null;
    assert the_pattern.find_last(new base_string("foox"), 3) == null;
    final @Nullable range match = the_pattern.find_last(new base_string("x"), 1);
    assert match != null;
    assert match.begin() == 0;
    assert match.end() == 1;
    final @Nullable range match2 = the_pattern.find_last(new base_string("xyzzyxy"), 6);
    assert match2 != null;
    assert match2.begin() == 5;
    assert match2.end() == 6;
    final @Nullable range match3 = the_pattern.find_last(new base_string("xyzzyxy"), 4);
    assert match3 != null;
    assert match3.begin() == 0;
    assert match3.end() == 1;
    final @Nullable range match4 = the_pattern.find_last(new base_string("xyzzyxy"), null);
    assert match4 != null;
    assert match4.begin() == 5;
    assert match4.end() == 6;
  }
  public void test_split() {
    final singleton_pattern<Character> the_pattern = new singleton_pattern<Character>('x');
    final immutable_list<immutable_list<Character>> split0 = the_pattern.split(new base_string("foo"));
    assert split0.size() == 1;
    assert this.equals(split0.get(0), new base_string("foo"));
    final immutable_list<immutable_list<Character>> split1 = the_pattern.split(new base_string("fooxbarx"));
    assert split1.size() == 3;
    assert this.equals(split1.get(0), new base_string("foo"));
    assert this.equals(split1.get(1), new base_string("bar"));
    assert this.equals(split1.get(2), new base_string(""));
    final immutable_list<immutable_list<Character>> split2 = the_pattern.split(new base_string("x1x2x3"));
    assert split2.size() == 4;
    assert this.equals(split2.get(0), new base_string(""));
    assert this.equals(split2.get(1), new base_string("1"));
    assert this.equals(split2.get(2), new base_string("2"));
    assert this.equals(split2.get(3), new base_string("3"));
  }
  public boolean equals(final immutable_list<Character> s0, final string s1) {
    return ideal.machine.elements.runtime_util.values_equal(((string) s0), s1);
  }
  public test_singleton_pattern() { }
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test(new base_string("test_singleton_pattern.test_match"));
    this.test_match();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test(new base_string("test_singleton_pattern.test_viable_prefix"));
    this.test_viable_prefix();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test(new base_string("test_singleton_pattern.test_match_prefix"));
    this.test_match_prefix();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test(new base_string("test_singleton_pattern.test_find_first"));
    this.test_find_first();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test(new base_string("test_singleton_pattern.test_find_last"));
    this.test_find_last();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test(new base_string("test_singleton_pattern.test_split"));
    this.test_split();
    ideal.machine.elements.runtime_util.end_test();
  }
}
