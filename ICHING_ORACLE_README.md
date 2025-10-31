# I Ching Hexagram Oracle

An interactive oracle application written in the Ideal programming language that casts three I Ching hexagrams to provide guidance based on the ancient Chinese Book of Changes (I Ching).

## Overview

The I Ching Oracle casts three hexagrams representing:
- **Past**: What has shaped your current situation
- **Present**: Your current state and circumstances
- **Future**: The direction you are heading

Each reading provides wisdom from the 64 hexagrams of the I Ching, helping you gain insight into your life's journey and the natural flow of change.

## Features

- Complete implementation of all 64 I Ching hexagrams
- Each hexagram includes:
  - Number (1-64)
  - Chinese name (traditional characters)
  - English name
  - Detailed interpretation and guidance
- Three-hexagram reading format (Past, Present, Future)
- Pseudo-random hexagram selection
- Beautiful formatted output with clear sections

## The 64 Hexagrams

The application includes all 64 hexagrams from the I Ching, from:
- Hexagram 1: 乾 (The Creative) - Supreme success and power
- Hexagram 2: 坤 (The Receptive) - Receptivity and devotion
- ...through...
- Hexagram 63: 既濟 (After Completion) - Everything in its place
- Hexagram 64: 未濟 (Before Completion) - Not yet finished

Each hexagram contains authentic interpretations based on traditional I Ching wisdom.

## How to Use

### Prerequisites

1. A working Ideal development environment
2. Ideal compiler (create tool) built and functional

### Running the Oracle

Once the Ideal development environment is set up:

```bash
# From the ideal directory
make iching
```

Or using the create tool directly:

```bash
thirdparty/jdk/bin/java -ea -classpath build/classes:thirdparty/jsr305-3.0.2.jar:thirdparty/java-cup-11b.jar:thirdparty/antlr-4.9.3-complete.jar ideal.development.tools.create -run -input=iching_oracle.i
```

### Customizing the Reading

The oracle uses a seed value for pseudo-random number generation. You can modify the seed in the `main()` function at the bottom of `iching_oracle.i`:

```ideal
void main() {
  -- Change this seed value for different readings
  seed : 42;
  perform_reading(seed);
}
```

For truly random readings, you could integrate with a time-based seed or user input.

## Sample Output

```
═══════════════════════════════════════════════════════════════
             I CHING ORACLE - THREE HEXAGRAM READING
═══════════════════════════════════════════════════════════════

Consulting the ancient wisdom of the I Ching...
The oracle casts three hexagrams for your guidance:

─────────────────────────────────────────────────────────────
  THE PAST - What Has Been
─────────────────────────────────────────────────────────────
Hexagram 1: The Creative (乾)
Supreme success and power. The time is ripe for creative action...

─────────────────────────────────────────────────────────────
  THE PRESENT - What Is Now
─────────────────────────────────────────────────────────────
Hexagram 11: Peace (泰)
Harmony and prosperity. Heaven and earth in balance...

─────────────────────────────────────────────────────────────
  THE FUTURE - What Will Be
─────────────────────────────────────────────────────────────
Hexagram 24: Return (復)
Recovery and renewal. The turning point has come...

═══════════════════════════════════════════════════════════════
                      ORACLE GUIDANCE
═══════════════════════════════════════════════════════════════

Your journey has been shaped by The Creative,
currently guided by Peace,
and moving toward Return.

Meditate on these three aspects of your path.
The wisdom of the I Ching illuminates the way forward.
Trust in the natural flow of change and transformation.

═══════════════════════════════════════════════════════════════
```

## Implementation Details

### Architecture

The application consists of several key components:

1. **Hexagram Class**: Represents each hexagram with its properties
   - Number, Chinese name, English name, interpretation
   - Methods for formatted display

2. **Random Generator Class**: Simple pseudo-random number generator
   - Uses linear congruential generator algorithm
   - Seeded for reproducible or varied readings

3. **Hexagram Database**: Complete set of all 64 hexagrams
   - Authentic traditional meanings
   - Accessible guidance for modern readers

4. **Reading Function**: Orchestrates the three-hexagram casting
   - Formats output beautifully
   - Provides synthesis of the three hexagrams

### Code Structure

```ideal
class hexagram { ... }                    -- Hexagram data structure
readonly list[hexagram] create_hexagrams() -- Initialize all 64 hexagrams
class random_generator { ... }            -- Pseudo-random number generator
hexagram cast_hexagram(...)               -- Cast a single hexagram
void perform_reading(integer seed_value)  -- Perform complete reading
void main()                               -- Entry point
```

## About the I Ching

The I Ching (易經), or "Book of Changes," is one of the oldest Chinese classical texts. Dating back over 3,000 years, it has been used for divination, philosophy, and guidance. The 64 hexagrams represent all possible situations in life, formed by combinations of eight trigrams representing fundamental forces: Heaven, Earth, Thunder, Wind, Water, Fire, Mountain, and Lake.

## References

- Traditional I Ching hexagram meanings
- Wilhelm/Baynes translation concepts
- Classical Chinese philosophical traditions

## Future Enhancements

Possible improvements for future versions:

1. **Interactive Mode**: Allow user to enter a question
2. **Changing Lines**: Implement the traditional method of changing lines to derive a second hexagram
3. **Trigram Analysis**: Show the component trigrams and their meanings
4. **Hexagram Relationships**: Show related hexagrams and patterns
5. **Journal Feature**: Save readings for reflection
6. **Date-based Seeds**: Use system time for truly random readings
7. **Advanced Interpretations**: Include line readings and judgments
8. **Hexagram Images**: Display hexagram line patterns visually

## License

This application is part of the Ideal programming language project.

Copyright 2014-2025 The Ideal Authors. All rights reserved.

Use of this source code is governed by a BSD-style license that can be found in the LICENSE file or at https://theideal.org/license/

## Author

Created as a demonstration application for the Ideal programming language, bringing ancient wisdom to modern code.

---

*May the wisdom of the I Ching guide your path.*
