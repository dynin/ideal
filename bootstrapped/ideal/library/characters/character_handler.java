// Autogenerated from library/characters.i

package ideal.library.characters;

import ideal.library.elements.*;

import javax.annotation.Nullable;

public interface character_handler extends deeply_immutable_data {
  boolean is_letter(char the_character);
  boolean is_letter_or_digit(char the_character);
  boolean is_whitespace(char the_character);
  boolean is_upper_case(char the_character);
  boolean is_digit(char the_character);
  @Nullable Integer from_digit(char the_character, Integer radix);
  char to_lower_case(char the_character);
  char from_code(Integer code);
}
