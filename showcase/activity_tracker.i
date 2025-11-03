-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

--- Activity Tracker Application
---
--- A simple activity tracking system that demonstrates:
--- - Enums with methods
--- - Datatypes with auto-constructors
--- - List operations
--- - String concatenation
--- - Control flow

--- Category enum for different types of activities
enum activity_category {
  work;
  exercise;
  learning;
  leisure;

  --- Convert category to string representation
  string to_string() {
    switch (this) {
      case work:
        return "Work";
      case exercise:
        return "Exercise";
      case learning:
        return "Learning";
      case leisure:
        return "Leisure";
      default:
        return "Unknown";
    }
  }
}

--- Activity datatype representing a single tracked activity
auto_constructor datatype activity {
  implements stringable;

  string name;
  nonnegative duration_minutes;
  activity_category category;

  override string to_string() {
    return name ++ " (" ++ duration_minutes ++ " min) - " ++ category.to_string();
  }
}

--- Main function demonstrating the activity tracker
void main() {
  println("Starting Activity Tracker...");
  println();

  -- Create sample activities using list literal
  activities : [
    activity.new("Morning workout", 45, activity_category.exercise),
    activity.new("Project meeting", 60, activity_category.work),
    activity.new("Code review", 30, activity_category.work),
    activity.new("Read programming book", 90, activity_category.learning),
    activity.new("Watch movie", 120, activity_category.leisure),
    activity.new("Evening run", 30, activity_category.exercise)
  ];

  -- Display all activities
  println("=== All Activities ===");
  for (act : activities) {
    println("  " ++ act.to_string);
  }
  println();

  -- Calculate total time
  var nonnegative total : 0;
  for (act : activities) {
    total += act.duration_minutes;
  }

  println("=== Total Time ===");
  println("Total minutes: " ++ total);
  println();
  println("Total activities tracked: " ++ activities.size);
  println();

  println("Activity Tracker completed successfully!");
}

-- Execute the main function
main();
