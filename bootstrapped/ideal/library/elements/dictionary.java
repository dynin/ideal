// Autogenerated from library/elements.i

package ideal.library.elements;

import javax.annotation.Nullable;

public interface dictionary<key_type, value_type> extends collection<dictionary.entry<key_type, value_type>>, readonly_dictionary<key_type, value_type>, writeonly_dictionary<key_type, value_type> {
  public interface any_entry<key_type, value_type> extends any_value { }
  public interface readonly_entry<key_type, value_type> extends readonly_value, any_entry<key_type, value_type> {
    key_type key();
    value_type value();
  }
  public interface writeonly_entry<key_type, value_type> extends writeonly_value, any_entry<key_type, value_type> { }
  public interface entry<key_type, value_type> extends readonly_value, readonly_entry<key_type, value_type>, writeonly_entry<key_type, value_type> { }
  public interface immutable_entry<key_type, value_type> extends immutable_value, readonly_entry<key_type, value_type> { }
  public interface deeply_immutable_entry<key_type, value_type> extends deeply_immutable_value, immutable_entry<key_type, value_type> { }
  @Nullable value_type put(key_type key, value_type value);
  @Nullable value_type remove(key_type key);
}
