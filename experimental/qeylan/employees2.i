datastore human_resources:
    struct address:
        street: string
        city: string
        zip: string
        country: string

        get to_string:
            "{street} {city} {zip} {country}"

    datatype employee:
        first: string
        last: string
        the_address: address
        hire_date: date
        the_department: department one_to_many the_employees

        #index(last, first)

    datatype department:
        number: nonnegative
        name: string
        -- This can be inferred
        the_employees: set[employee] many_to_one the_department

    -- This can be inferred
    set[employee] employees
    set[depatment] depatments

    do employees_by_city() set[employee] pure:
        from depatments
        when employees.join_date in dates
        by  city: employees.address.city
        get count: count(),
            employees: nest (employees.{first, last})
        when count > 0

do populate_datastore(db: human_resources):
    with:
        let engineering = insert db.departments set 1, "Engineering"
        let the_address = address {"1 Main Street", "Springfield", "12345", "USA"}
    from [
        {"Bob", "Anderson", the_address, today(), engineering},
        {"Alice", "Smith", the_address,  today(), engineering},
    ] insert db.employees

do main(env: Env):
    -- Connect figures our the default instance of the datastore and loads schema
    var db = human_resources.connect(env, "http://localhost/inmemory)

    populate_datastore(db)

    let read_db = db as readonly human_resources
    from employee : read_db.employees_by_city do:
        log.info(employee)
