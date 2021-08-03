// Autogenerated from runtime/characters/test_character_handler.i

package ideal.runtime.characters;

import ideal.library.elements.*;
import ideal.library.characters.*;
import ideal.machine.characters.normal_handler;

public class test_character_handler {
  public void predicate_test() {
    final normal_handler the_character_handler = normal_handler.instance;
    assert the_character_handler.is_letter('x');
    assert !the_character_handler.is_letter('6');
    assert the_character_handler.is_letter_or_digit('x');
    assert the_character_handler.is_letter_or_digit('8');
    assert !the_character_handler.is_letter_or_digit(' ');
    assert the_character_handler.is_whitespace(' ');
    assert !the_character_handler.is_whitespace('x');
    assert !the_character_handler.is_whitespace('4');
    assert the_character_handler.is_upper_case('X');
    assert !the_character_handler.is_upper_case('x');
    assert !the_character_handler.is_upper_case('5');
    assert the_character_handler.is_digit('0');
    assert the_character_handler.is_digit('5');
    assert !the_character_handler.is_digit('x');
    assert !the_character_handler.is_digit('?');
  }
  public void digit_test() {
    assert ideal.machine.elements.runtime_util.values_equal(radix.MINIMUM_RADIX, 2);
    assert ideal.machine.elements.runtime_util.values_equal(radix.DEFAULT_RADIX, 10);
    assert ideal.machine.elements.runtime_util.values_equal(radix.MAXIMUM_RADIX, 36);
    final normal_handler the_character_handler = normal_handler.instance;
    assert ideal.machine.elements.runtime_util.values_equal(the_character_handler.from_digit('0', radix.DEFAULT_RADIX), 0);
    assert ideal.machine.elements.runtime_util.values_equal(the_character_handler.from_digit('5', radix.DEFAULT_RADIX), 5);
    assert ideal.machine.elements.runtime_util.values_equal(the_character_handler.from_digit('F', 16), 15);
    assert the_character_handler.from_digit('X', 16) == null;
  }
  public void conversion_test() {
    final normal_handler the_character_handler = normal_handler.instance;
    assert the_character_handler.to_lower_case('X') == 'x';
    assert the_character_handler.to_lower_case('x') == 'x';
    assert the_character_handler.to_lower_case('5') == '5';
  }
  public test_character_handler() { }
  public void run_all_tests() {
    ideal.machine.elements.runtime_util.start_test(new ideal.runtime.elements.base_string("test_character_handler.predicate_test"));
    this.predicate_test();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test(new ideal.runtime.elements.base_string("test_character_handler.digit_test"));
    this.digit_test();
    ideal.machine.elements.runtime_util.end_test();
    ideal.machine.elements.runtime_util.start_test(new ideal.runtime.elements.base_string("test_character_handler.conversion_test"));
    this.conversion_test();
    ideal.machine.elements.runtime_util.end_test();
  }
}
