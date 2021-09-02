import ideal.runtime.elements.base_list;

void test_list() {
  readonly list[string] subtype_list : base_list[string].new("foo");
  readonly list[readonly value] supertype_list : subtype_list;
}
