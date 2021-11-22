class token[covariant deeply_immutable data payload_type] {
  extends deeply_immutable data;

  --payload_type payload;

  void new_scan_state(token[deeply_immutable data] token) {
  }

  void test_token(base_token[string] arg) {
    token[deeply_immutable data] v : arg;
    --new_scan_state(arg);
  }

  void test_list(list[string] strings) {
    strings[5] = "bar";
  }
}
