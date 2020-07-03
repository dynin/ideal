
class testcache {
  private var string or null generated_foo_cache;
  string foo() {
    var result : generated_foo_cache;
    if (result is null) {
      result = generated_foo_compute();
      generated_foo_cache = result;
      assert result is string;
    } else {
      result = "foobar";
      assert result is string;
    }
    return result;
  }
  private string generated_foo_compute() {
    return perform_expensive_operation();
  }
  testcache() {
    generated_foo_cache = missing.instance;
  }
}

var integer index : 0;

string perform_expensive_operation() {
  index += 1;
  return "$$$" ++ index;
}

instance : testcache.new();

println(instance.foo());
