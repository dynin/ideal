/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.showcase.coach.forms;

import ideal.library.elements.*;
import ideal.library.reflections.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.showcase.coach.common.sorter;
public class select_input<T extends value_wrapper> implements widget {

  // TODO: replace with a function[String, T]
  public interface option_displayer<T> {
    String display(T value);
  }

  public static class option<T extends value_wrapper> implements Comparable<option<T>> {

    public final String name;
    public final T value;

    public option(String name, T value) {
      this.name = name;
      this.value = value;
    }

    @Override
    public int compareTo(option other) {
      return this.name.compareToIgnoreCase(other.name);
    }
  }

  public final reference_wrapper<T> model;
  public final readonly_list<option<T>> the_options;

  public select_input(reference_wrapper<T> model, readonly_list<T> options,
      option_displayer<T> displayer, boolean sort) {
    this.model = model;

    list<option<T>> tmp_options = new base_list<option<T>>();
    for (int i = 0; i < options.size(); ++i) {
      T value = options.get(i);
      tmp_options.append(new option(displayer.display(value), value));
    }
    if (sort) {
      // TODO: use comparator.
      //the_options = sorter.sort(tmp_options);
      the_options = tmp_options;
    } else {
      the_options = tmp_options;
    }
  }

  @Override
  public <R> R accept(widget_visitor<R> the_visitor) {
    return the_visitor.visit_select_input(this);
  }
}
