-- Copyright 2014 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

-- Interoperability with Java(tm).

implicit import ideal.library.elements;

package java {

  package builtins {
    interface primitive_type {
      extends deeply_immutable data;
    }
    class int {
      implements primitive_type;
      implements integer;
    }
    class char {
      implements primitive_type;
      implements character;
      implements integer;  -- That's how it works in Java.
    }
    enum boolean {
      implements primitive_type;
    }
  }

  package lang {
    import builtins.int;
    import builtins.char;

    class Object {
      extends value;

      public boolean equals(readonly Object other) pure;
      public integer hashCode() pure;
      public String toString();
    }

    class String {
      extends Object;
      implements deeply_immutable data, stringable;

      public integer length();
      public overload String substring(integer begin);
      public overload String substring(integer begin, integer end);
      public char charAt(integer index);

      public static String valueOf(int value);
      public static String valueOf(char value);
    }

    class StringBuffer {
      extends Object;

      public StringBuffer(String s);

      public StringBuffer reverse();
    }
  }
}

package javax {
  package annotation {
    interface Nullable {
    }
  }
}
