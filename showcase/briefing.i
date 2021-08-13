-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

program briefing {
  implicit import ideal.library.resources;
  implicit import ideal.library.texts;
  implicit import ideal.library.calendars;
  implicit import ideal.runtime.elements;
  implicit import ideal.runtime.resources;
  implicit import ideal.runtime.texts;
  implicit import ideal.runtime.texts.text_library;
  implicit import ideal.runtime.patterns;
  implicit import ideal.runtime.logs;
  implicit import ideal.runtime.formats;
  implicit import ideal.runtime.calendars;
  implicit import ideal.machine.resources;
  implicit import ideal.machine.characters;
  implicit import ideal.machine.resources;
  implicit import ideal.machine.calendars.calendar_utilities;

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
      var nonnegative score;
      if (item_dictionary.contains_key("score")) {
        score = item_dictionary.get("score") !> nonnegative;
      } else {
        score = 0;
      }
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
  TEST_RUN : false;
  MIN_SCORE_THRESHOLD : 100;

  INDEX_HTML : "index" ++ base_extension.HTML;

  HEADER_CLASS : "header";
  TITLE_CLASS : "title";
  ORIGIN_CLASS : "origin";
  BY_CLASS : "by";
  SCORE_CLASS : "score";
  DISCUSSION_CLASS : "discussion";

  briefing_catalog : filesystem.CURRENT_CATALOG.resolve("../briefing").access_catalog();
  var resource_catalog input_catalog;
  var resource_catalog output_catalog;
  --first : day_of(2021, JULY, 6);
  first : day_of(2021, AUGUST, 10);
  --last : today();
  last : day_of(2021, AUGUST, 12);

  void start() {
    input_catalog = briefing_catalog.resolve("input").access_catalog();
    output_catalog = briefing_catalog.resolve("output").access_catalog();

    if (TEST_RUN) {
      id_list : hacker_news.topstories();
      log.info("Got count " ++ id_list.size);
      write_page(id_list.slice(0, 50), last);
    } else {
      all_item_ids : hash_set[item_id].new();
      var day : first;
      loop {
        day_item_ids : hash_set[item_id].new();
        for (id : read_ids(day).elements) {
          if (!all_item_ids.contains(id)) {
            day_item_ids.add(id);
            all_item_ids.add(id);
          }
        }
        log.info("Got " ++ day_item_ids.size ++ " items for " ++ day);
        write_page(day_item_ids.elements, day);
        if (day == last) {
          break;
        }
        day = next(day);
      }
    }
  }

  string describe_day(gregorian_day day, character separator) {
    return day.year ++ separator ++ two_digit(day.month.index_base_1) ++ separator ++
        two_digit(day.day);
  }

  string day_slashes(gregorian_day day) => describe_day(day, '/');
  string day_dots(gregorian_day day) => describe_day(day, '.');

  string two_digit(nonnegative number) {
    if (number < 10) {
      return "0" ++ number;
    } else {
      return number.to_string;
    }
  }

  gregorian_day next(gregorian_day day) => day.add_days(1);
  gregorian_day previous(gregorian_day day) => day.add_days(-1);

  void write_page(readonly list[item_id] ids, gregorian_day day) {
    page : render_page(ids, day);
    file : output_catalog.resolve(day_page_file(day));
    log.info("=== " ++ file);
    file.access_string(make_catalog_option.instance).content =
        text_utilities.to_markup_string(page);
  }

  set[item_id] read_ids(gregorian_day day) {
    day_catalog : input_catalog.resolve(day_slashes(day)).access_catalog();
    result : hash_set[item_id].new();

    content : day_catalog.content;
    assert content is_not null;
    files : content.values().elements;
    for (file : files) {
      log.info("=== " ++ file);
      file_content : hacker_news.id_list(file);
      result.add_all(file_content);
    }

    log.info("Got " ++ result.size ++ " ids.");
    return result;
  }


  text_element render_page(readonly list[item_id] ids, gregorian_day day) {
    body_content : base_list[text_fragment].new();
    body_content.append(make_header(day));
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
    title : base_element.new(TITLE, PROGRAM_NAME ++ " " ++ day_dots(day) !> base_string);
    link : text_utilities.make_css_link(top_prefix("news-not-paper.css"));
    text_node head : text_utilities.make_element(HEAD, [ charset, referrer, title, link ]);

    body_content.append(base_element.new(HR));
    body_content.append(text_utilities.make_html_link("a hack by dynin labs" !> base_string,
        "https://dynin.com" !> base_string));
    body : base_element.new(BODY, text_utilities.join(body_content));

    -- TODO: text_library redundant
    return text_utilities.make_element(text_library.HTML, [ head, body ]);
  }

  string day_page_file(gregorian_day day) {
    return day_slashes(day) ++ resource_util.PATH_SEPARATOR ++ INDEX_HTML;
  }

  string top_prefix(string filename) {
    return "../../../" ++ filename;
  }

  string day_page_url(gregorian_day day) {
    return top_prefix(day_page_file(day));
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

  text_fragment make_header(gregorian_day day) {
    header_fragments : base_list[text_fragment].new();
    header_fragments.append(text_utilities.make_html_link("hacker news" !> base_string,
        "https://news.ycombinator.com" !> base_string));
    header_fragments.append(" digest" !> base_string);
    header_fragments.append(NBSP);
    header_fragments.append(NBSP);
    if (day != first) {
      header_fragments.append(text_utilities.make_html_link(LARR,
          day_page_url(previous(day)) !> base_string));
      header_fragments.append(" " !> base_string);
    }
    header_fragments.append(day_dots(day) !> base_string);
    if (day != last) {
      header_fragments.append(" " !> base_string);
      header_fragments.append(text_utilities.make_html_link(RARR,
        day_page_url(next(day)) !> base_string));
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
