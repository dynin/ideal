-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Works together with flags_data extension.
namespace flags_utilities {
  implicit import ideal.library.elements;
  implicit import ideal.library.patterns;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.patterns;
  implicit import ideal.machine.channels;
  implicit import ideal.machine.characters;

  dash_pattern : singleton_pattern[character].new('-');
  separator_pattern : option_pattern[character].new([
      -- TODO: cast is redundant.
      singleton_pattern[character].new('=') .> pattern[character],
      singleton_pattern[character].new(':')
  ]);

  dictionary[string, string] parse_flags(readonly list[string] arguments,
      procedure[void, string] error_reporter) {

    arg_dictionary : hash_dictionary[string, string].new();
    var integer index;

    for (index = 0; index < arguments.size; index += 1) {
      argument : arguments[index];
      if (dash_pattern.match_prefix(argument) is_not null) {
        separator : separator_pattern.find_first(argument, 1);
        if (separator is null) {
          arg_dictionary.put(normalize(argument.skip(1)), "");
        } else {
          arg_dictionary.put(normalize(argument.slice(1, separator.begin)),
              argument.skip(separator.end));
        }
      } else {
        break;
      }
    }

    if (index < arguments.size) {
      error_reporter("Non-flag parameters found--don't know what to do!");
    }

    return arg_dictionary;
  }

  private string normalize(string the_string) {
    result : string_writer.new();
    for (c : the_string) {
      if (c != '-' && c != '_') {
        result.write(unicode_handler.instance.to_lower_case(c));
      }
    }
    return result.elements();
  }

  boolean boolean_flag(dictionary[string, string] arg_dictionary, string name) {
    flag_name : normalize(name);

    not_flag : "not" ++ flag_name;
    if (arg_dictionary.contains_key(not_flag)) {
      arg_dictionary.remove(not_flag);
      return false;
    }

    no_flag : "no" ++ flag_name;
    if (arg_dictionary.contains_key(no_flag)) {
      arg_dictionary.remove(no_flag);
      return false;
    }

    value : arg_dictionary.get(flag_name);
    if (value is null) {
      return false;
    }

    flag_value : normalize(value);
    arg_dictionary.remove(flag_name);
    return flag_value != "false" && flag_value != "no";
  }

  string or null string_flag(dictionary[string, string] arg_dictionary, string name) {
    flag_name : normalize(name);
    has_flag : arg_dictionary.contains_key(flag_name);

    if (has_flag) {
      return arg_dictionary.remove(flag_name);
    } else {
      return missing.instance;
    }
  }

  void finish(dictionary[string, string] arg_dictionary, procedure[void, string] error_reporter) {
    if (arg_dictionary.is_not_empty) {
      error_reporter("Unknown flag: " ++ arg_dictionary.keys().elements.first);
    }
  }
}
