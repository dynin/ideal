import ideal.runtime.elements.base_list;
implicit import ideal.library.texts;

void test_list() {
  readonly list[string] subtype_list : base_list[string].new("foo");
  readonly list[readonly stringable] supertype_list : subtype_list;

  string_list : [ "foo", ];
  readonly list[immutable value] value_list : string_list;

  text_fragment foo : "bar";
}
