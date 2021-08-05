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
  implicit import ideal.machine.resources;

  auto_constructor class item_id {
    implements identifier;

    nonnegative id;

    override string to_string => id.to_string;
  }

  auto_constructor class item {
    implements data, stringable;

    item_id id;
    nonnegative score;
    nonnegative time;
    string title;
    string url;

    override string to_string => "item:" ++ id ++ "," ++ url;
  }

  parser : json_parser.new(normal_handler.instance);

  void start() {
    log.info("Starting...");

    file : filesystem.CURRENT_CATALOG.resolve("../briefing/08-02/16-00");
    top_content : file.access_string(missing.instance).content;

    top_list : parser.parse(top_content) !> readonly list[readonly value];
    log.info("Got count " ++ top_list.size);
    first : item_id.new(top_list[0] !> nonnegative);
    log.info("First value " ++ first);
    first_id : item_identifier(first);
    first_content : first_id.access_string(missing.instance).content;
    log.info("First content: " ++ first_content);
    log.info("First item: " ++ parse_item(first, first_content));
  }

  resource_identifier item_identifier(item_id id) {
    return network.url("https://hacker-news.firebaseio.com/v0/item/" ++ id ++ base_extension.JSON);
  }

  item parse_item(item_id id, string json) {
    item_dictionary : parser.parse(json) !> dictionary[string, readonly value];

    score : item_dictionary.get("score") !> nonnegative;
    time : item_dictionary.get("time") !> nonnegative;
    title : item_dictionary.get("title") !> string;
    url : item_dictionary.get("url") !> string;

    return item.new(id, score, time, title, url);
  }
}
