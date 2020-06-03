implicit import ideal.library.elements;
implicit import ideal.runtime.elements;
implicit import ideal.runtime.logs;

enum declaration_pass {
  implements deeply_immutable data, stringable, readonly displayable;

  NONE;
  TYPES_AND_PROMOTIONS;
  METHODS_AND_VARIABLES;

  boolean is_before(declaration_pass other) {
    return this.ordinal() < other.ordinal();
  }

  boolean is_after(declaration_pass other) {
    return this.ordinal() > other.ordinal();
  }

  static declaration_pass last() {
    return values()[values().length - 1];
  }

  override string display() {
    return to_string();
  }

  override string to_string() {
    return base_string.new(name(), "/", String.valueOf(ordinal()));
  }
}
