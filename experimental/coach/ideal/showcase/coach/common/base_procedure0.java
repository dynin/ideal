/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.showcase.coach.common;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.showcase.coach.reflections.*;
import ideal.showcase.coach.forms.*;

import java.lang.reflect.*;
public class base_procedure0 extends name implements procedure_id, invokable_procedure0 {

  private Class this_class;
  private Method method;

  public base_procedure0(String s) {
    super(s);
  }

  @Override
  public void init(Class this_class) {
    Method method = find_method(this_class, this.to_string());
    assert method.getDeclaringClass() == this_class;
    assert method.getParameterTypes().length == 0;
    assert (method.getModifiers() & Modifier.STATIC) == 0;

    assert this.this_class == null && this.method == null;
    this.this_class = this_class;
    this.method = method;
  }

  @Override
  public Object invoke(Object the_object) {
    // TODO: precondition should be relaxed
    assert the_object.getClass() == this_class;
    try {
      return method.invoke(the_object);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  static Method find_method(Class the_class, string name) {
    Method[] methods = the_class.getDeclaredMethods();
    for (Method method : the_class.getDeclaredMethods()) {
      if (utilities.eq(new base_string(method.getName()), name)) {
        return method;
      }
    }
    throw new RuntimeException("Method " + name + " not found in " + the_class);
  }
}
