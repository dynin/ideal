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
    nonnegative descendants;

    override string to_string => "item:" ++ id ++ "," ++ url;
  }

  -- Hacker News API : https://github.com/HackerNews/API
  namespace hacker_news {
    api_url_prefix : "https://hacker-news.firebaseio.com/v0/";
    item_page_url_prefix : "https://news.ycombinator.com/item?id=";
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

    resource_identifier item_page_url(item_id id) {
      return network.url(item_page_url_prefix ++ id);
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
        url = item_page_url(id);
      }
      score : item_dictionary.get("score") !> nonnegative;
      title : item_dictionary.get("title") !> string;
      descendants : item_dictionary.get("descendants") !> nonnegative;

      return item.new(id, by, time, url, score, title, descendants);
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

  PROGRAM_NAME : "hacker news digest";
  MIN_SCORE_THRESHOLD : 100;
  HTML_FILE : filesystem.CURRENT_CATALOG.resolve("tmp/news-not-paper.html");

  HEADER_CLASS : "header";
  TITLE_CLASS : "title";
  ORIGIN_CLASS : "origin";
  BY_CLASS : "by";
  SCORE_CLASS : "score";
  DISCUSSION_CLASS : "discussion";

  auto_constructor class date {
    implements immutable data, equality_comparable, stringable;

    nonnegative month;
    nonnegative day;

    override string to_string => two_digit(month) ++ "-" ++ two_digit(day);

    string dotted => "2021" ++ "." ++ two_digit(month) ++ "." ++ two_digit(day);

    date next() {
      if (month == 7 && day == 31) {
        return date.new(8, 1);
      } else {
        return date.new(month, day + 1);
      }
    }

    date prev() {
      if (month == 8 && day == 1) {
        return date.new(7, 31);
      } else {
        new_day : day - 1;
        assert new_day is nonnegative;
        return date.new(month, new_day);
      }
    }

    private static string two_digit(nonnegative number) {
      if (number < 10) {
        return "0" ++ number;
      } else {
        return number.to_string;
      }
    }
  }

  briefing_catalog : filesystem.CURRENT_CATALOG.resolve("../briefing/").access_catalog();
  first : date.new(8, 8);
  --first : date.new(7, 6);
  last : date.new(8, 9);

  void start() {
    log.info("Starting...");

    hash : hash_set[item_id].new();

    var readonly list[item_id] id_list;
    if (true) {
      id_list = hacker_news.topstories();
    } else {
      id_list = hacker_news.id_list(briefing_catalog.resolve("08-02/16-00"));
    }
    log.info("Got count " ++ id_list.size);

    page : render_page(id_list.slice(0, 50), last);
    HTML_FILE.access_string(missing.instance).content = text_utilities.to_markup_string(page);
    --log.info(text_utilities.to_markup_string(html));
  }

  text_element render_page(readonly list[item_id] ids, date the_date) {
    body_content : base_list[text_fragment].new();
    body_content.append(make_header(the_date));
    body_content.append(base_element.new(HR));

    for (item_id : ids) {
      item : hacker_news.get_item(item_id);
      if (item.score >= MIN_SCORE_THRESHOLD) {
        body_content.append(render_html(item));
        log.info(item.title ++ " " ++ hacker_news.short_origin(item) ++
            " (" ++ item.by ++ ", " ++ item.score ++ ")");
      }
    }

    -- TODO: text_node redundant
    text_node charset : base_element.new(META, CHARSET, resource_util.UTF_8 !> base_string,
        missing.instance);
    referrer : make_element(META, NAME, "referrer", CONTENT, "no-referrer", missing.instance);
    title : base_element.new(TITLE, PROGRAM_NAME ++ " " ++ the_date.dotted() !> base_string);
    link : text_utilities.make_css_link("news-not-paper.css");
    text_node head : text_utilities.make_element(HEAD, [ charset, referrer, title, link ]);

    body_content.append(base_element.new(HR));
    body_content.append(text_utilities.make_html_link("a hack by dynin labs" !> base_string,
        "https://dynin.com" !> base_string));
    body : base_element.new(BODY, text_utilities.join(body_content));

    -- TODO: text_library redundant
    return text_utilities.make_element(text_library.HTML, [ head, body ]);
  }

  string day_page_url(date the_date) {
    return the_date ++ base_extension.HTML;
  }

  text_element render_html(item the_item) {
    item_fragments : base_list[text_fragment].new();
    item_fragments.append(base_element.new(SPAN, CLASS, SCORE_CLASS !> base_string,
        the_item.score.to_string !> base_string));
    item_fragments.append(" " !> base_string);
    item_fragments.append(make_html_class_link(the_item.title !> base_string,
        the_item.url.to_string, TITLE_CLASS));
    item_fragments.append(" " !> base_string);
    item_fragments.append(base_element.new(SPAN, CLASS, ORIGIN_CLASS !> base_string,
        "/ " ++ hacker_news.short_origin(the_item) !> base_string));
    item_fragments.append(" " !> base_string);
    item_fragments.append(make_html_class_link("/ " ++ the_item.descendants !> base_string,
        hacker_news.item_page_url(the_item.id).to_string, DISCUSSION_CLASS));
    item_fragments.append(" " !> base_string);
    return base_element.new(DIV, text_utilities.join(item_fragments));
  }

  text_fragment make_header(date the_date) {
    header_fragments : base_list[text_fragment].new();
    header_fragments.append(PROGRAM_NAME !> base_string);
    header_fragments.append(NBSP);
    header_fragments.append(NBSP);
    if (the_date != first) {
      header_fragments.append(text_utilities.make_html_link(LARR,
        day_page_url(the_date.prev()) !> base_string));
      header_fragments.append(" " !> base_string);
    }
    header_fragments.append(the_date.dotted() !> base_string);
    if (the_date != last) {
      header_fragments.append(" " !> base_string);
      header_fragments.append(text_utilities.make_html_link(RARR,
        day_page_url(the_date.next()) !> base_string));
    }
    return base_element.new(DIV, CLASS, HEADER_CLASS !> base_string,
        text_utilities.join(header_fragments));
  }

  text_element make_html_class_link(text_fragment text, string link_target, string class_name) {
    return make_element(A, HREF, link_target, CLASS, class_name, text);
  }

  text_element make_element(element_id id, attribute_id attr0, string value0,
      attribute_id attr1, string value1, text_fragment or null children) {
    attributes : list_dictionary[attribute_id, attribute_fragment].new();
    attributes.put(attr0, value0 !> base_string);
    attributes.put(attr1, value1 !> base_string);
    return base_element.new(id, attributes, children);
  }
}
