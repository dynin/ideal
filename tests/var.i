
namespace test {
  interface AI {
    extends deeply_immutable data;
    string field;
  }

  class BI {
    implements AI;
    implement string field;
  }
}

target generate_var: generate_java(test);
