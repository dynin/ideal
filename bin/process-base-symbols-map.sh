#!/bin/sh

cat <<EOF
-- Autogenerated code.

namespace base_symbols_map {
  dictionary[string, nonnegative] symbols_map : hash_dictionary[string, nonnegative].new();

  static {
EOF

grep "public static final int" | \
    sed s/"public static final int "/"   symbols_map.put(\""/ | \
    sed s/" ="/\",/ | \
    sed s/"\;"/"\);"/

cat <<EOF
  }
}
EOF