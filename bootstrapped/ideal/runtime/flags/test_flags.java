// Autogenerated from runtime/flags/test_flags.i

package ideal.runtime.flags;

import ideal.library.elements.*;
import ideal.runtime.elements.*;

import javax.annotation.Nullable;

public class test_flags implements value {
  public static class demo_flags implements value {
    public final boolean ARG_BOOL;
    public final @Nullable string ARG_STRING;
    public demo_flags(final readonly_list<string> arguments, final procedure1<Void, string> error_reporter) {
      final dictionary<string, string> arg_dictionary = flags_utilities.parse_flags(arguments, error_reporter);
      this.ARG_BOOL = flags_utilities.boolean_flag(arg_dictionary, new base_string("ARG_BOOL"));
      this.ARG_STRING = flags_utilities.string_flag(arg_dictionary, new base_string("ARG_STRING"));
      flags_utilities.finish(arg_dictionary, error_reporter);
    }
  }
  public void test_flag_parse() {
    final test_flags.demo_flags the_demo_flags = new test_flags.demo_flags(new base_immutable_list<string>(new ideal.machine.elements.array<string>(new string[]{ new base_string("-arg-bool=true"), new base_string("-arg-string=str") })), new procedure1<Void, string>() {
      public @Override Void call(string first) {
        test_flags.this.error_reporter(first);
        return null;
      }
    });
    assert the_demo_flags.ARG_BOOL == true;
    assert ideal.machine.elements.runtime_util.values_equal(the_demo_flags.ARG_STRING, new base_string("str"));
    final test_flags.demo_flags the_demo_flags2 = new test_flags.demo_flags(new base_immutable_list<string>(new ideal.machine.elements.array<string>(new string[]{ new base_string("-noargbool"), new base_string("-arg-string:bar") })), new procedure1<Void, string>() {
      public @Override Void call(string first) {
        test_flags.this.error_reporter(first);
        return null;
      }
    });
    assert the_demo_flags2.ARG_BOOL == false;
    assert ideal.machine.elements.runtime_util.values_equal(the_demo_flags2.ARG_STRING, new base_string("bar"));
  }
  public string reported_message;
  private void error_reporter(final string message) {
    this.reported_message = message;
  }
  public void test_failed_parse() {
    final test_flags.demo_flags the_demo_flags = new test_flags.demo_flags(new base_immutable_list<string>(new ideal.machine.elements.array<string>(new string[]{ new base_string("-foo") })), new procedure1<Void, string>() {
      public @Override Void call(string first) {
        test_flags.this.error_reporter(first);
        return null;
      }
    });
    assert ideal.machine.elements.runtime_util.values_equal(this.reported_message, new base_string("Unknown flag: foo"));
    final test_flags.demo_flags the_demo_flags2 = new test_flags.demo_flags(new base_immutable_list<string>(new ideal.machine.elements.array<string>(new string[]{ new base_string("-arg-bool"), new base_string("bar") })), new procedure1<Void, string>() {
      public @Override Void call(string first) {
        test_flags.this.error_reporter(first);
        return null;
      }
    });
    assert ideal.machine.elements.runtime_util.values_equal(this.reported_message, new base_string("Non-flag parameters found--don\'t know what to do!"));
  }
  public test_flags() { }
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test(new base_string("test_flags.test_flag_parse"));
    this.test_flag_parse();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test(new base_string("test_flags.test_failed_parse"));
    this.test_failed_parse();
    ideal.machine.elements.runtime_util.end_test();
  }
}
