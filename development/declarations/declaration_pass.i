enum declaration_pass {
  implements deeply_immutable data, stringable, readonly displayable;

  NONE;
  TYPES_AND_PROMOTIONS;
  METHODS_AND_VARIABLES;

  boolean is_before(declaration_pass other) {
    return this.ordinal < other.ordinal;
  }

  boolean is_after(declaration_pass other) {
    return this.ordinal > other.ordinal;
  }

  -- TODO: fix hack to handle Java strings
  override string to_string => "" ++ name;

  override string display() => to_string;

  -- TODO: static declaration_pass last() => values()[values().length - 1];
  static declaration_pass last => declaration_pass.METHODS_AND_VARIABLES;
}
