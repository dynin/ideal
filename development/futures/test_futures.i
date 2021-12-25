-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Tests associated with futures.
test_suite test_futures {

  test_case test_simple_futures() {
    future0 : base_future[string].new("foo");
    assert future0.value == "foo";

    future1 : base_future[string].new();
    assert future1.value is null;
    future1.set("bar");
    assert future1.value is_not null;
    assert future1.value == "bar";
  }

  static var nonnegative count0 : 0;
  static var nonnegative count1 : 0;

  static observe0() {
    count0 += 1;
  }

  static observe1() {
    count1 += 1;
  }

  test_case test_future_observers() {
    lifespan the_lifespan : base_lifespan.new(missing.instance);

    op0 : base_operation.new(observe0, "observe0");
    op1 : base_operation.new(observe1, "observe1");

    future0 : base_future[string].new("foo");
    event_queue.process();
    assert count0 == 0;
    future0.observe(op0, the_lifespan);
    event_queue.process();
    assert count0 == 0;

    future1 : base_future[string].new();
    future1.observe(op1, the_lifespan);
    event_queue.process();
    assert count1 == 0;
    future1.set("bar");
    event_queue.process();
    assert count1 == 1;

    short_lifespan : the_lifespan.make_sub_span();
    future2 : base_future[string].new();
    future2.observe(op1, short_lifespan);
    event_queue.process();
    assert count1 == 1;
    short_lifespan.dispose();
    future2.set("baz");
    event_queue.process();
    assert count1 == 1;
    assert future2.value == "baz";
  }
}
