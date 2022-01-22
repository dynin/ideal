// Autogenerated from development/constructs/construct_visitor.i

package ideal.development.constructs;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.names.*;

public abstract class construct_visitor<return_value> implements value {
  public return_value process(final construct c) {
    if (c instanceof block_construct) {
      return this.process_block(((block_construct) c));
    }
    if (c instanceof constraint_construct) {
      return this.process_constraint(((constraint_construct) c));
    }
    if (c instanceof empty_construct) {
      return this.process_empty(((empty_construct) c));
    }
    if (c instanceof comment_construct) {
      return this.process_comment(((comment_construct) c));
    }
    if (c instanceof extension_construct) {
      return this.process_extension(((extension_construct) c));
    }
    if (c instanceof flavor_construct) {
      return this.process_flavor(((flavor_construct) c));
    }
    if (c instanceof procedure_construct) {
      return this.process_procedure(((procedure_construct) c));
    }
    if (c instanceof list_construct) {
      return this.process_list(((list_construct) c));
    }
    if (c instanceof name_construct) {
      return this.process_name(((name_construct) c));
    }
    if (c instanceof conditional_construct) {
      return this.process_conditional(((conditional_construct) c));
    }
    if (c instanceof import_construct) {
      return this.process_import(((import_construct) c));
    }
    if (c instanceof modifier_construct) {
      return this.process_modifier(((modifier_construct) c));
    }
    if (c instanceof operator_construct) {
      return this.process_operator(((operator_construct) c));
    }
    if (c instanceof parameter_construct) {
      return this.process_parameter(((parameter_construct) c));
    }
    if (c instanceof resolve_construct) {
      return this.process_resolve(((resolve_construct) c));
    }
    if (c instanceof return_construct) {
      return this.process_return(((return_construct) c));
    }
    if (c instanceof supertype_construct) {
      return this.process_supertype(((supertype_construct) c));
    }
    if (c instanceof type_declaration_construct) {
      return this.process_type_declaration(((type_declaration_construct) c));
    }
    if (c instanceof type_announcement_construct) {
      return this.process_type_announcement(((type_announcement_construct) c));
    }
    if (c instanceof literal_construct) {
      return this.process_literal(((literal_construct) c));
    }
    if (c instanceof variable_construct) {
      return this.process_variable(((variable_construct) c));
    }
    if (c instanceof loop_construct) {
      return this.process_loop(((loop_construct) c));
    }
    if (c instanceof jump_construct) {
      return this.process_jump(((jump_construct) c));
    }
    if (c instanceof switch_construct) {
      return this.process_switch(((switch_construct) c));
    }
    if (c instanceof case_clause_construct) {
      return this.process_case_clause(((case_clause_construct) c));
    }
    if (c instanceof grammar_construct) {
      return this.process_grammar(((grammar_construct) c));
    }
    if (c == null) {
      utilities.panic(new base_string("null construct in visitor"));
      return null;
    }
    {
      utilities.panic(ideal.machine.elements.runtime_util.concatenate(new base_string("unknown construct type in construct_visitor.visit(): "), c));
      return null;
    }
  }
  public abstract return_value process_default(construct c);
  public return_value process_block(final block_construct c) {
    return this.process_default(c);
  }
  public return_value process_conditional(final conditional_construct c) {
    return this.process_default(c);
  }
  public return_value process_constraint(final constraint_construct c) {
    return this.process_default(c);
  }
  public return_value process_empty(final empty_construct c) {
    return this.process_default(c);
  }
  public return_value process_comment(final comment_construct c) {
    return this.process_default(c);
  }
  public abstract return_value process_extension(extension_construct c);
  public return_value process_flavor(final flavor_construct c) {
    return this.process_default(c);
  }
  public return_value process_procedure(final procedure_construct c) {
    return this.process_default(c);
  }
  public return_value process_list(final list_construct c) {
    return this.process_default(c);
  }
  public return_value process_name(final name_construct c) {
    return this.process_default(c);
  }
  public return_value process_import(final import_construct c) {
    return this.process_default(c);
  }
  public return_value process_modifier(final modifier_construct c) {
    return this.process_default(c);
  }
  public return_value process_operator(final operator_construct c) {
    return this.process_default(c);
  }
  public return_value process_parameter(final parameter_construct c) {
    return this.process_default(c);
  }
  public return_value process_resolve(final resolve_construct c) {
    return this.process_default(c);
  }
  public return_value process_return(final return_construct c) {
    return this.process_default(c);
  }
  public return_value process_supertype(final supertype_construct c) {
    return this.process_default(c);
  }
  public return_value process_type_declaration(final type_declaration_construct c) {
    return this.process_default(c);
  }
  public return_value process_type_announcement(final type_announcement_construct c) {
    return this.process_default(c);
  }
  public return_value process_literal(final literal_construct c) {
    return this.process_default(c);
  }
  public return_value process_variable(final variable_construct c) {
    return this.process_default(c);
  }
  public return_value process_loop(final loop_construct c) {
    return this.process_default(c);
  }
  public return_value process_jump(final jump_construct c) {
    return this.process_default(c);
  }
  public return_value process_switch(final switch_construct c) {
    return this.process_default(c);
  }
  public return_value process_case_clause(final case_clause_construct c) {
    return this.process_default(c);
  }
  public return_value process_grammar(final grammar_construct c) {
    return this.process_default(c);
  }
  public construct_visitor() { }
}
