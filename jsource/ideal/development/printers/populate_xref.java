/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.printers;

import ideal.library.elements.*;
import ideal.library.resources.*;
import ideal.library.texts.*;
import ideal.library.channels.*;
import ideal.runtime.elements.*;
import ideal.runtime.reflections.*;
import ideal.runtime.logs.*;
import ideal.runtime.texts.*;
import ideal.runtime.resources.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.comments.*;
import ideal.development.constructs.*;
import ideal.development.declarations.*;
import ideal.development.actions.*;
import ideal.development.types.*;
import ideal.development.values.*;

import javax.annotation.Nullable;

public class populate_xref extends construct_visitor<Void> implements value {

  private final analysis_context the_analysis_context;
  private final xref_context the_xref_context;
  private final set<principal_type> all_types;

  public populate_xref(analysis_context the_analysis_context, xref_context the_xref_context) {
    this.the_analysis_context = the_analysis_context;
    this.the_xref_context = the_xref_context;
    this.all_types = new hash_set<principal_type>();
  }

  @Override
  public Void process_default(construct c) {
    process_construct_list(c.children());
    return null;
  }

  public void process_construct_list(readonly_list<construct> constructs) {
    for (int i = 0; i < constructs.size(); ++i) {
      process(constructs.get(i));
    }
  }

  @Override
  public Void process_block(block_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_conditional(conditional_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_constraint(constraint_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_empty(empty_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_comment(comment_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_extension(extension_construct c) {
    process_construct_list(c.children());
    return null;
  }

  @Override
  public Void process_flavor(flavor_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_procedure(procedure_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_list(list_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_name(name_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_import(import_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_modifier(modifier_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_operator(operator_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_parameter(parameter_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_resolve(resolve_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_return(return_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_supertype(supertype_construct c) {
    @Nullable declaration the_super_declaration =
        (declaration) the_analysis_context.get_analyzable(c);
    assert the_super_declaration instanceof type_declaration;
    readonly_list<construct> types = c.types;
    for (int i = 0; i < types.size(); ++i) {
      @Nullable name_construct the_construct = xref_context.unwrap_name(types.get(i));
      if (the_construct == null) {
        continue;
      }
      @Nullable analyzable the_analyzable = the_analysis_context.get_analyzable(the_construct);
      if (the_analyzable != null) {
        analysis_result result = the_analyzable.analyze();
        if (result instanceof type_action) {
          the_xref_context.add(the_super_declaration, xref_mode.DIRECT_SUPERTYPE,
              the_construct);
        }
      }
    }

    return null;
  }

  @Override
  public Void process_type_declaration(type_declaration_construct c) {
    @Nullable analyzable the_declaration = the_analysis_context.get_analyzable(c);
    if (!(the_declaration instanceof type_declaration)) {
      utilities.panic("Type declaration expected, got "+  the_declaration);
    }

    type_declaration the_type_declaration = (type_declaration) the_declaration;
    if (all_types.contains(the_type_declaration.get_declared_type())) {
      return null;
    }

    all_types.add(the_type_declaration.get_declared_type());
    the_xref_context.add(the_type_declaration, xref_mode.TYPE_DECLARATION, c);
    process_default(c);

    set<principal_type> visited_types = new hash_set<principal_type>();
    readonly_list<origin> super_types = the_xref_context.get_targets(the_type_declaration,
        xref_mode.DIRECT_SUPERTYPE);
    list<type_declaration> super_declarations = new base_list<type_declaration>();
    if (super_types != null) {
      for (int i = 0; i < super_types.size(); ++i) {
        declaration super_declaration = the_xref_context.origin_to_declaration(super_types.get(i));
        if (super_declaration instanceof type_declaration) {
          super_declarations.append((type_declaration) super_declaration);
          principal_type supertype = ((type_declaration) super_declaration).get_declared_type();
          visited_types.add(supertype);
        } else {
          utilities.panic("Super declaration " + super_declaration);
        }
      }
    }

    add_supertypes(the_type_declaration, the_type_declaration, visited_types);
    for (int i = 0; i < super_declarations.size(); ++i) {
      add_supertypes(the_type_declaration, super_declarations.get(i), visited_types);
    }

    return null;
  }

  private void add_supertypes(type_declaration documenting_declaration,
      type_declaration the_type_declaration, set<principal_type> visited_types) {

    readonly_list<declaration> signature = the_type_declaration.get_signature();
    for (int i = 0; i < signature.size(); ++i) {
      declaration member = signature.get(i);
      if (member instanceof supertype_declaration) {
        type super_type = ((supertype_declaration) member).get_supertype();
        principal_type super_principal = super_type.principal();
        if (visited_types.contains(super_principal)) {
          continue;
        }
        visited_types.add(super_principal);
        type_declaration super_declaration = (type_declaration) super_principal.get_declaration();
        action super_action = super_type.to_action(super_declaration);
        the_xref_context.add_action(documenting_declaration, xref_mode.INDIRECT_SUPERTYPE,
            super_action);
        add_supertypes(documenting_declaration, super_declaration, visited_types);
      }
    }
  }

  @Override
  public Void process_type_announcement(type_announcement_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_literal(literal_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_variable(variable_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_loop(loop_construct c) {
    return process_default(c);
  }

  @Override
  public Void process_jump(jump_construct c) {
    return process_default(c);
  }
}
