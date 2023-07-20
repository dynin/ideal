service greet_service {
  integer counter : 0;

  string greet(string name) {
    return "Hello, " ++ name ++ ": number " ++ counter;
  }

  void merge(greet_service left, greet_service right, greet_service root) {
    this.counter = left.counter + right.counter - root.counter;
  }
}

main() {
  greeter : greet_service.locate;
  greeting : greeter.greet("Nicolas");
  label : label_view.new(greeting, bold_style);
  app : application_view(label, app_style);
  run_app(app);
}

bold_style : ...
app_style : ...
