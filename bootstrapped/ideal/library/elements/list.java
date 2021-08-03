// Autogenerated from library/elements.i

package ideal.library.elements;

public interface list<element> extends collection<element>, readonly_list<element>, writeonly_list<element> {
  reference<element> at(Integer index);
  void append(element the_element);
  void append_all(readonly_list<element> the_list);
  void prepend(element the_element);
  element remove_last();
  element remove_at(Integer index);
}
