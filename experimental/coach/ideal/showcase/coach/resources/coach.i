-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
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
    string publish()
      -- The reason style declarations are inline is to make it possible to
      -- copy and paste the result into email.
      #(body
        (div (style "font-family: sans-serif;")
          (div (style "text-align:center; font-size:48pt; font-weight:bold;")
            "ideal.fit")
          (div (style "text-align:center; font-size:18pt; font-weight:bold;")
            name)
          (div (style "font-size:16pt; font-weight:bold;")
            rounds " rounds")
          (for (e exercises)
            (div (style "font-size:14pt;") {e.name()})
          )
        )
      )
  }
}
