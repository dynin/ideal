-- Copyright 2014-2022 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://theideal.org/license/

interface A[value T] {
  integer foo();
}

class B[value T] {
  subtypes A[T];
  override integer foo() => 68;
}

class C[value T] {
  subtypes A[T], B[T];
  override integer foo() => 42;
}
