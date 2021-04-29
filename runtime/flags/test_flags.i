-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

class test_flags {

  meta_flags class demo_flags {
    boolean ARG_BOOL;
    string or null ARG_STRING;
  }

  testcase test_flag_parse() {
    the_demo_flags : demo_flags.new([ "-arg-bool=true", "-arg-string=str" ], error_reporter);
    assert the_demo_flags.ARG_BOOL == true;
    assert the_demo_flags.ARG_STRING == "str";

    the_demo_flags2 : demo_flags.new([ "-noargbool", "-arg-string:bar" ], error_reporter);
    assert the_demo_flags2.ARG_BOOL == false;
    assert the_demo_flags2.ARG_STRING == "bar";
  }

  var string reported_message;

  private void error_reporter(string message) {
    reported_message = message;
  }

  testcase test_failed_parse() {
    the_demo_flags : demo_flags.new([ "-foo", ], error_reporter);
    assert reported_message == "Unknown flag: foo";

    the_demo_flags2 : demo_flags.new([ "-arg-bool", "bar" ], error_reporter);
    assert reported_message == "Non-flag parameters found--don't know what to do!";
  }
}
