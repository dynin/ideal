implicit import ideal.library.elements;

class token[covariant deeply_immutable data payload_type] {
  extends deeply_immutable data;

  --payload_type payload;

  static void test_token(token[string] arg) {
    token[deeply_immutable data] v : arg;
  }
}
