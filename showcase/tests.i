implicit import ideal.runtime.elements;

class tests {
  void test_list() {
    --readonly list[string] subtype_list : base_list[string].new("foo");
    --readonly list[readonly stringable] supertype_list : subtype_list;

    string_list : [ "foo", ];
    readonly list[deeply_immutable value] value_list : string_list;
  }
}
