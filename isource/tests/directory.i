-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

-- Simple directory API: interfaces

interface directory {
  group or null get_group(string name) pure;
}

interface group {
  -- For simplicity we assume that names are unique.
  entry or null lookup(string name) pure;
}

public interface entry {
  string or null get_phone(string type) pure;
}

-- Simple directory API: implementation

public datatype directory_impl {
  implements directory;

  dictionary[string, group] groups;

  implement group or null get_group(string name) pure {
    return groups.get(name);
  }
}

public datatype group_impl {
  implements group;

  dictionary[string, entry] entries;

  implement entry or null lookup(string name) pure {
    return entries.get(name);
  }
}

public datatype entry_impl {
  implements entry;

  dictionary[string, string] phones;

  implement string or null get_phone(string type) pure {
    return phones.get(type);
  }
}

-- Simple directory API: use

string lookup(directory the_directory, string group, string name, string type) pure {
  -- Type is inferred; assertions narrow the type to non-null so the type checker doesn't complain
  the_group : the_directory.get_group(group);
  assert the_group is_not null;

  the_entry : the_group.lookup(name);
  assert the_entry is_not null;

  the_phone : the_entry.get_phone(type);
  assert the_phone is_not null;

  return the_phone;
}

void test(directory the_directory) {
  return println(lookup(the_directory, "work", "Jane Hacker", "mobile"));
}

-- Now things get more interesting.

interface group_v2 {
  entry_v2 or null lookup(string name) pure;
  -- The query is just a predicate;
  -- where the query is executed is implementation-dependent
  set[entry_v2] query(function[boolean, entry_v2] the_query) pure;
}

public interface entry_v2 {
  readonly string name;
  string or null get_phone(string type) pure;
}

public datatype group_v2_impl {
  implements group_v2;

  dictionary[string, entry_v2] entries;

  implement entry_v2 or null lookup(string name) pure {
    return entries.get(name);
  }

  implement set[entry_v2] query(function[boolean, entry_v2] the_query) pure {
    -- This will return a collection that will be converted into a set
    -- return entries.values.filter(the_query);
  }
}

public datatype entry_v2_impl {
  implements entry_v2;

  readonly string name;
  dictionary[string, string] phones;

  implement string or null get_phone(string type) pure {
    return phones.get(type);
  }
}

boolean custom_predicate(entry_v2 the_entry) pure {
  name : the_entry.name;
  -- A name that starts with 'J': matches "Jim" and "John".
  return !name.is_empty && name[0] == 'J';
}

void find_js(group_v2 the_group) {
  set[entry_v2] names_starting_with_js : the_group.query(custom_predicate);
  println("Number of people whose names start with 'J': ", names_starting_with_js.size);
}
