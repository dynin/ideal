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

  public populate_xref(analysis_context the_analysis_context, xref_context the_xref_context) {
    this.the_analysis_context = the_analysis_context;
    this.the_xref_context = the_xref_context;
  }

  @Override
  public Void process_default(construct c) {
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
    return process_default(c);
  }

  @Override
  public Void process_type_declaration(type_declaration_construct c) {
    @Nullable analyzable the_declaration = the_analysis_context.get_analyzable(c);
    if (the_declaration instanceof type_declaration) {
      the_xref_context.add(the_declaration, xref_mode.DECLARATION, c);
    }
    //return process_default(c);
    return null;
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
