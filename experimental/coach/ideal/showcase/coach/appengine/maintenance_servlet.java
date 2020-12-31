/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.appengine;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A servlet that displays "service unavailable" message.
 */
public class maintenance_servlet extends HttpServlet {

  private String eta;
  private String admin;

  @Override
  public void init(ServletConfig servlet_config) {
    eta = servlet_config.getInitParameter("eta");
    admin = servlet_config.getInitParameter("admin");
  }

  @Override
  public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE,
      "Sorry, the service is down for maintenance.\n" +
      "Expected to be back up on " + eta + ".\n" +
      "If you have questions, please contact " + admin + ".\n");
  }
}
