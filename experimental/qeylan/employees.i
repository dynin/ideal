datastore human_resources {
  datatype address {
    string street;
    string city;
    string zip;
    string country;

    -- Pure function.  String interpolation syntax is not finalized.
    string to_string() pure => "\(street) \(city) \(zip) \(country)";
  }

  -- Employee datatype.  Note that objects/datatypes have an identity
  datatype employee {
    string first;
    string last;
    the address;
    gregorian_date hire_date;
    the department;

    #index (last, first);
  }

  datatype department {
    number: nonnegative;
    string: name;

    boolean in_this_deparment(the employee) pure => the_employee.the_deparment == this;
    readonly set[employee] employees pure => all_employees.filter(in_this_deparment);
  }

  set[employee] all_employees;
  set[depatment] all_depatments;

  -- Simple reusable query, syntax sugar over function
  -- Fallback to Qeylan for complex queries.
  readonly set[employee] employees_by_city() => #qeylan {
    from all_depatments
    when employees.join_date in dates
    by  city: employees.address.city
    get count: count(),
      employees: nest (employees.{first, last})
    when count > 0
  }
}

void populate_datastore(the human_resources) {
  depatment engineering = department.new(1, "Engineering");
  the_human_resources.all_depatments.add(engineering);

  the_address : address.new("1 Main Street", "Springfield", "12345", "USA");

  the_human_resources.all_employees.add(employee.new("Bob", "Anderson", the_address,
    gregorian_date.today(), engineering);
  the_human_resources.all_employees.add(employee.new("Alice", "Smith", the_address,
    gregorian_date.today(), engineering);
}

void main() {
  -- Select a datastore based on the environment (test/inmemory, production, etc.)
  the human_resources : human_resources.instance;

  populate_datastore(the_human_resources);

  for (employee : the_human_resources.employees_by_city()) {
    log.info(employee);
  }
}
