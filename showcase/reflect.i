-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

-- Coach: an application for planning workouts.
-- Designed in collaboration with Miche Hoffer (http://michrofit.com/)

world coach {
  static version : "2014.06.17";

  datatype exercise_type {
    string name;
    string description_link;
    exercise_category category;
    exercise_parameter parameter;
  }

  enum exercise_category {
    UPPER_BODY;
    LOWER_BODY;
    CORE;
    CARDIO;
    BALANCE;
  }

  enum exercise_parameter {
    REPETITION_COUNT;
    DISTANCE;
  }

  datatype exercise {
    exercise_type type;
    -- one of these is valid based on type.parameter
    string repetition_count;
    string distance;
    string name() {
      return repetition_count ++ distance ++ " " ++ type.name;
    }
  }

  datatype exercise_set {
    string name;
    string rounds;
    list[exercise] exercises;
    string publish() => "Test";
  }
}
