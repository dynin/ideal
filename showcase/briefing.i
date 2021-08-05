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
    implements immutable data, stringable;

    item_id id;
    nonnegative score;
    nonnegative time;
    string title;
    string url;

    override string to_string => "item:" ++ id ++ "," ++ url;
  }

  -- Hacker News API : https://github.com/HackerNews/API
  class hacker_news_api {
    api_url_prefix : "https://hacker-news.firebaseio.com/v0/";
    parser : json_parser.new(normal_handler.instance);

    readonly list[item_id] id_list(resource_identifier list_resource) {
      content : list_resource.access_string(missing.instance).content;
      json_list : parser.parse(content) !> readonly list[readonly value];
      result : base_list[item_id].new();
      for (number_id : json_list) {
        result.append(item_id.new(number_id !> nonnegative));
      }
      return result;
    }

    readonly list[item_id] topstories() {
      return id_list(network.url(api_url_prefix ++ "topstories"++ base_extension.JSON));
    }

    item get_item(item_id id) {
      return parse_item(id, item_url(id).access_string(missing.instance).content);
    }

    resource_identifier item_url(item_id id) {
      return network.url(api_url_prefix ++ "item/" ++ id ++ base_extension.JSON);
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

  MIN_SCORE_THRESHOLD : 100;

  void start() {
    log.info("Starting...");
    hacker_news : hacker_news_api.new();

    var readonly list[item_id] top_list;
    if (true) {
      top_list = hacker_news.topstories();
    } else {
      top_list = hacker_news.id_list(filesystem.CURRENT_CATALOG.resolve("../briefing/08-02/16-00"));
    }
    log.info("Got count " ++ top_list.size);
    first : top_list.first;
    log.info("First value " ++ first);
    log.info("First item: " ++ hacker_news.get_item(first));
    for (item_id : top_list) {
      item : hacker_news.get_item(item_id);
      if (item.score >= MIN_SCORE_THRESHOLD) {
        log.info(item.title ++ " " ++ item.url ++ " (" ++ item.score ++ ")");
      }
    }
  }
}
