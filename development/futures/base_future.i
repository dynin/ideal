-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- A concrete implementation of a |future|.
class base_future[covariant value element] {
  implements future[element];

  private var element or null the_value;
  private set[operation] observers : hash_set[operation].new();

  class dispose_observer[value element] {
    implements disposable;

    base_future[element] the_future;
    operation observer;

    -- TODO: generate ctor
    dispose_observer(base_future[element] the_future, operation observer) {
      this.the_future = the_future;
      this.observer = observer;
    }

    void dispose() {
      the_future.observers.remove(observer);
    }
  }

  overload base_future(element the_value) {
    this.the_value = the_value;
  }

  overload base_future() {
    -- TODO: use uninitalized singleton instead
    this.the_value = missing.instance;
  }

  implement element or null value => the_value;

  implement boolean is_done => the_value is_not null;

  void set(element the_value) {
    assert !is_done;
    this.the_value = the_value;

    if (observers.is_not_empty) {
      for (observer : observers.elements) {
        observer.schedule();
      }
    }
  }

  implement void observe(operation observer, lifespan the_lifespan) {
    if (is_done) {
      return;
    }

    observers.add(observer);
    the_lifespan.add_resource(dispose_observer[element].new(this, observer));
  }
}
