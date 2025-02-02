/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.appengine;

import ideal.library.elements.*;
import ideal.library.resources.*;
import javax.annotation.Nullable;
import ideal.library.texts.*;
import ideal.runtime.elements.*;
import ideal.runtime.resources.*;
import ideal.runtime.channels.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.development.scanners.*;
import ideal.development.origins.*;
import ideal.development.tools.*;
import ideal.showcase.coach.reflections.*;
import ideal.showcase.coach.webforms.*;
import ideal.showcase.coach.common.*;

import java.io.*;
import java.net.*;
import javax.servlet.*;
import javax.servlet.http.*;
import com.google.appengine.api.users.*;

/**
 * The framework for an ideal framework application hosted on AppEngine.
 */
public abstract class base_servlet extends HttpServlet {

  private static final string IDEAL_SOURCE_DIR = new base_string("isource");

  private static final name APP_NAME_PARAM = new name("app_name");
  private static final name RESTRICT_URI_PARAM = new name("restrict_uri");
  private static final name DATASTORE_NAME_PARAM = new name("datastore_name");
  private static final name ADMINS_PARAM = new name("admins");
  private static final name USERS_PARAM = new name("users");

  private static final String EMAIL_SEPARATOR_REGEX = ",";

  private static final string JAVASCRIPT_RUNTIME = new base_string("runtime");

  private resource_catalog resources;
  private translator the_translator;
  public source_content runtime_js;
  datastore_schema bundled_world;

  protected string app_name;
  private string datastore_name;
  private @Nullable string restrict_uri;
  private set<string> admins_whitelist;
  private set<string> users_whitelist;

  @Override
  public void init(ServletConfig servlet_config) throws ServletException {
    config the_config = new config(servlet_config);

    app_name = the_config.get(APP_NAME_PARAM);
    assert app_name != null;

    datastore_name = the_config.get(DATASTORE_NAME_PARAM);
    if (datastore_name == null) {
      datastore_name = new base_string(utilities.s(app_name).toLowerCase());
    }

    restrict_uri = the_config.get(RESTRICT_URI_PARAM);

    admins_whitelist = parse_email_list(the_config.get(ADMINS_PARAM));
    users_whitelist = parse_email_list(the_config.get(USERS_PARAM));
    users_whitelist.add_all(admins_whitelist);

    resource_catalog resources = new servlet_resources(servlet_config.getServletContext()).top();
    resources = resources.resolve(IDEAL_SOURCE_DIR).access_catalog();

    the_translator = new translator(resources);

    source_content source = load_source(get_source_name(), base_extension.IDEAL_SOURCE);
    translation_result translated = translate_source(source, app_name, null);
    if (!translated.is_success()) {
      utilities.panic("Translation error " + translated.get_error_messages());
    }
    bundled_world = translated.get_new_declaration();

    runtime_js = load_source(JAVASCRIPT_RUNTIME, base_extension.JAVASCRIPT_SOURCE);
  }

  private static class config {
    private ServletConfig the_servlet_config;

    public config(ServletConfig the_servlet_config) {
      this.the_servlet_config = the_servlet_config;
    }

    public @Nullable string get(identifier parameter) {
      String result = the_servlet_config.getInitParameter(utilities.s(parameter.to_string()));
      if (result != null) {
        return new base_string(result);
      } else {
        return null;
      }
    }
  }

  private create_manager creator() {
    return the_translator.creator;
  }

  private string get_source_name() {
    return new base_string(utilities.s(app_name).toLowerCase());
  }

  protected boolean persistence_enabled() {
    return true;
  }

  protected string get_datastore_name() {
    return datastore_name;
  }

  private source_content load_source(string filename, extension ext) {
    return new source_content(creator().top_catalog().resolve(filename, ext));
  }

  private static set<string> parse_email_list(string param) {
    set<string> result = new hash_set<string>();
    for (String email : utilities.s(param).split(EMAIL_SEPARATOR_REGEX)) {
      String trimmed = email.trim();
      if (!trimmed.isEmpty()) {
        result.add(new base_string(trimmed));
      }
    }
    return result;
  }

  public translation_result translate_source(source_content source, string name,
      @Nullable string version_suffix) {
    return the_translator.translate_source(source, name, version_suffix);
  }

  @Override
  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String request_uri = request.getRequestURI();

    if (restrict_uri != null && !utilities.eq(new base_string(request_uri), restrict_uri)) {
      response.sendError(HttpServletResponse.SC_NOT_FOUND);
      return;
    }

    UserService userService = UserServiceFactory.getUserService();
    User user = userService.getCurrentUser();

    string email;
    string logout_url;

    if (user != null) {
      email = new base_string(user.getEmail());
      logout_url = new base_string(userService.createLogoutURL(request_uri));
    } else {
      response.sendRedirect(userService.createLoginURL(request_uri));
      return;
    }

    if (!users_whitelist.contains(email)) {
      response.sendError(HttpServletResponse.SC_FORBIDDEN,
        view_renderer.escape_html(email) + ", I don't recognize you.<br />" +
        "<a href=\"" + view_renderer.escape_html(logout_url) + "\">Logout.</a>");
      return;
    }

    boolean is_admin = admins_whitelist.contains(email);

    text_content text_response = do_service(request, email, logout_url, is_admin);

    response.setContentType(text_response.to_content_type());
    response.getWriter().write(utilities.s(text_response.content));
  }

  public abstract text_content do_service(HttpServletRequest request, string email,
      string logout_url, boolean is_admin);
}
