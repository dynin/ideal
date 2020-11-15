-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

-- Interoperability with Java(tm).

namespace adapters {
  import ideal.library.elements.value;
  import ideal.library.elements.data;
  import ideal.library.elements.character;
  import ideal.library.elements.integer;
  import ideal.library.elements.boolean;
  import ideal.library.elements.stringable;

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

        public overload static String valueOf(int value);
        public overload static String valueOf(char value);
      }

      class StringBuilder {
        extends Object;

        public StringBuilder(String s);

        public StringBuilder reverse();
      }
    }
  }

  package javax {
    package annotation {
      interface Nullable {
      }
    }
  }
}
