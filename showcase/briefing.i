-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

program briefing {
  implicit import ideal.library.resources;
  implicit import ideal.library.formats;
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
    string url;
    nonnegative score;
    string title;
    nonnegative descendants;

    boolean has_url => url.is_not_empty;

    override string to_string => "item:" ++ id ++ ";" ++ url;
  }

  --- The serializers will be generated.
  namespace serializer {
    item_id read_item_id(the readonly json_data) {
      item_id.new(the_json_data !> nonnegative);
    }

    readonly json_data write_item_id(the readonly item_id) {
      return the_item_id.id;
    }

    readonly list[item_id] read_item_id_list(the readonly json_data) {
      the json_array : the_json_data !> json_array;
      result : base_list[item_id].new();
      for (number_id : the_json_array) {
        result.append(read_item_id(number_id));
      }
      return result;
    }

    json_data write_item_id_list(readonly list[item_id] item_ids) {
      result : json_array_impl.new();
      for (the_id : item_ids) {
        result.append(write_item_id(the_id));
      }
      return result;
    }
  }

  private class item_order {
    implements order[item];

    implement implicit sign call(item first, item second) {
      var sign result : second.score <=> first.score;
      if (result == sign.equal) {
        result = second.descendants <=> first.descendants;
      }
      return result;
    }
  }

  -- Hacker News API : https://github.com/HackerNews/API
  namespace hacker_news {
    hacker_news_host : "news.ycombinator.com";
    api_url_prefix : "https://hacker-news.firebaseio.com/v0/";
    item_page_url_prefix : "https://" ++ hacker_news_host ++ "/item?id=";
    -- Minimum host length for which the www stripping is performed.
    www_stripping_threshold : 7;
    www_prefix_pattern : list_pattern[character].new("www.");
    slash_pattern : singleton_pattern[character].new('/');

    readonly list[item_id] topstories() {
      topstories_url : network.url(api_url_prefix ++ "topstories" ++ base_extension.JSON);
      topstories_content : topstories_url.access_json_data(missing.instance).content;
      return serializer.read_item_id_list(topstories_content);
    }

    readonly item get_item(the item_id) {
      item_url : network.url(api_url_prefix ++ "item/" ++ the_item_id.id ++ base_extension.JSON);
      content : item_url.access_json_data(missing.instance).content;
      return parse_item(the_item_id, content !> readonly json_object);
    }

    string item_page_url(the item_id) {
      return item_page_url_prefix ++ the_item_id.id;
    }

    item parse_item(item_id id, readonly json_object item_object) {
      by : item_object.get("by") !> string;
      var string url;
      if (item_object.contains_key("url")) {
        url = item_object.get("url") !> string;
      } else {
        url = "";
      }
      var nonnegative score;
      if (item_object.contains_key("score")) {
        score = item_object.get("score") !> nonnegative;
      } else {
        score = 0;
      }
      title : item_object.get("title") !> string;
      var nonnegative descendants;
      if (item_object.contains_key("descendants")) {
        descendants = item_object.get("descendants") !> nonnegative;
      } else {
        descendants = 0;
      }

      return item.new(id, by, url, score, title, descendants);
    }

    string short_origin(the item) {
      if (!the_item.has_url()) {
        return hacker_news_host;
      }
      -- TODO: handle URL parsing more robustly
      url : network.url(the_item.url);
      host : url.host;
      assert host is string;

      if (host.size > www_stripping_threshold) {
        www_prefix : www_prefix_pattern.match_prefix(host);
        if (www_prefix is_not null) {
          return host.skip(www_prefix);
        }
      }

      if (host == "twitter.com" || host == "github.com") {
        -- TODO: instead of this hack, expose path in resource_identifier.
        url_string : url.to_string;
        prefix_size : "https://".size + host.size;
        segment : slash_pattern.find_first(url_string, prefix_size + 1);
        if (segment is range) {
          return host ++ url_string.slice(prefix_size, segment.end - 1 !> nonnegative);
        }
      }

      return host;
    }
  }

  PROGRAM_NAME : "news, !paper";
  TEST_RUN : false;
  DEPLOY_MODE : true;
  MIN_SCORE_THRESHOLD : 100;
  HOUR_DAY_STARTS : 6;
  DAYS_STORIES_EXPIRE : 5;

  INDEX_HTML : "index" ++ base_extension.HTML;
  ALL_ITEMS_JSON : "all-items" ++ base_extension.JSON;

  HEADER_CLASS : "header";
  TOP_CLASS : "top";
  TITLE_CLASS : "title";
  ORIGIN_CLASS : "origin";
  SCORE_CLASS : "score";
  DISCUSSION_CLASS : "discussion";

  briefing_catalog : filesystem.CURRENT_CATALOG.resolve("../briefing").access_catalog();
  var resource_catalog input_catalog;
  var resource_catalog output_catalog;
  hour : hour_now();
  minute : minute_now();
  var first : day_of(2021, SEPTEMBER, 24);
  var last : today();

  void start() {
    input_catalog = briefing_catalog.resolve("input").access_catalog();
    output_catalog = briefing_catalog.resolve("output").access_catalog();

    if (TEST_RUN) {
      test_run();
      return;
    }

    first = last.add_days(-3);

    if (hour < HOUR_DAY_STARTS) {
      last = last.add_days(-1);
    }

    save_top();

    all_item_ids : hash_set[item_id].new();
    previous_ids : read_ids(previous(first));
    if (previous_ids is_not null) {
      all_item_ids.add_all(previous_ids);
    }
    var day : first;
    loop {
      day_item_ids : hash_set[item_id].new();
      id_set : read_ids(day);
      if (id_set is null) {
        if (day == last) {
          break;
        } else {
          day = next(day);
          continue;
        }
      }
      for (id : id_set.elements) {
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
      expired_ids : read_ids(day.add_days(-DAYS_STORIES_EXPIRE));
      if (expired_ids is_not null) {
        for (expired_id : expired_ids.elements) {
          all_item_ids.remove(expired_id);
        }
      }
      write_all_items(all_item_ids, day);
      day = next(day);
    }
  }

  private void test_run() {
    log.info("Now: " ++ two_digit(hour) ++ ":" ++ two_digit(minute));
    topstories : hacker_news.topstories();
    log.info("Got count " ++ topstories.size);
    write_page(topstories.slice(0, 50), last);
  }

  private void save_top() {
    top_stories : hacker_news.topstories();
    day_catalog : input_catalog.resolve(day_slashes(today())).access_catalog();
    time_id : day_catalog.resolve(two_digit(hour) ++ "-" ++ two_digit(minute));
    log.info("Writing " ++ time_id);
    time_id.access_json_data(make_catalog_option.instance).content =
        serializer.write_item_id_list(top_stories);
  }

  string describe_day(gregorian_day day, character separator) {
    return day.year ++ separator ++ two_digit(day.month.index_base_1) ++ separator ++
        two_digit(day.day);
  }

  string day_slashes(gregorian_day day) => describe_day(day, '/');
  string day_dashes(gregorian_day day) => describe_day(day, '-');

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
    log.info("Writing " ++ file);
    file.access_string(make_catalog_option.instance).content =
        text_utilities.to_markup_string(page);
  }

  readonly list[item_id] read_id_list(resource_identifier list_resource) {
    content : list_resource.access_json_data(missing.instance).content;
    return serializer.read_item_id_list(content);
  }

  readonly set[item_id] or null read_ids(gregorian_day day) {
    day_catalog : input_catalog.resolve(day_slashes(day)).access_catalog();
    result : hash_set[item_id].new();

    all_items_json : day_catalog.resolve(ALL_ITEMS_JSON);
    if (all_items_json.exists) {
      log.info("Reading " ++ all_items_json);
      all_items : read_id_list(all_items_json);
      result.add_all(all_items);
    } else {
      content : day_catalog.content;
      if (content is null) {
        log.info("Can't access " ++ day_catalog);
        return missing.instance;
      }

      files : content.elements;
      for (file : files) {
        if (file.key != ALL_ITEMS_JSON) {
          log.info("Reading " ++ file.value);
          file_content : read_id_list(file.value);
          result.add_all(file_content);
        } else {
          log.info("Skipping " ++ file.value);
        }
      }
    }

    log.info("Got " ++ result.size ++ " ids.");
    return result;
  }

  void write_all_items(readonly set[item_id] ids, gregorian_day day) {
    day_catalog : input_catalog.resolve(day_slashes(day)).access_catalog();
    all_items_json : day_catalog.resolve(ALL_ITEMS_JSON).access_json_data(
        make_catalog_option.instance);
    log.info("Writing " ++ all_items_json);
    all_items_json.content = serializer.write_item_id_list(ids.elements);
  }

  text_element render_page(readonly list[item_id] ids, gregorian_day day) {
    body_content : base_list[text_fragment].new();
    body_content.append(make_header(day));
    body_content.append(base_element.new(HR));

    items : base_list[item].new();

    for (item_id : ids) {
      item : hacker_news.get_item(item_id);
      if (item.score >= MIN_SCORE_THRESHOLD) {
        items.append(item);
        log.info(item.title ++ " " ++ hacker_news.short_origin(item) ++
            " (" ++ item.by ++ ", " ++ item.score ++ ")");
      }
    }

    items.sort(item_order.new());

    for (item : items) {
      body_content.append(render_html(item));
    }

    -- TODO: text_node redundant
    text_node charset : base_element.new(META, CHARSET, resource_util.UTF_8, missing.instance);
    referrer : make_element(META, NAME, "referrer", CONTENT, "origin", missing.instance);
    title : base_element.new(TITLE, PROGRAM_NAME ++ " " ++ day_dashes(day));
    link : text_utilities.make_css_link(top_prefix("news-not-paper.css", day));
    text_node head : text_utilities.make_element(HEAD, [ charset, referrer, title, link ]);

    body_content.append(base_element.new(HR));
    body_content.append(text_utilities.make_html_link("a hack", about_page_url(day)));
    body_content.append(" ");
    body_content.append(text_utilities.make_html_link("by dynin labs", "https://dynin.com"));
    body : base_element.new(BODY, text_utilities.join(body_content));

    -- TODO: text_library redundant
    return text_utilities.make_element(text_library.HTML, [ head, body ]);
  }

  string day_page_file(gregorian_day day) {
    if (day == last) {
      return INDEX_HTML;
    } else {
      return day_slashes(day) ++ resource_util.PATH_SEPARATOR ++ INDEX_HTML;
    }
  }

  string top_prefix(string filename, gregorian_day current) {
    if (current == last) {
      return filename;
    } else {
      return "../../../" ++ filename;
    }
  }

  string day_page_url(gregorian_day day, gregorian_day current) {
    if (DEPLOY_MODE) {
      if (day == last) {
        return resource_util.PATH_SEPARATOR;
      } else {
        return resource_util.PATH_SEPARATOR ++ day_slashes(day) ++ resource_util.PATH_SEPARATOR;
      }
    } else {
      return top_prefix(day_page_file(day), current);
    }
  }

  string about_page_url(gregorian_day current) {
    if (DEPLOY_MODE) {
      return "/about/";
    } else {
      return top_prefix("about.html", current);
    }
  }

  text_element render_html(the item) {
    item_page : hacker_news.item_page_url(the_item.id);
    url : the_item.has_url() ? the_item.url : item_page;
    item_fragments : base_list[text_fragment].new();
    item_fragments.append(base_element.new(SPAN, CLASS, SCORE_CLASS, the_item.score.to_string));
    item_fragments.append(" ");
    item_fragments.append(make_html_class_link(the_item.title, url, TITLE_CLASS));
    item_fragments.append(" ");
    item_fragments.append(base_element.new(SPAN, CLASS, ORIGIN_CLASS,
        "/ " ++ hacker_news.short_origin(the_item)));
    item_fragments.append(" ");
    item_fragments.append(make_html_class_link("/ " ++ the_item.descendants, item_page,
        DISCUSSION_CLASS));
    item_fragments.append(" ");
    return base_element.new(DIV, text_utilities.join(item_fragments));
  }

  text_fragment make_header(gregorian_day day) {
    header_fragments : base_list[text_fragment].new();
    header_fragments.append(base_element.new(SPAN, CLASS, TOP_CLASS, PROGRAM_NAME));
    header_fragments.append(" / ");
    header_fragments.append(text_utilities.make_html_link("hn", "https://news.ycombinator.com"));
    header_fragments.append(" digest");
    header_fragments.append(NBSP);
    header_fragments.append(NBSP);
    if (day != first || output_catalog.resolve(day_page_file(previous(day))).exists) {
      header_fragments.append(text_utilities.make_html_link(LARR,
          day_page_url(previous(day), day)));
      header_fragments.append(" ");
    }
    header_fragments.append(day_dashes(day));
    if (day != last) {
      header_fragments.append(" ");
      header_fragments.append(text_utilities.make_html_link(RARR, day_page_url(next(day), day)));
    }
    return base_element.new(DIV, CLASS, HEADER_CLASS, text_utilities.join(header_fragments));
  }

  text_element make_html_class_link(text_fragment text, string link_target, string class_name) {
    return make_element(A, HREF, link_target, CLASS, class_name, text);
  }

  text_element make_element(element_id id, attribute_id attr0, string value0,
      attribute_id attr1, string value1, text_fragment or null children) {
    attributes : list_dictionary[attribute_id, attribute_fragment].new();
    attributes.put(attr0, value0);
    attributes.put(attr1, value1);
    return base_element.new(id, attributes, children);
  }
}
