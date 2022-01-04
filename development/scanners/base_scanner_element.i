-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

abstract class base_scanner_element {
  implements scanner_element;

  private var the scanner_config;

  override set_config(the scanner_config) {
    this.the_scanner_config = the_scanner_config;
  }

  protected var scanner_config config() {
    assert the_scanner_config is_not null;
    return the_scanner_config;
  }

  protected var character_handler the_character_handler => config.the_character_handler;
}
