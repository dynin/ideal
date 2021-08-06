-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

program briefing {
  implicit import ideal.library.resources;
  implicit import ideal.library.texts;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.resources;
  implicit import ideal.runtime.texts;
  implicit import ideal.runtime.texts.text_library;
  implicit import ideal.runtime.patterns;
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
    string by;
    nonnegative time;
    resource_identifier url;
    nonnegative score;
    string title;

    override string to_string => "item:" ++ id ++ "," ++ url;
  }

  -- Hacker News API : https://github.com/HackerNews/API
  class hacker_news_api {
    api_url_prefix : "https://hacker-news.firebaseio.com/v0/";
    item_url_prefix : "https://news.ycombinator.com/item?id=";
    parser : json_parser.new(normal_handler.instance);
    -- Minimum host length for which the www stripping is performed.
    www_stripping_threshold : 7;
    www_prefix_pattern : list_pattern[character].new("www.");

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

      by : item_dictionary.get("by") !> string;
      time : item_dictionary.get("time") !> nonnegative;
      url_string : item_dictionary.get("url") !> (string or null);
      var resource_identifier url;
      if (url_string is_not null) {
        url = network.url(url_string);
      } else {
        url = network.url(item_url_prefix ++ id);
      }
      score : item_dictionary.get("score") !> nonnegative;
      title : item_dictionary.get("title") !> string;

      return item.new(id, by, time, url, score, title);
    }

    string short_origin(item the_item) {
      host : the_item.url.host;
      assert host is string;

      if (host.size > www_stripping_threshold) {
        www_prefix : www_prefix_pattern.match_prefix(host);
        if (www_prefix is_not null) {
          return host.skip(www_prefix);
        }
      }

      return host;
    }
  }

  MIN_SCORE_THRESHOLD : 100;

  void start() {
    log.info("Starting...");
    hacker_news : hacker_news_api.new();

    var readonly list[item_id] id_list;
    if (true) {
      id_list = hacker_news.topstories();
    } else {
      id_list = hacker_news.id_list(filesystem.CURRENT_CATALOG.resolve("../briefing/08-02/16-00"));
    }
    log.info("Got count " ++ id_list.size);
    body_content : base_list[text_fragment].new();
    for (item_id : id_list.slice(0, 10)) {
      item : hacker_news.get_item(item_id);
      if (item.score >= MIN_SCORE_THRESHOLD) {
        body_content.append(render_html(item));
        log.info(item.title ++ " " ++ hacker_news.short_origin(item) ++
            " (" ++ item.by ++ ", " ++ item.score ++ ")");
      }
    }

    body : base_element.new(BODY, text_utilities.join(body_content));
    --body_string = text_utilities.to_markup_string(body);
    log.info(text_utilities.to_markup_string(body));
  }

  text_element render_html(item the_item) {
    title : text_utilities.make_html_link(the_item.title !> base_string, the_item.url.to_string);
    origin : base_element.new(SPAN, CLASS, "origin" !> base_string, missing.instance);
    return base_element.new(DIV, text_utilities.join(title, " " !> base_string, origin));
  }
}
