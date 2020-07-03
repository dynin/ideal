
class testcache {
  var integer index;
  private var string or null generated_foo_cache;

  string foo() {
    var result : generated_foo_cache;
    if (result is null) {
      result = generated_foo_compute();
      generated_foo_cache = result;
    }
    return result;
  }

  private string generated_foo_compute() {
    return perform_expensive_operation();
  }

  testcache() {
    index = 0;
    generated_foo_cache = missing.instance;
  }

  string bar() {
    return perform_expensive_operation();
  }

  string perform_expensive_operation() {
    index += 1;
    return "$$$" ++ index;
  }
}


instance : testcache.new();

println(instance.bar());
println(instance.bar());
println(instance.foo());
println(instance.foo());
