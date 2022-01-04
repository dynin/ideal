-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

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
  nonnegative repetition_count;
  string distance; -- TODO: this should be a decimal number.
}

datatype exercise_set {
  list[exercise] exercises;
}

datatype workout {
  exercise_set set;
  nonnegative repetition_count;
}

datatype exercise_program {
  list[workout] workouts;
}
