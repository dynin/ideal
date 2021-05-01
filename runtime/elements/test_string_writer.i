-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

import ideal.machine.channels.string_writer;

class test_string_writer {

  test_case basic_test() {
    the_writer : string_writer.new();

    assert the_writer.size() == 0;
    assert the_writer.elements() == "";

    the_writer.write_all("foo");
    assert the_writer.size() == 3;
    assert the_writer.elements() == "foo";

    the_writer.write('b');
    the_writer.write('a');
    the_writer.write('r');
    assert the_writer.size() == 6;
    assert the_writer.elements() == "foobar";

    elements : the_writer.elements();
    the_writer.clear();
    please assert elements == "foobar";
    please assert the_writer.size() == 0;
    please assert the_writer.elements() == "";
  }
}
