#!/bin/sh

TYPES=" \
    analyzable_action \
    block_declaration \
    conditional_analyzer \
    constraint_analyzer \
    declaration_list_analyzer \
    enum_value_analyzer \
    error_signal \
    extension_analyzer \
    flavor_analyzer \
    import_analyzer \
    jump_analyzer \
    list_initializer_analyzer \
    literal_analyzer \
    loop_analyzer \
    parameter_analyzer \
    procedure_declaration \
    resolve_analyzer \
    return_analyzer \
    statement_list_analyzer \
    supertype_declaration \
    target_declaration \
    type_announcement \
    type_declaration \
    type_parameter_declaration \
    variable_declaration"

for t in $TYPES
do
  name=`echo $t | sed s/_declaration// | sed s/_analyzer//`
  echo "    if (the_analyzable instanceof $t) {"
  echo "      return process_$name(($t) the_analyzable);"
  echo "    }"
  echo ""
done

for t in $TYPES
do
  name=`echo $t | sed s/_declaration// | sed s/_analyzer//`
  echo "  public T process_$name($t the_$name) {"
  echo "    return process_default(the_$name);"
  echo "  }"
  echo ""
done
