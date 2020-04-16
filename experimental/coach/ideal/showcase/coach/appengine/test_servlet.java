/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.appengine;

import java.io.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A servlet that tests relection API.
 */
public class test_servlet extends HttpServlet {

  private static Method test = findMethod(InnerClass.class, "testMethod");

  private static Method findMethod(Class theClass, String name) {
    Method[] methods = theClass.getDeclaredMethods();
    for (Method method : theClass.getDeclaredMethods()) {
      if (method.getName().equals(name)) {
        return method;
      }
    }
    throw new RuntimeException("Method " + name + " not found in " + theClass);
  }

  @Override
  public void service(HttpServletRequest request, HttpServletResponse response) {
    try {
      InnerClass instance = new InnerClass();
      // String result = (String) test.invoke(instance);
      String result = (String) instance.invoke(test);
      response.getWriter().write(result);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static class InnerClass {
    public Object invoke(Method method) throws Exception {
      return method.invoke(this);
    }

    public String testMethod() {
      return "Ok.\n";
    }
  }
}
