-- Copyright 2014-2020 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

--- The core types of the ideal library.
package elements {
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
    subtypes entity;
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
  --- fully customizable reference entities.</cpp>
  --- <j>There is no Java equivalent of this type, except for builtin
  --- operators such as assignment and getter/setter convention for fields.</j>

  reference_kind reference[combivariant any value value_type] {
    subtypes entity;

    explicit value_type get() pure;
    explicit void set(value_type new_value) writeonly;
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
    subtypes value;
  }

  --- A value with an internal structure.
  concept composite_value {
    subtypes value;

    -- TODO: use special constructors to implement this.
    --not_yet_implemented self copy() pure;
    --not_yet_implemented self deep_copy() pure;

    not_yet_implemented immutable composite_value frozen_copy() pure;
    not_yet_implemented deeply_immutable composite_value deep_frozen_copy() pure;
  }

  --- <div>A type that represents computation the result of which doesn't matter.
  --- It has only one value that means nothing, and is also known as
  --- the unit type.</div>
  -- <div>TODO: make a singleton</div>
  singleton void {
    subtypes deeply_immutable data;
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
    subtypes value;

    not_yet_implemented static equivalence_relation[equality_comparable] natural_equality;
  }

  --- <div>A type that has two values: |true| and |false|.
  --- Logical operators |and|, |or|, |not|, |xor| are defined
  --- on the type.  Note that |and| and |or| operators
  --- are short-circuiting: if the second operand
  --- can't affect the value of the result, it's not
  --- evaluated, as is the case when the first operand to |and| is |false|.</div>
  --- <j>Equivalent to Java's boolean type.</j>
  enum boolean {
    subtypes deeply_immutable data;
    subtypes equality_comparable;
    subtypes stringable;

    true;
    false;
  }

  --- Sign of a number, or result of a comparison.
  enum sign {
    subtypes deeply_immutable data;
    subtypes comparable;
    -- TODO: subtypes stringable;

    less;
    equal;
    greater;
  }

  --- Describes types that have a natural order.
  concept comparable {
    subtypes equality_comparable;

    not_yet_implemented static order[comparable] natural_order;
  }


  -- TODO: add number that's a superclass of integer.

  --- <div>An integer value with an unlimited range.  The runtime
  --- should hide the distinction between integers that can be
  --- represented as one machine word and arbitrary-precision integers
  --- (<em>bigint</em>s).</div>
  ---
  --- <div>When integers with a specific representation are needed,
  --- types such |int32| from |ideal.library.interop| should be used.</div>
  datatype integer {
    subtypes deeply_immutable data;
    --TODO: this should be implemented by deeply_immutable data...
    subtypes comparable;
    subtypes stringable;

    -- TODO: add signum method
  }

  --- A type that encapsulates integer values from 0 (inclusive) to
  --- positive infinity (exclusive).
  datatype nonnegative {
    subtypes integer;
  }

  --- <div>A type whose values are distinct from valid values of other types.
  --- Unlike many languages in which |null| is a value, in ideal
  --- |null| is type, and values are specific--|missing|, |not_applicable|,
  --- |unknown|, |not_a_number| and so on.
  --- The motivation behind this is to allow the programmer to specify
  --- the nature of the exceptional value.
  --- Developers can subtype |null| if necessary, though the
  --- need for this should be quite rare.</div>
  ---
  --- <div>The check for null should be of the form |if (x is null)|.
  --- The program shouldn't depend on what type of null value is being returned.
  --- </div>
  -- Some runtimes may choose to use a single value for null in
  -- performance-optimized mode.
  interface null {
    subtypes deeply_immutable data;
    subtypes stringable;
    -- TODO: this is a hack to make equal_to work, remove.
    subtypes equality_comparable;
  }

  --- Signals that a value is missing.
  singleton missing {
    subtypes null;
  }

  --- A procedure that given entities as arguments, performs
  --- a computation and returns a value.  The arguments of the type
  --- are interpreted as follows: the first argument is the type of
  --- return value, the second through last argument types correspond
  --- to first through last argmuent type of procedure.  For example,
  --- |procedure[boolean, integer]| is a procedure that takes an integer
  --- argument and returns a boolean value.
  procedure_kind procedure[covariant entity return_type, ... contravariant entity argument_types] {
    subtypes immutable value;
    subtypes equality_comparable;
  }

  --- A procedure that has no visible side effects.
  procedure_kind function[covariant entity return_type, ... contravariant entity argument_types] {
    -- TODO: handle variable argument types in supertype
    -- subtypes procedure[return_type, argument_types];
    subtypes deeply_immutable data;
  }

  --- A boolean-valued pure function.
  concept predicate[contravariant value element] {
    subtypes function[boolean, element];
  }

  --- A binary relation.
  concept relation[contravariant value element] {
    subtypes function[boolean, element, element];
  }

  --- Equivalence relation is reflexive, symmetric, and transitive.
  concept equivalence_relation[contravariant value element] {
    subtypes relation[element];
  }

  --- Equivalence relation with a corresponding hash function.
  concept equivalence_with_hash[contravariant value element] {
    subtypes equivalence_relation[element];
    -- TODO: instead of returning integer, have a type declaration.
    integer hash(element the_element) pure;
  }

  --- Order defined by a relation that is transitive, antisymmetric, and total.
  concept order[contravariant value element] {
    subtypes function[sign, element, element];
  }

  --- A finite collection of element values, such as a sequence or a set.
  interface collection[combivariant value element] {
    subtypes composite_value;

    --- The number of elements in the collection.
    nonnegative size readonly;

    --- Specifies whether the collection has zero elements.
    boolean is_empty readonly;

    --- Specifies whether the collection has more than zero elements.
    --- Shortcut for |!is_empty|.
    boolean is_not_empty readonly;

    --- Enumerates elements in some collection-defined order.
    --- This method returns a snapshot of the collection state, so subsequent mutations
    --- of the collection do not cause changes in the returned list.
    immutable list[element] elements readonly;

    not_yet_implemented boolean has(predicate[element] the_predicate) pure;
    not_yet_implemented nonnegative count(predicate[element] the_predicate) pure;
    not_yet_implemented readonly collection[element] filter(predicate[element] the_predicate) pure;
    not_yet_implemented readonly list[element] order_by(order[element] the_order) pure;
    not_yet_implemented immutable collection[element]
        filter(predicate[element] the_predicate) immutable pure;
    not_yet_implemented void remove(predicate[element] the_predicate);

    void clear() writeonly;
  }

  --- A finite sequence of elements.
  interface list[combivariant value element] {
    subtypes collection[element];

    --- Read the list's element for the specified index.
    implicit readonly reference[element] get(nonnegative index) pure;
    implicit mutable reference[element] at(nonnegative index) mutable pure;
    -- TODO: handle writeonly refs
    not_yet_implemented implicit writeonly reference[element] set(nonnegative index) writeonly pure;

    --- Access the first element of the list.
    --- Assumes the list is not empty.
    element first readonly;

    --- Access the last element of the list.
    --- Assumes the list is not empty.
    element last readonly;

    void append(element the_element);
    void append_all(readonly list[element] the_list);
    void prepend(element the_element);

    not_yet_implemented void prepend_all(readonly list[element] the_list);
    not_yet_implemented void insert(nonnegative index, element the_element);
    not_yet_implemented void insert_all(nonnegative index, readonly list[element] the_list);
    not_yet_implemented element remove_at(nonnegative index);

    element remove_last();

    --- <div>Skips over the specified count of elements and returns an immutable slice
    --- that begins with |count| and ends with the end of this list.</div>
    -- TODO: slice in readonly should return readonly view.
    immutable list[element] skip(nonnegative count) pure;
    --- <div>Returns an immutable sublist with the given the starting and ending indices.</div>
    --- <div>The starting index is inclusive, the ending is exclusive.</div>
    immutable list[element] slice(nonnegative begin, nonnegative end) pure;

    -- TODO: readonly reverse() should return readonly view.
    not_yet_implemented readonly list[element] reverse() pure;
    --- Returns an immutable list with the order of the elements reversed.
    immutable list[element] reverse() immutable pure;

    --- Returns an immutable copy of this list.
    -- TODO: this should be in composite_value
    immutable list[element] frozen_copy() pure;
  }

  --- A half-open range of nonnegative integers.
  --- |begin| is inclusive, |end| is exclusive.
  -- TODO: implement ordered_set; parametrize over integers.
  interface range {
    subtypes deeply_immutable list[nonnegative];
    subtypes equality_comparable;

    --- Start of the range.
    nonnegative begin;

    --- End of the range; greater or equal than start.
    nonnegative end;

    range skip(nonnegative count) pure;
    range slice(nonnegative begin, nonnegative end) pure;
  }

  --- A finite set of elements.
  interface set[combivariant value element] {
    subtypes collection[element];

    boolean contains(element the_element) pure;

    void add(element the_element);
    void add_all(readonly collection[element] the_collection);
    boolean remove(element the_element);

    -- TODO: this should be in data
    not_yet_implemented set[element] copy() pure;
    immutable set[element] frozen_copy() pure;
  }

  --- A finite set of elements with an explicit ordering.
  --- Unlike a list, it is guaranteed not to have duplicate elements.
  --- Attempting to insert a duplicate element will trigger an assertion failure.
  interface ordered_set[combivariant value element] {
    subtypes collection[element];
    readonly subtypes readonly list[element], readonly set[element];
    immutable subtypes immutable list[element], immutable set[element];
    deeply_immutable subtypes deeply_immutable list[element], deeply_immutable set[element];

    void append(element the_element);
    void append_all(readonly list[element] the_list);
    void prepend(element the_element);

    immutable ordered_set[element] frozen_copy() pure;

    -- TODO: Declare remaining methods.
  }

  --- A type that encapsulates an atom of text.  Lists of characters
  --- make up |string|s.
  datatype character {
    subtypes deeply_immutable data;
    --TODO: this should be implemented by deeply_immutable data...
    subtypes equality_comparable;
    subtypes stringable;
  }

  --- A list of characters.
  datatype string {
    subtypes deeply_immutable list[character];
    subtypes stringable;
    -- TODO: this won't be needed when list[data] subtypes data.
    subtypes deeply_immutable data;
    --TODO: this should be implemented by deeply_immutable data...
    subtypes equality_comparable;

    -- TODO: these shouldn't be needed once type aliases work properly.
    implement string skip(nonnegative count) pure;
    implement string slice(nonnegative begin, nonnegative end) pure;
  }

  --- <div>A type whose values can be converted to a canonical
  --- string representation.</div>
  interface stringable {
    subtypes value;

    string to_string readonly;
  }

  --- A finite associative collection.
  interface dictionary[combivariant readonly value key_type, value value_type] {
    -- TODO: "dictionary" shouldn't be needed here, if to_java_transformer is smarter.
    subtypes collection[dictionary.entry[key_type, value_type]];

    interface entry[readonly value key_type, value value_type] {
      subtypes value;

      -- TODO: replace with references
      key_type key immutable;
      value_type value readonly;
    }

    -- TODO: value_type or null
    value_type or null get(key_type key) pure;
    value_type or null put(key_type key, value_type value);
    value_type or null remove(key_type key);

    -- TODO: should we use keys().contains instead?
    boolean contains_key(key_type key) pure;

    readonly set[key_type] keys() pure;
    immutable set[key_type] keys() immutable pure;

    readonly collection[value_type] values() pure;

    immutable dictionary[key_type, value_type] frozen_copy() pure;
  }

  --- Values of this type can be compared for equality using
  --- reference comparison.
  interface reference_equality {
    subtypes equality_comparable;
  }

  --- An identifier such as a URI.
  interface identifier {
    subtypes deeply_immutable data;
    subtypes stringable;
    subtypes equality_comparable;
  }

  -- TODO: of_type()
}
