-- Copyright 2014-2021 The Ideal Authors. All rights reserved.
--
-- Use of this source code is governed by a BSD-style
-- license that can be found in the LICENSE file or at
-- https://developers.google.com/open-source/licenses/bsd

one : 1;
two : 2;
--class testing {
--   int xyz: one;
--}
integer #id:three : plus(one, two);
--int four() { return plus(two, two); }
integer double(integer x) { return plus(x, x); }
integer add5(integer y) { return plus(y, 5); }
--println(plus(double(two), four()));
--four();
println(double(double(three)));
println(plus(add5(three), two));
println(7, " ", add5(63));
{ println(one, " ", plus(one, one), " ", one); }
