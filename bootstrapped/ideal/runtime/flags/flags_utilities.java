// Autogenerated from runtime/flags/flags_utilities.i

package ideal.runtime.flags;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.library.elements.*;
import ideal.library.patterns.*;
import ideal.runtime.elements.*;
import ideal.runtime.patterns.*;
import ideal.machine.channels.*;
import ideal.machine.characters.*;

import javax.annotation.Nullable;

public class flags_utilities {
  public static final singleton_pattern<Character> dash_pattern = new singleton_pattern<Character>('-');
  public static final option_pattern<Character> separator_pattern = new option_pattern<Character>(new base_immutable_list<pattern<Character>>(new ideal.machine.elements.array<pattern<Character>>(new pattern[]{ (pattern<Character>) (pattern) new singleton_pattern<Character>('='), new singleton_pattern<Character>(':') })));
  public static dictionary<string, string> parse_flags(final readonly_list<string> arguments, final procedure1<Void, string> error_reporter) {
    final hash_dictionary<string, string> arg_dictionary = new hash_dictionary<string, string>();
    Integer index;
    for (index = 0; index < arguments.size(); index += 1) {
      final string argument = arguments.get(index);
      if (flags_utilities.dash_pattern.match_prefix(argument) != null) {
        final @Nullable range separator = flags_utilities.separator_pattern.find_first(argument, 1);
        if (separator == null) {
          arg_dictionary.put(flags_utilities.normalize(argument.skip(1)), new base_string(""));
        } else {
          arg_dictionary.put(flags_utilities.normalize(argument.slice(1, separator.begin())), argument.skip(separator.end()));
        }
      } else {
        break;
      }
    }
    if (index < arguments.size()) {
      error_reporter.call(new base_string("Non-flag parameters found--don\'t know what to do!"));
    }
    return arg_dictionary;
  }
  private static string normalize(final string the_string) {
    final string_writer result = new string_writer();
    {
      final readonly_list<Character> c_list = (readonly_list<Character>) the_string;
      for (Integer c_index = 0; c_index < c_list.size(); c_index += 1) {
        final char c = c_list.get(c_index);
        if (c != '-' && c != '_') {
          result.write(normal_handler.instance.to_lower_case(c));
        }
      }
    }
    return result.elements();
  }
  public static boolean boolean_flag(final dictionary<string, string> arg_dictionary, final string name) {
    final string flag_name = flags_utilities.normalize(name);
    final string not_flag = ideal.machine.elements.runtime_util.concatenate(new base_string("not"), flag_name);
    if (arg_dictionary.contains_key(not_flag)) {
      arg_dictionary.remove(not_flag);
      return false;
    }
    final string no_flag = ideal.machine.elements.runtime_util.concatenate(new base_string("no"), flag_name);
    if (arg_dictionary.contains_key(no_flag)) {
      arg_dictionary.remove(no_flag);
      return false;
    }
    final @Nullable string value = arg_dictionary.get(flag_name);
    if (value == null) {
      return false;
    }
    final string flag_value = flags_utilities.normalize(value);
    arg_dictionary.remove(flag_name);
    return !ideal.machine.elements.runtime_util.values_equal(flag_value, new base_string("false")) && !ideal.machine.elements.runtime_util.values_equal(flag_value, new base_string("no"));
  }
  public static @Nullable string string_flag(final dictionary<string, string> arg_dictionary, final string name) {
    final string flag_name = flags_utilities.normalize(name);
    final boolean has_flag = arg_dictionary.contains_key(flag_name);
    if (has_flag) {
      return arg_dictionary.remove(flag_name);
    } else {
      return null;
    }
  }
  public static void finish(final dictionary<string, string> arg_dictionary, final procedure1<Void, string> error_reporter) {
    if (arg_dictionary.is_not_empty()) {
      error_reporter.call(ideal.machine.elements.runtime_util.concatenate(new base_string("Unknown flag: "), arg_dictionary.keys().elements().first()));
    }
  }
}
