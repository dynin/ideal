-- I Ching Hexagram Oracle
-- An oracle application that casts three hexagrams representing past, present, and future
-- Based on the ancient Chinese Book of Changes (I Ching)

implicit import ideal.runtime.elements;

-- Hexagram class to represent each of the 64 hexagrams
class hexagram {
  integer number;
  string chinese_name;
  string english_name;
  string interpretation;

  hexagram(integer num, string cn, string en, string interp) {
    number = num;
    chinese_name = cn;
    english_name = en;
    interpretation = interp;
  }

  string to_string() {
    return "Hexagram " ++ number ++ ": " ++ english_name ++ " (" ++ chinese_name ++ ")";
  }

  string full_description() {
    return to_string() ++ "\n" ++ interpretation;
  }
}

-- Initialize all 64 hexagrams with their meanings
readonly list[hexagram] create_hexagrams() {
  hexagrams : base_list[hexagram].new();

  hexagrams.append(hexagram.new(1, "乾", "The Creative", "Supreme success and power. The time is ripe for creative action. Perseverance brings good fortune. Leadership and strength are at your disposal."));
  hexagrams.append(hexagram.new(2, "坤", "The Receptive", "Receptivity and devotion. Success through gentle persistence and following. Like the earth, be supportive and yielding. Seek guidance from those wiser."));
  hexagrams.append(hexagram.new(3, "屯", "Difficulty at the Beginning", "Initial difficulties and chaos. Do not rush forward. Gather your resources and seek help. Patient persistence will overcome obstacles."));
  hexagrams.append(hexagram.new(4, "蒙", "Youthful Folly", "Inexperience and immaturity. Seek wisdom and guidance. Do not act rashly. Education and humility lead to growth and understanding."));
  hexagrams.append(hexagram.new(5, "需", "Waiting", "Patient waiting is necessary. Do not force matters. Nourish yourself during the wait. The right moment will come. Trust in divine timing."));
  hexagrams.append(hexagram.new(6, "訟", "Conflict", "Dispute and contention arise. Avoid escalation. Seek mediation and compromise. Retreat from conflict is wise. Legal matters require caution."));
  hexagrams.append(hexagram.new(7, "師", "The Army", "Discipline and organization. Strong leadership is needed. United effort brings success. Maintain moral authority. Strategy over force."));
  hexagrams.append(hexagram.new(8, "比", "Holding Together", "Union and alliance. Seek supportive relationships. Be sincere and truthful. Together you are stronger. Choose your allies carefully."));
  hexagrams.append(hexagram.new(9, "小畜", "Small Accumulating", "Small gains through gentle restraint. Build gradually. Minor obstacles slow progress. Patience and attention to detail bring success."));
  hexagrams.append(hexagram.new(10, "履", "Treading", "Conduct yourself carefully. Mind your manners and behavior. Move forward with caution and respect. Dangerous situations require proper conduct."));
  hexagrams.append(hexagram.new(11, "泰", "Peace", "Harmony and prosperity. Heaven and earth in balance. Good fortune flows naturally. Success in all endeavors. Share your abundance."));
  hexagrams.append(hexagram.new(12, "否", "Standstill", "Stagnation and obstruction. Progress is blocked. Withdraw and conserve energy. Do not waste effort. Better times will come."));
  hexagrams.append(hexagram.new(13, "同人", "Fellowship", "Community and partnership. Work together toward common goals. Organize and unite with others. Shared purpose brings success."));
  hexagrams.append(hexagram.new(14, "大有", "Great Possession", "Abundance and great success. Blessings are yours. Use wealth wisely and generously. Power brings responsibility. Share your fortune."));
  hexagrams.append(hexagram.new(15, "謙", "Modesty", "Humility brings success. Do not boast or show off. Modesty and simplicity are virtues. Greatness through humble service."));
  hexagrams.append(hexagram.new(16, "豫", "Enthusiasm", "Joy and anticipation. Follow your inspiration. Music and celebration are appropriate. Enthusiasm mobilizes helpers and resources."));
  hexagrams.append(hexagram.new(17, "隨", "Following", "Adapt to the times. Follow the guidance of those wiser. Rest and renewal are needed. Go with the flow. Accept what comes."));
  hexagrams.append(hexagram.new(18, "蠱", "Work on the Decayed", "Corruption must be addressed. Repair what has been damaged. Clean up the mess. Take responsibility. Healing is possible."));
  hexagrams.append(hexagram.new(19, "臨", "Approach", "New opportunities approach. Leadership and advancement are favored. Springtime of growth. Move forward confidently."));
  hexagrams.append(hexagram.new(20, "觀", "Contemplation", "Observe and reflect. Step back for perspective. Spiritual insight comes through meditation. Be a model for others."));
  hexagrams.append(hexagram.new(21, "噬嗑", "Biting Through", "Obstacles must be removed forcefully. Legal action may be necessary. Clear away what blocks progress. Justice and decisiveness are required."));
  hexagrams.append(hexagram.new(22, "賁", "Grace", "Beauty and elegance. Form and substance in harmony. Cultural refinement. Appearances matter, but do not neglect the essential."));
  hexagrams.append(hexagram.new(23, "剝", "Splitting Apart", "Decay and erosion. Things fall apart. Yield to the inevitable. Preserve what is essential. Wait for renewal to begin."));
  hexagrams.append(hexagram.new(24, "復", "Return", "Recovery and renewal. The turning point has come. Spring returns after winter. New beginning emerges. Return to your source."));
  hexagrams.append(hexagram.new(25, "無妄", "Innocence", "Natural spontaneity. Act without ulterior motives. Authenticity brings fortune. Unexpected events occur. Stay true to your nature."));
  hexagrams.append(hexagram.new(26, "大畜", "Great Accumulating", "Great potential restrained. Build your strength. Study the wisdom of the past. Store energy for future action. Self-cultivation is favored."));
  hexagrams.append(hexagram.new(27, "頤", "Nourishment", "Proper nourishment of body and spirit. Watch what you consume and express. Care for yourself and others. Sustenance leads to health."));
  hexagrams.append(hexagram.new(28, "大過", "Great Excess", "Extraordinary pressure and burden. The structure is stressed. Bold action is needed. Take care not to collapse. Seek support."));
  hexagrams.append(hexagram.new(29, "坎", "The Abysmal Water", "Danger and difficulty. Proceed with caution. Sincerity and inner truth protect you. Flow around obstacles like water. Faith is essential."));
  hexagrams.append(hexagram.new(30, "離", "The Clinging Fire", "Clarity and illumination. Cling to what is bright and true. Awareness and consciousness. Illuminate the path. Depend on what is reliable."));
  hexagrams.append(hexagram.new(31, "咸", "Influence", "Mutual attraction and influence. Relationships form. Sensitivity to others. Courtship and romance. Openness and receptivity bring connection."));
  hexagrams.append(hexagram.new(32, "恆", "Duration", "Endurance and constancy. Long-lasting relationships. Persist in the right direction. Steadfast commitment. Success through consistency."));
  hexagrams.append(hexagram.new(33, "遯", "Retreat", "Strategic withdrawal. Retire from conflict. Preserve yourself for better times. Retreat is not defeat. Maintain dignity and principles."));
  hexagrams.append(hexagram.new(34, "大壯", "Great Power", "Great strength and vigor. Power must be used wisely. Do not abuse your strength. Forceful action can succeed if tempered with restraint."));
  hexagrams.append(hexagram.new(35, "晉", "Progress", "Advancement and promotion. Rapid progress is made. Your talents are recognized. Rise like the sun. Success and honor come."));
  hexagrams.append(hexagram.new(36, "明夷", "Darkening of the Light", "Darkness and oppression. Hide your light. Protect your inner truth. Persevere through difficult times. Better to yield than break."));
  hexagrams.append(hexagram.new(37, "家人", "The Family", "Family and household. Proper roles and relationships. Domestic harmony. Start with your own house. Order begins at home."));
  hexagrams.append(hexagram.new(38, "睽", "Opposition", "Contradiction and divergence. Differences arise. Maintain respect despite disagreement. Small matters can still succeed. Seek common ground."));
  hexagrams.append(hexagram.new(39, "蹇", "Obstruction", "Obstacles block the way. Turn inward for reflection. Seek help and guidance. Going forward is difficult. Look within for solutions."));
  hexagrams.append(hexagram.new(40, "解", "Deliverance", "Release and liberation. Tension dissolves. Forgiveness and pardon. Return to normalcy. Clear away the old. New start is possible."));
  hexagrams.append(hexagram.new(41, "損", "Decrease", "Reduction and simplification. Less is more. Sacrifice for higher purpose. Control desires. Simple and sincere brings good fortune."));
  hexagrams.append(hexagram.new(42, "益", "Increase", "Growth and benefit. Blessings increase. Good fortune flows. Advance and undertake projects. Seize opportunities. Help others."));
  hexagrams.append(hexagram.new(43, "夬", "Breakthrough", "Resolute determination. Breakthrough after long struggle. Confront the problem directly. Truth must be proclaimed. Act decisively."));
  hexagrams.append(hexagram.new(44, "姤", "Coming to Meet", "Unexpected encounter. Temptation appears. Be cautious of seduction. Weak challenges strong. Maintain your principles."));
  hexagrams.append(hexagram.new(45, "萃", "Gathering Together", "Assembly and congregation. Come together for common purpose. Leadership organizes the group. Offerings and sacrifice unite people."));
  hexagrams.append(hexagram.new(46, "升", "Pushing Upward", "Ascending and rising. Gradual advancement. Growth like a plant pushing through earth. Effort brings steady progress. Seek guidance."));
  hexagrams.append(hexagram.new(47, "困", "Oppression", "Exhaustion and adversity. Resources are depleted. Difficult times test your character. Maintain inner truth. Accept the situation."));
  hexagrams.append(hexagram.new(48, "井", "The Well", "The source that nourishes all. Inexhaustible resources. Community and service. Draw from deep wisdom. Maintain and improve the infrastructure."));
  hexagrams.append(hexagram.new(49, "革", "Revolution", "Radical change and transformation. The old order must go. Revolution is necessary. Timing is critical. Make it legitimate."));
  hexagrams.append(hexagram.new(50, "鼎", "The Cauldron", "Nourishment and transformation. The sacred vessel. Sacrifice to higher powers. Cultural refinement and wisdom. Prepare the offering."));
  hexagrams.append(hexagram.new(51, "震", "The Arousing Thunder", "Shock and surprise. Thunder brings fear then laughter. Wake-up call. Sudden movement. After the shock, reflection brings understanding."));
  hexagrams.append(hexagram.new(52, "艮", "Keeping Still", "Stillness and meditation. Stop and be quiet. Know when to stop. Contemplation brings clarity. Rest the restless mind."));
  hexagrams.append(hexagram.new(53, "漸", "Gradual Progress", "Step by step advancement. Development like a growing tree. Proceed gradually. Proper sequence. Marriage and long-term commitments favored."));
  hexagrams.append(hexagram.new(54, "歸妹", "The Marrying Maiden", "Improper relationships. Desire without true connection. Know your place. Subordinate position. Acting beyond your role brings misfortune."));
  hexagrams.append(hexagram.new(55, "豐", "Abundance", "Peak of success. Fullness and plenty. Enjoy the harvest. Be like the sun at midday. Abundance is temporary, so use it wisely."));
  hexagrams.append(hexagram.new(56, "旅", "The Wanderer", "Travel and transition. The stranger in a strange land. Adapt to circumstances. Be cautious and modest. Know that this too shall pass."));
  hexagrams.append(hexagram.new(57, "巽", "The Gentle Wind", "Gentle penetration. Subtle influence like wind or wood. Repeated effort gradually succeeds. Flexibility and adaptation. Follow guidance."));
  hexagrams.append(hexagram.new(58, "兌", "The Joyous Lake", "Joy and pleasure. Cheerful exchange. Good communication. Enjoyment shared with others. Delight in fellowship. Inner joy radiates outward."));
  hexagrams.append(hexagram.new(59, "渙", "Dispersion", "Dissolution and scattering. Rigid structures dissolve. Spiritual forces dispel obstacles. Religious devotion. Let go of old patterns."));
  hexagrams.append(hexagram.new(60, "節", "Limitation", "Necessary limits and boundaries. Moderation in all things. Accept natural limitations. Regulate yourself. Balance between excess and deficiency."));
  hexagrams.append(hexagram.new(61, "中孚", "Inner Truth", "Sincerity and inner truth. Truthfulness brings power. Open your heart. Honest communication. Faith that moves mountains."));
  hexagrams.append(hexagram.new(62, "小過", "Small Exceeding", "Minor matters succeed. Small things are favored. Great things should wait. Humility and conscientiousness. Pay attention to details."));
  hexagrams.append(hexagram.new(63, "既濟", "After Completion", "Everything in its place. Success achieved. But the peak contains the seeds of decline. Remain vigilant. Completion brings new beginning."));
  hexagrams.append(hexagram.new(64, "未濟", "Before Completion", "Not yet finished. The journey continues. Be patient and careful. Success is close but not yet achieved. One more step is needed."));

  return hexagrams;
}

-- Simple pseudo-random number generator using a seed
-- Note: This is a basic implementation for demonstration purposes
class random_generator {
  var integer seed;

  random_generator(integer initial_seed) {
    seed = initial_seed;
  }

  -- Generate a pseudo-random number between 0 and max-1
  integer next(integer max) {
    -- Linear congruential generator
    seed = (seed * 1103515245 + 12345) % 2147483648;
    return (seed % max);
  }
}

-- Cast a random hexagram
hexagram cast_hexagram(random_generator rng, readonly list[hexagram] hexagrams) {
  random_index : rng.next(64);
  return hexagrams[random_index];
}

-- Perform a three-hexagram reading
void perform_reading(integer seed_value) {
  println("═══════════════════════════════════════════════════════════════");
  println("             I CHING ORACLE - THREE HEXAGRAM READING           ");
  println("═══════════════════════════════════════════════════════════════");
  println("");
  println("Consulting the ancient wisdom of the I Ching...");
  println("The oracle casts three hexagrams for your guidance:");
  println("");

  -- Initialize hexagrams
  all_hexagrams : create_hexagrams();

  -- Initialize random number generator with seed
  rng : random_generator.new(seed_value);

  -- Cast three hexagrams
  past : cast_hexagram(rng, all_hexagrams);
  present : cast_hexagram(rng, all_hexagrams);
  future : cast_hexagram(rng, all_hexagrams);

  -- Display the reading
  println("─────────────────────────────────────────────────────────────");
  println("  THE PAST - What Has Been");
  println("─────────────────────────────────────────────────────────────");
  println(past.full_description());
  println("");

  println("─────────────────────────────────────────────────────────────");
  println("  THE PRESENT - What Is Now");
  println("─────────────────────────────────────────────────────────────");
  println(present.full_description());
  println("");

  println("─────────────────────────────────────────────────────────────");
  println("  THE FUTURE - What Will Be");
  println("─────────────────────────────────────────────────────────────");
  println(future.full_description());
  println("");

  println("═══════════════════════════════════════════════════════════════");
  println("                      ORACLE GUIDANCE                          ");
  println("═══════════════════════════════════════════════════════════════");
  println("");
  println("Your journey has been shaped by " ++ past.english_name ++ ",");
  println("currently guided by " ++ present.english_name ++ ",");
  println("and moving toward " ++ future.english_name ++ ".");
  println("");
  println("Meditate on these three aspects of your path.");
  println("The wisdom of the I Ching illuminates the way forward.");
  println("Trust in the natural flow of change and transformation.");
  println("");
  println("═══════════════════════════════════════════════════════════════");
}

-- Main entry point
void main() {
  -- Use a seed value for the random generator
  -- In a real implementation, this could be based on current time
  -- For demonstration, we use a fixed seed
  seed : 42;

  -- Perform the oracle reading
  perform_reading(seed);
}

-- Run the oracle
main();
