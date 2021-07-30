-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Encapsulates operations on the collection of related resources.
interface resource_store {
  boolean allow_scheme(string scheme);
  boolean allow_up;
  string build_name(string scheme, immutable list[string] path) pure;
  boolean exists(string scheme, immutable list[string] path) pure;
  string read_string(string scheme, immutable list[string] path) pure;
  void make_catalog(string scheme, immutable list[string] path);
  void write_string(string scheme, immutable list[string] path, string new_value);
  readonly set[string] or null read_catalog(string scheme, immutable list[string] path) pure;
}
