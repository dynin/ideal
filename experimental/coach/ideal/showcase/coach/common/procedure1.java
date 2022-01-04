/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
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
public abstract class procedure1<T> extends name implements procedure_id {

  // Note: we also use FIELD_SEPARATOR as a regexp, so it must not contain regexp special chars.
  public static final String FIELD_SEPARATOR = "/";

  private final Class arg_type;
  private Class this_class;
  private Method method;

  public procedure1(String s, Class arg_type) {
    super(s);
    this.arg_type = arg_type;
  }

  @Override
  public void init(Class this_class) {
    Method method = base_procedure0.find_method(this_class, this.to_string());
    assert method.getDeclaringClass() == this_class;
    assert method.getParameterTypes().length == 1;
    assert method.getParameterTypes()[0] == arg_type;
    assert (method.getModifiers() & Modifier.STATIC) == 0;

    assert this.this_class == null && this.method == null;
    this.this_class = this_class;
    this.method = method;
  }

  Method get_method() {
    assert method != null;
    return method;
  }

  Class get_this_class() {
    assert this_class != null;
    return this_class;
  }

  protected abstract string to_string(T arg);

  public procedure0arg bind(T arg) {
    return new bound_p0(this, arg, to_string(arg));
  }

  private static class bound_p0 extends debuggable implements invokable_procedure0 {
    private final procedure1 main;
    private final Object arg;
    private final string arg_value;

    public bound_p0(procedure1 main, Object arg, string arg_value) {
      this.main = main;
      this.arg = arg;
      this.arg_value = arg_value;
    }

    @Override
    public Object invoke(Object the_object) {
      // TODO: precondition should be relaxed
      assert the_object.getClass() == main.get_this_class();
      try {
        return main.get_method().invoke(the_object, arg);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    public string to_string() {
      return new base_string(main.toString(), FIELD_SEPARATOR, arg_value);
    }
  }
}
