-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

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

        boolean equals(readonly Object other) pure;
        int hashCode;
        String toString();
      }

      class String {
        extends Object;
        implements deeply_immutable data, stringable;

        integer length();
        overload String substring(integer begin);
        overload String substring(integer begin, integer end);
        char charAt(integer index);

        overload static String valueOf(int value);
        overload static String valueOf(char value);
      }

      class StringBuilder {
        extends Object;

        StringBuilder(String s);

        StringBuilder reverse();
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
