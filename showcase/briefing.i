-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

program briefing {
  implicit import ideal.library.resources;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.resources;
  implicit import ideal.runtime.logs;
  implicit import ideal.runtime.formats;
  implicit import ideal.machine.resources;
  implicit import ideal.machine.characters;

  auto_constructor class item_id {
    implements identifier;

    nonnegative id;

    override string to_string() pure {
      --readonly stringable value : id;
      --return id.to_string;
      return "foo";
    }
  }

  parser : json_parser.new(normal_handler.instance);

  void start() {
    log.info("Starting...");

    file : filesystem.CURRENT_CATALOG.resolve("../briefing/08-02/16-00");
    top_content : file.access_string(missing.instance).content;

    top_list : parser.parse(top_content) !> readonly list[readonly value];
    log.info("Got count " ++ top_list.size);
  }
}
