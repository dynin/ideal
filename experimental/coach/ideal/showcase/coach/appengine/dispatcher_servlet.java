/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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

import javax.servlet.http.*;

/**
 * The framework for an ideal framework forms-based application.
 */
public class dispatcher_servlet extends base_servlet {

  static {
    // This is a workaround of a Java design decision that disables assertions by default.
    // And no, "java -ea" in dev_appserver.sh doesn't help.
    dispatcher_servlet.class.getClassLoader().setDefaultAssertionStatus(true);
  }

  private final Object MONITOR = new byte[0];
  private @Nullable base_user_state active_user_state;

  @Override
  public text_content do_service(HttpServletRequest request, string email, string logout_url,
      boolean is_admin) {

    fluid_context request_context = new fluid_context(request, email, is_admin, logout_url);

    // For now, we serialize requests.
    synchronized (MONITOR) {
      user_state context = get_user_state(request_context.get_user());
      context.clear_world_cache();

      base_handler handler = new request_handler(context, request_context);

      procedure0arg action = handler.get_action();

      if (action == null) {
        action = request_handler.DEFAULT_PAGE;
      }

      while (true) {
        Object result = ((invokable_procedure0) action).invoke(handler);

        if (result instanceof text_content) {
          return (text_content) result;
        } else if (result instanceof widget) {
          return handler.widget_to_html((widget) result);
        } else if (result instanceof error_message) {
          return handler.show_error_message((error_message) result);
        } else if (result instanceof string) {
          return new text_content(resource_util.TEXT_PLAIN, (string) result);
        } else if (result instanceof String) {
          return new text_content(resource_util.TEXT_PLAIN, new base_string((String) result));
        } else if (result == null) {
          action = request_handler.DEFAULT_PAGE;
        } else {
          throw new RuntimeException("Uknown result " + result);
        }
      }
    }
  }

  protected user_state get_user_state(string human) {
    if (active_user_state == null || !utilities.eq(human, active_user_state.human)) {
      active_user_state = new base_user_state(human, this);
    }
    return active_user_state;
  }
}
