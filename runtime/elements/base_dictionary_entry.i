-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Simple immutable implementation of a |dictionary.entry|.
-- TODO: this datatype should be autogenerated.
public class base_dictionary_entry[readonly value key_type, value value_type] {
  implements dictionary.entry[key_type, value_type];

  private key_type the_key;
  private value_type the_value;

  public base_dictionary_entry(dictionary.entry[key_type, value_type] entry) {
    this.the_key = entry.key;
    this.the_value = entry.value;
  }

  override key_type key() {
    return the_key;
  }

  override value_type value() {
    return the_value;
  }
}
