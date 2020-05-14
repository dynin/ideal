/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.machine.elements;

import ideal.library.elements.*;

import java.lang.reflect.Array;
import java.util.Arrays;

public class array<T> {
  private final T[] the_elements;
  public final int size;

  @SuppressWarnings("unchecked")
  public array(Class<T> the_type, int size) {
    the_elements = (T[]) Array.newInstance(the_type, size);
    this.size = size;
  }

  @SuppressWarnings("unchecked")
  public array(int size) {
    the_elements = (T[]) new Object[size];
    this.size = size;
  }

  // Important: this doesn't create a copy of an array!
  @SuppressWarnings("unchecked")
  public array(T[] the_elements) {
    this.the_elements = the_elements;
    this.size = the_elements.length;
  }

  @SuppressWarnings("unchecked")
  public T get(int index) {
    return the_elements[index];
  }

  public reference<T> at(final int index) {
    return new reference<T>() {
      @Override
      public T get() {
        return the_elements[index];
      }

      @Override
      public void set(T new_value) {
        the_elements[index] = new_value;
      }
    };
  }

  public void set(int index, T value) {
    the_elements[index] = value;
  }

  public void move(int source, int destination, int length) {
    System.arraycopy(the_elements, source, the_elements, destination, length);
  }

  public void copy(int source_begin, array destination, int destination_begin, int length) {
    Object[] destination_elements = destination.the_elements;
    System.arraycopy(the_elements, source_begin, destination_elements, destination_begin, length);
  }

  public void scrub(int index, int length) {
    Arrays.fill(the_elements, index, index + length, null);
  }
}
