-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- The ideal solution to the circle-ellipse problem.
---
--- @see http://en.wikipedia.org/wiki/Circle-ellipse_problem

-- Constructor is generated using auto_constructor extension.
auto_constructor datatype ellipse {
  implements stringable;

  integer width;
  integer height;

  void scale(integer horizontal, integer vertical) {
    width *= horizontal;
    height *= vertical;
  }

  override string to_string() pure {
    return "ellipse(" ++ width ++ "*" ++ height ++ ")";
  }
}

-- Constructor is generated using auto_constructor extension.
auto_constructor datatype circle {
  implements readonly ellipse;

  integer diameter;

  override integer width() => diameter;
  override integer height() => diameter;

  void scale(integer factor) {
    diameter *= factor;
  }

  override string to_string() pure {
    return "circle(" ++ diameter ++ ")";
  }
}

void print_as_ellipse(readonly ellipse e) {
  println("Printing as ellipse: width ", e.width, ", height ", e.height);
}

void try_to_modify(readonly ellipse the_ellipse) {
  println("Trying to modify ", the_ellipse);
  if (the_ellipse is mutable ellipse) {
    -- Note that static type of |the_ellipse| is automatically narrowed to |mutable ellipse|.
    the_ellipse.scale(2, 3);
    println("Modified succesfully: ", the_ellipse);
  } else {
    println("This is not a mutable ellipse");
  }
}

void main() {
  the_ellipse : ellipse.new(2, 3);
  println("Starting with an ellipse: ",  the_ellipse);
  the_ellipse.scale(3, 5);
  println("Scaled the ellipse: ",  the_ellipse);
  print_as_ellipse(the_ellipse);
  println();

  the_circle : circle.new(4);
  println("Starting with a circle: ",  the_circle);
  the_circle.scale(2);
  println("Scaled the circle: ",  the_circle);
  print_as_ellipse(the_circle);
  println();

  try_to_modify(the_ellipse);
  try_to_modify(the_circle);
}

main();

-- Starting with an ellipse: ellipse(2*3)
-- Scaled the ellipse: ellipse(6*15)
-- Printing as ellipse: width 6, height 15
--
-- Starting with a circle: circle(4)
-- Scaled the circle: circle(8)
-- Printing as ellipse: width 8, height 8
--
-- Trying to modify ellipse(6*15)
-- Modified succesfully: ellipse(12*45)
-- Trying to modify circle(8)
-- This is not a mutable ellipse
