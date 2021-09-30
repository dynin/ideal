one : 1;
two : 2;
class cool {
  static var integer sfield;
  static var integer sfield2;
  var integer field;
  cool() {
    #id:field = 68;
    sfield = 680;
  }
}
cool.sfield2 = 4242;
cool x : cool.new();
println(cool.sfield);
println(x.field);
println(cool.sfield2);

integer three : plus(one, two);
integer four => plus(2, 2);
integer double(integer x) => plus(x, x);
integer add5(integer y) => plus(y, 5);
integer constant() => 34;
println(plus(double(two), four()));
--four();
constant();
println(((6868)));
--json: [a: 'foo', b: 5, c: [1, 2, 3]];
--Checking paramteres with initializers.
println('foo' : 5 * 5);
println(constant());
println(double(double(three)));
println(plus(add5(three), two));
println(7, " ", add5(63));
{ println(one, " ", plus(one, one), " ", one); }
please println("hello " ++ "world");
string triple(string s) { return s ++ s ++ s; }
println(triple("Hurrah! "));
string tt()
  #(h1 "Hello " (a (name "http://google.com") "world!<>&"))
string tt2()
  #(h1 "Hello " (h2 "world!"))
println(tt());
class typefoo {
  static string baz : "Hey baz.";
  static string method() {
    return "xxx" ++ baz;
  }
}
println(typefoo.method());

class new_type {
  implements value;

  string field;
  overload new_type() { }
  overload new_type(string val) => field = val;
  string get_state() => "state: " ++ field;
}
println(new_type.new("hey!").get_state());
elements : [ 42, 68, 18 ];
println("size: " ++ elements.size);
println("first: " ++ elements[0] ++ " second: " ++ elements[1]);

for (element : elements) {
  println("  element: " ++ element);
}

class sub_type {
  extends new_type;
  sub_type(string v2) {
    --super();
  }
  void some_method(value arg) {
    (arg !> new_type).field;
  }
}
new_type val : sub_type.new("hahaha");
println((val is sub_type) ? "yeah" : "nay");
void test_loop() {
  loop {
    println("Hey.");
  }
}
-- test_loop();
void test_assignment() {
  var nonnegative non : 1;
  non += 2;
  var integer may : 3;
  may += 2;
  integer test : 4;
  assert test is nonnegative;
  nonnegative narrowed : test;

  string or null test_string : "123";

  if (test_string is string) {
    println(test_string.size);
  }

  if (test_string is null) {
    for (; false; 3) { }

    println("null...");
  } else {
    println(test_string.size);
  }
}
class A {
  A() {}
  void method(integer value) {
    println(value ++ ": A");
  }
}
class B {
  extends A;
  B() {}
  override void method(integer value) {
    println(value ++ ": B");
  }
}
void test_and() {
  string or null value : "foo";
  if (value is null || value.size < 5) {
    -- TODO: should be redundant
    assert value is_not null;
    string svar : value;
    println(value);
  }

  string sval : "data";
  --deeply_immutable data dcast : sval .> deeply_immutable data;
  dcast : sval .> deeply_immutable data;
  string ocast : dcast !> string;
  println(ocast);
}

A.new().method(1);
B.new().method(2);
A foo : B.new();
foo.method(3);
test_and();

class X {
  var any value foo_value;

  void changestate() {
    foo_value = true;
  }

  var string get_string => "foo";

  void test_types() {
    b : X.new();
    foo_value = true;
    this.foo_value = "abc";
    string foo_string : foo_value;
    println(get_string);
  }
}
