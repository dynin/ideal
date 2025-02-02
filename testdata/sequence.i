-- Copyright 2014-2025 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

import java.util.List;
import java.util.ArrayList;

--implicit import ideal.library;
implicit import ideal.concepts;
implicit import ideal.constructs;
import ideal.concepts.type;
import ideal.scanners.scanner_element;
import ideal.semantics.lookup_row;

public class sequence[readonly_data element] {
  implements readonly_data;

  private List list;
  public overload sequence() {
    list : ArrayList.new();
  }
  public overload sequence(element n1) {
    this();

    append(n1);
  }
  public overload sequence(element n1, element n2) {
    this();
    append(n1);
    append(n2);
  }
  public overload sequence(sequence ns1, element n2) {
    this();
    append_all(ns1);
    append(n2);
  }
  public overload sequence(element n1, sequence ns2) {
    this();
    append(n1);
    append_all(ns2);
  }
  public overload sequence(element n1, element n2, element n3) {
    this();
    append(n1);
    append(n2);
    append(n3);
  }
  public overload sequence(element n1, element n2, element n3, element n4) {
    this();
    append(n1);
    append(n2);
    append(n3);
    append(n4);
  }
  public overload sequence(element n1, element n2, element n3, element n4, element n5) {
    this();
    append(n1);
    append(n2);
    append(n3);
    append(n4);
    append(n5);
  }
  public overload sequence(element n1,
                  element n2,
		  element n3,
		  element n4,
		  element n5,
		  element n6) {
    this();
    append(n1);
    append(n2);
    append(n3);
    append(n4);
    append(n5);
    append(n6);
  }
  public integer size() {
    return list.size();
  }

  public boolean empty => list.isEmpty();

  public boolean is_not_empty => !empty();

  public element get(integer index) {
    return list.get(index) !> element;
  }
  public void append(element t) {
    list.add(t);
  }
  public void append_all(sequence[element] s) {
    --list.addAll(s.list);
  }
  public sequence subsequence(integer begin, integer end) {
    assert begin >= 0 && end <= size();
    result : sequence[element].new();
    -- fix: inc(i)
    for (var integer i : begin; i < end; i += 1) {
      result.append(get(i));
    }
    return result;
  }
  public sequence subsequence(integer begin) {
    return subsequence(begin, size());
  }

  public void test() {
    if (true) {
      test();
    } else {
      test();
    }
  }
}
