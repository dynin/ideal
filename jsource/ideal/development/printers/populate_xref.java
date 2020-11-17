/*
 * Copyright 2014-2020 Voidhe Ideal Authors. All rights reserved.
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
      construct the_construct = types.get(i);
      @Nullable analyzable the_analyzable = the_analysis_context.get_analyzable(the_construct);
      if (the_analyzable != null) {
        analysis_result result = the_analyzable.analyze();
        if (result instanceof type_action) {
          principal_type the_type = ((type_action) result).get_type().principal();
          the_xref_context.add(the_super_declaration, xref_mode.SUPERTYPE_DECLARATION,
              the_construct);
        }
      }
    }

    return null;
  }

  @Override
  public Void process_type_declaration(type_declaration_construct c) {
    @Nullable analyzable the_declaration = the_analysis_context.get_analyzable(c);
    if (the_declaration instanceof type_declaration) {
      type_declaration the_type_declaration = (type_declaration) the_declaration;
      if (all_types.contains(the_type_declaration.get_declared_type())) {
        return null;
      }
      all_types.add(the_type_declaration.get_declared_type());
      the_xref_context.add(the_type_declaration, xref_mode.DECLARATION, c);
    }
    return process_default(c);
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
