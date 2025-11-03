# Activity Tracker - Ideal Language Example

An activity tracking application written in the Ideal programming language.

## Overview

This activity tracker demonstrates several key features of the Ideal language:

- **Enums**: `activity_category` for categorizing different types of activities
- **Datatypes**: `activity` as an immutable value type for representing individual activities
- **Classes**: `activity_tracker` as a mutable class for managing collections of activities
- **Auto-constructors**: Automatic generation of constructors for datatypes
- **String operations**: Concatenation using `++` operator
- **Collections**: Working with lists to store and iterate over activities
- **Control flow**: For loops, if statements, and switch statements
- **Pure functions**: Methods marked as `pure` that don't have side effects
- **Stringable interface**: Implementing `to_string()` for custom string representations

## Features

The activity tracker provides the following functionality:

1. **Add Activities**: Record activities with a name, duration (in minutes), and category
2. **View All Activities**: Display a complete list of all tracked activities
3. **Calculate Total Time**: Compute total time spent across all activities
4. **Category Statistics**: Group activities by category and show time distribution
5. **Summary Report**: Comprehensive overview of all activities and statistics

## Categories

Activities can be categorized as:
- Work
- Exercise
- Learning
- Leisure
- Social
- Personal

## File Structure

- `activity_tracker.i` - Main application file containing:
  - `activity_category` enum
  - `activity` datatype
  - `activity_tracker` class
  - `main()` function with sample data

## Running the Program

To run the activity tracker:

```bash
# Build Ideal (if not already built)
make buildall

# Run the activity tracker
make activity_tracker
```

Or manually:
```bash
java -ea -classpath build/classes:thirdparty/jsr305-3.0.2.jar:thirdparty/java-cup-11b.jar:thirdparty/antlr-4.9.3-complete.jar \
  ideal.development.tools.create -run -input=activity_tracker.i
```

## Sample Output

The program comes with pre-populated sample data demonstrating various activities:

```
Starting Activity Tracker...

Adding activities...
Added activity: Morning workout (45 min) - Exercise
Added activity: Project meeting (60 min) - Work
Added activity: Code review (30 min) - Work
...

==================================
    ACTIVITY TRACKER SUMMARY
==================================
Total activities: 10

=== Total Time ===
Total minutes: 620
Total hours: 10h 20m

=== Category Statistics ===
  Work: 180 min (29%)
  Exercise: 115 min (18%)
  Learning: 150 min (24%)
  Leisure: 120 min (19%)
  Social: 75 min (12%)
  Personal: 20 min (3%)

=== All Activities ===
  Morning workout (45 min) - Exercise
  Project meeting (60 min) - Work
  ...
==================================
```

## Code Highlights

### Enum with Method

```ideal
enum activity_category {
  work;
  exercise;
  learning;

  string to_string() {
    switch (this) {
      case work:
        return "Work";
      case exercise:
        return "Exercise";
      ...
    }
  }
}
```

### Auto-constructor Datatype

```ideal
auto_constructor datatype activity {
  implements stringable;

  string name;
  nonnegative duration_minutes;
  activity_category category;

  override string to_string() pure {
    return name ++ " (" ++ duration_minutes ++ " min) - " ++ category.to_string();
  }
}
```

### Mutable Class with Collections

```ideal
class activity_tracker {
  var list[activity] activities;

  void add_activity(string name, nonnegative duration_minutes, activity_category category) {
    new_activity : activity.new(name, duration_minutes, category);
    activities.append(new_activity);
  }

  nonnegative get_total_time() {
    var nonnegative total : 0;
    for (act : activities) {
      total += act.duration_minutes;
    }
    return total;
  }
}
```

## Learning Points

This example demonstrates:

1. **Type Safety**: Using `nonnegative` type for durations ensures values are always >= 0
2. **Immutability**: Activities are immutable datatypes once created
3. **Variance**: Lists are covariant in Ideal
4. **Pure Methods**: The `to_string()` method is marked as `pure`
5. **Pattern Matching**: Switch statements for enum handling
6. **String Interpolation**: Building strings with `++` operator
7. **Object-Oriented Design**: Separation of data (activity) from behavior (activity_tracker)

## Customization

You can easily customize the tracker by:

1. Adding new categories to `activity_category` enum
2. Extending `activity` datatype with additional fields (tags, notes, timestamps)
3. Adding filtering methods to `activity_tracker` class
4. Implementing persistence to save/load activities
