// Autogenerated from isource/library/elements.i

package ideal.library.elements;

public interface readonly_list<element> extends readonly_collection<element>, any_list<element> {
  element get(int index);
  immutable_list<element> slice(int begin);
  immutable_list<element> slice(int begin, int end);
  immutable_list<element> frozen_copy();
}
