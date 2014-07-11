/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.appengine;

import ideal.library.elements.*;
import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;
import ideal.runtime.channels.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.values.*;
import ideal.showcase.coach.reflections.*;
import ideal.showcase.coach.forms.*;
import ideal.showcase.coach.webforms.*;
import ideal.showcase.coach.common.*;
import ideal.showcase.coach.common.procedure1;

import java.io.*;
import java.net.*;
import java.util.Map;
import javax.servlet.http.*;
public class fluid_context implements render_context, parameter_context {

  private static final name ACTION = new name("action");
  private static final name IMPERSONATE = new name("impersonate");

  private static final String ACTION_PREFIX = "!/";

  private final string human;
  public final boolean admin_mode;
  public final string request_uri;
  public final string logout_url;
  public @Nullable string impersonate;
  public @Nullable immutable_list<string> action;

  private final dictionary<string, string> parameters;

  public fluid_context(HttpServletRequest request, string human, boolean admin_mode,
      string logout_url) {
    this.human = human;
    this.admin_mode = admin_mode;
    this.request_uri = new base_string(request.getRequestURI());
    this.logout_url = logout_url;

    parameters = new hash_dictionary<string, string>();
    @SuppressWarnings("unchecked")
    Map<String, String[]> request_param_map = request.getParameterMap();

    for (Map.Entry<String, String[]> param : request_param_map.entrySet()) {
      String key = param.getKey();
      if (key.startsWith(ACTION_PREFIX)) {
        set_action(key.substring(ACTION_PREFIX.length()));
      } else {
        String value = param.getValue()[0];
        if (utilities.eq(new base_string(key), ACTION.to_string())) {
          set_action(value);
        } else {
          parameters.put(new base_string(key), new base_string(value));
        }
      }
    }

    if (admin_mode) {
      impersonate = get(IMPERSONATE);
    }
  }

  private void set_action(String action_value) {
    assert action == null;
    String[] action_parsed = action_value.split(procedure1.FIELD_SEPARATOR);
    list<string> result = new base_list<string>();
    for (int i = 0; i < action_parsed.length; ++i) {
      result.append(new base_string(action_parsed[i]));
    }
    action = result.frozen_copy();
  }

  public string get_user() {
    return impersonate != null ? impersonate : human;
  }

  @Override
  public @Nullable string get(identifier id) {
    return parameters.get(id.to_string());
  }

  public string base_uri() {
    if (impersonate != null) {
      return append_param(request_uri, IMPERSONATE, impersonate);
    } else {
      return request_uri;
    }
  }

  @Override
  public string to_uri(procedure0 action) {
    return append_param(base_uri(), ACTION, action.to_string());
  }

  @Override
  public string to_button_id(procedure0 action) {
    return new base_string(ACTION_PREFIX, action.to_string());
  }

  private static string append_param(string uri, identifier id, string value) {
    String the_uri = utilities.s(uri);
    StringBuilder sb = new StringBuilder(the_uri);

    if (the_uri.indexOf('?') < 0) {
      sb.append('?');
    } else {
      sb.append('&');
    }
    sb.append(url_encode(utilities.s(id.to_string())));
    sb.append('=');
    sb.append(url_encode(utilities.s(value)));

    return new base_string(sb.toString());
  }

  private static String url_encode(String s) {
    try {
      return URLEncoder.encode(s, utilities.s(resource_util.UTF_8));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }
}
