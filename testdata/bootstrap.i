-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- Root type in the ideal type system.
--- It is the supertype of |value| and |reference|.
--- There should be no need to subtype |entity| directly; in fact,
--- it's unlikely that you'll use this type in your code at all.
---
-- |non_terminating| is not a subtype of |entity| because it marks functions
-- that never complete, and therefore never return any entity.
--
-- <x>There are also a few types used internally in the kernel that
-- are not a subtype of entity--|error_type| and types used
-- to describe thrown exceptions.</x>

concept entity {
}

--- <div>Root type for regular values (as opposed to |reference|s).</div>
---
--- <div>Before using this type as a direct supertype, consider
--- whether your objects satisfy the contract for the |data| type.
--- Subtypes of |data| have library support for a lot of useful functionality.
--- </div>
---
--- <c>Corresponds to "rvalue" in C.</c>
--- <j>Roughly corresponds to |Object| in Java.</j>

concept value {
  refines entity;
}

--- <div>An entity that supports read and write access to a value.
--- Implementations include local variables, fields, and elements
--- of a list.</div>
---
--- <div>You can use this type to implement pass-by-reference semantics,
--- such as the assignment operation.</div>
---
--- <c>Corresponds to "lvalue" in C.</c>
--- <cpp>C++ supports pass-by-reference, but not the creation of
--- fully customizable reference objects.</cpp>
--- <j>There is no Java equivalent of this type, except for builtin
--- operators such as assignment and getter/setter convention for fields.</j>

reference_kind reference[any value value_type] {
  refines entity;

  value_type get() readonly;
  void set(value_type new_value) writeonly;
}

--- <div>Data is a value that can be fully represented by bits.
--- It can be serialized for persistent storage or transfer
--- over the network.  The fields of the data object must
--- also be data.  Most entities in a typical program--such
--- as integers and strings--are data.</div>
---
--- <div>The library provides a lot of functionality for subtypes of |data|,
--- such as equivalence predicates, hash functions, and serializers.</div>
---
--- <div>Examples of values that are <em>not</em> data are a clock or a file
--- descriptor--since they refer to entities that cannot be packaged and
--- sent over the network.</div>
---
--- <j>Related concepts in Java are |Serializable| and |Cloneable|.</j>

concept data {
  refines value;

  not_yet_implemented data copy() readonly;
  not_yet_implemented data deep_copy() readonly;

  not_yet_implemented immutable data frozen_copy() readonly;
  not_yet_implemented deeply_immutable data deep_frozen_copy() readonly;
}

--- <div>A type whose values can be converted to a canonical
--- string representation.</div>
concept stringable {
  refines readonly value;

  string to_string() readonly;
}

--- <div>A type that represents computation the result of which doesn't matter.
--- It has only one value that means nothing, and is also known as
--- the unit type.</div>
-- <div>TODO: make a singleton</div>
singleton void {
  extends deeply_immutable data;
}

--- <div>This type means that no observable computation has taken place,
--- and that program state is unaffected.  Empty statement is of
--- type |nothing|.</div>
---
--- <div>This is a stronger contract than |void|, which allows
--- procedures that don't return any value yet have side effects.</div>
singleton nothing {
  extends void;
}

--- <div>Describes types that have a natural equivalence relation,
--- such as an integer.  There are types for which multiple good
--- equivalence relationships exist--consider a modifiable sequence, that
--- can be compared either element-by-element or by reference.
--- On the other hand, for an immutable sequence
--- element-by-element comparison makes more sense.</div>
---
--- <div>When obviously best choice doesn't exist, it is preferrable to
--- force the programmer to make decision which equivalence relation
--- to use explicitly.  Data structures have been designed to
--- allow the relation to be selected.</div>
---
--- <div>For operators == and != to work on values of a type, the type
--- must implement this interface.</div>
concept equality_comparable {
  refines value;

  static equivalence_relation[equality_comparable] equivalence;
}

--- <div>A type that has two values: |true| and |false|.
--- Logical operators |and|, |or|, |not|, |xor| are defined
--- on the type.  Note that |and| and |or| operators
--- are short-circuiting: if the second operand
--- can't affect the value of the result, it's not
--- evaluated, as is the case when the first operand to |and| is |false|.</div>
--- <j>Equivalent to `Java's boolean type.</j>
enum boolean {
  implements deeply_immutable data;
  implements equality_comparable;
  implements stringable;

  true;
  false;
}

--- <div>An integer value with an unlimited range.  The runtime
--- should hide the distinction between integers that can be
--- represented as one machine word and arbitrary-precision integers
--- (<em>bigint</em>s).</div>
---
--- <div>When integers with a specific representation are needed,
--- types such |int32| from |ideal.library.interop| should be used.</div>
class integer {
  implements deeply_immutable data;
  implements equality_comparable;
  implements stringable;
}

--- A type that encapsulates integer values from 0 (inclusive) to
--- positive infinity (exclusive).
class nonnegative {
  refines integer;
}

--- A type that encapsulates integer values from 1 (inclusive) to
--- positive infinity (exclusive).
class positive {
  refines nonnegative;
}


--- <div>A type whose values are distinct from valid values of other types.
--- Unlike many languages in which |null| is a value, in ideal
--- |null| is type, and values are specific--|missing|, |not_applicable|
--- |not_a_number| and so on.  The motivation behind this is to allow
--- the programmer to specify the nature of the exceptional value.
--- Developers can subtype |null| if necessary, though the
--- need for this should be quite rare.</div>
---
--- <div>The check for null should be of the form |if (x is null)|.
--- The program shouldn't depend on what type of null value is being returned.
--- </div>
-- Some runtimes may choose to use a single value for null in
-- performance-optimized mode.
concept null {
  refines deeply_immutable data;
  implements stringable;
}

--- A subroutine that given values as arguments, returns a value
--- that is a result of computation.  The arguments of the type
--- are interpreted as follows: the first argument is the type of
--- return value, the second through last argument types correspond
--- to first through last arguent type of procedure.  For example,
--- |procedure[boolean, integer]| is a procedure that takes an integer
--- argument and returns boolean.
procedure_kind procedure[entity ret, entity arg1] {
  refines value;
}

procedure_kind procedure[entity ret, entity arg1, entity arg2] {
  refines value;
}

--- A procedure that has no visible side effects.
procedure_kind function[entity ret, entity arg1] {
  refines procedure[ret, arg1];
  implements deeply_immutable data;
}

procedure_kind function[entity ret, entity arg1, entity arg2] {
  refines procedure[ret, arg1, arg2];
  implements deeply_immutable data;
}

concept predicate[value element] {
  refines function[boolean, element];
}

concept relation[value element] {
  refines function[boolean, element, element];
}

--- Equivalence relation is reflexive, symmetric, and transitive.
concept equivalence_relation[value element] {
  refines relation[element];
}

--- A finite collection of element values, such as a sequence or a set.
interface collection[value element] {
  refines value;

  readonly nonnegative size;
  readonly boolean is_empty;

  -- Enumerates elements in some collection-defined order.
  immutable list[element] elements() readonly;
  boolean has(predicate[element] p) readonly;
  boolean all(predicate[element] p) readonly;
  readonly collection[element] filter(predicate[element] p) readonly;
  immutable collection[element] filter(predicate[element] p) immutable;
  void remove(predicate[element] p);

  void clear() writeonly;
}

interface collection_with_equivalence[value element] {
  refines collection[element];

  equivalence_relation[element] element_equivalence;
  boolean contains(element e) readonly;
  boolean contains_all(collection[element] c) readonly;
  void remove_one(element e);
  void remove_all(collection[element] c);
  void retain_all(collection[element] c);
}

--interface collection_with_equivalence[data element] {
--  implements data;
--}

--interface collection[equality_comparable element] {
--  implements collection_with_equivalence[element];
--}

interface list[value element] {
  implements collection[element];

  implicit reference[element] get(nonnegative index) readonly;
  void append(element e);
  void prepend(element e);
  void insert(nonnegative index, element e);
  void append_all(readonly list[element] c);
  void prepend_all(readonly list[element] c);
  void insert_all(nonnegative index, readonly list[element] c);
  element remove_at(nonnegative index);
  -- TODO: slice
}

interface list_with_equivalence[value element] {
  implements collection_with_equivalence[element];
  implements list[element];

  list_with_equivalence(equivalence_relation[element] element_equivalence);
}

--interface list[equality_comparable element] {
--  implements list_with_equivalence[element];
--  list() {
--    -- super(element.equivalence);
--  }
--}

--- A type that encapsulates an atom of text.  Lists of chracaters
--- make up |string|s.
class character {
  implements deeply_immutable data;
  implements equality_comparable;
  implements stringable;
}

class string {
  implements deeply_immutable list[character];
}

interface closeable {
  readonly boolean is_closed;
  void close();
  -- void close(task callback);
  -- void add_on_close(task callback);
  -- void remove_on_close(task callback);
}

interface syncable {
  -- extensible enum sync_type {
  --   no_sync; /* NOP */
  --   default_sync;
  --   process_sync;
  --   host_sync;
  --   cluster_sync;
  --   lan_sync;
  --  wan_sync;
  --  /*  world?... */
  --}
  -- void sync(sync_type type: default_sync);
  --void sync(sync_type type, task callback);
  void sync();
}

interface input[value element] {
  extends closeable;
  readonly boolean is_available;
  readonly nonnegative available;
  immutable list[element] read(positive max);
  -- onread(task[immutable list[data]);
  -- void skip(nonnegative howmany);
}

concept output[value element]  {
  extends closeable, syncable;
  readonly boolean is_available;
  readonly nonnegative available;
  void write(readonly list[element] c);
  -- void send(input[data] copy, task done);
}
