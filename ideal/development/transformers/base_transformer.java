/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.transformers;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.constructs.*;
import ideal.development.notifications.*;
import ideal.development.components.*;

import javax.annotation.Nullable;

public class base_transformer extends construct_visitor<Object>
    implements transformer {

  public construct transform(construct c) {
    if (c == null) {
      return null;
    }

    construct new_construct = (construct) process(c);
    return new_construct;
  }

  protected final annotation_construct transform(annotation_construct mod) {
    return (annotation_construct) transform((construct) mod);
  }

  public list<construct> transform1(construct c) {
    return transform(new base_list<construct>(c));
  }

  public list<construct> transform(@Nullable readonly_list<? extends construct> constructs) {
    if (constructs == null) {
      return null;
    }

    list<construct> result = new base_list<construct>();
    for (int i = 0; i < constructs.size(); ++i) {
      if (constructs.get(i) != null) {
	Object transformed = process(constructs.get(i));
	if (transformed instanceof construct) {
	  result.append((construct) transformed);
	} else if (transformed instanceof readonly_list/*<construct>*/) {
	  result.append_all((readonly_list<construct>) transformed);
	} else if (transformed == null) {
	  // nothing
	} else {
	  utilities.panic("Unknown result of transform " + transformed);
	}
      }
    }
    return result;
  }

  // _m to get around "have the same erasure" error
  protected list<annotation_construct> transform_a(readonly_list<annotation_construct> seq,
      position source) {
    list<annotation_construct> result = new base_list<annotation_construct>();
    for (int i = 0; i < seq.size(); ++i) {
      result.append(transform(seq.get(i)));
    }
    return result;
  }

  @Override
  public Object process_default(construct c) {
    utilities.panic("base_transform.process_default()");
    return null;
  }

  @Override
  public Object process_block(block_construct c) {
    return new block_construct(transform_a(c.annotations, c),
      transform(c.body),
      c);
  }

  @Override
  public Object process_conditional(conditional_construct c) {
    return new conditional_construct(transform(c.cond_expr),
      transform(c.then_expr),
      transform(c.else_expr),
      c.is_statement,
      c);
  }

  @Override
  public Object process_constraint(constraint_construct c) {
    return new constraint_construct(transform(c.expr), c);
  }

  @Override
  public Object process_empty(empty_construct c) {
    return c;
  }

  @Override
  public Object process_extension(extension_construct c) {
    return c.transform(this);
  }

  @Override
  public Object process_procedure(procedure_construct c) {
    return new procedure_construct(transform_a(c.annotations, c),
      transform(c.ret),
      c.name,
      process_list(c.parameters),
      transform_a(c.post_annotations, c),
      transform(c.body),
      c);
  }

  @Override
  public @Nullable list_construct process_list(@Nullable list_construct c) {
    if (c == null) {
      return null;
    } else {
      return new list_construct(transform(c.elements), c.grouping, c);
    }
  }

  @Override
  public Object process_name(name_construct c) {
    return c;
  }

  @Override
  public Object process_import(import_construct c) {
    return new import_construct(transform_a(c.annotations, c),
      transform(c.type),
      c);
  }

  @Override
  public Object process_flavor(flavor_construct c) {
    return new flavor_construct(c.flavor,
      transform(c.expr),
      c);
  }

  @Override
  public Object process_modifier(modifier_construct c) {
    return c;
  }

  @Override
  public Object process_operator(operator_construct c) {
    return new operator_construct(c.the_operator, transform(c.arguments), c);
  }

  @Override
  public Object process_parameter(parameter_construct c) {
    return new parameter_construct(transform(c.main), process_list(c.parameters), c);
  }

  @Override
  public Object process_resolve(resolve_construct c) {
    return new resolve_construct(transform(c.qualifier), transform(c.name), c);
  }

  @Override
  public Object process_return(return_construct c) {
    return new return_construct(transform(c.the_expression), c);
  }

  @Override
  public Object process_supertype(supertype_construct c) {
    return new supertype_construct(c.kind, transform(c.types), c);
  }

  @Override
  public Object process_type_declaration(type_declaration_construct c) {
    return new type_declaration_construct(transform_a(c.annotations, c),
      c.kind,
      c.name,
      process_list(c.parameters),
      transform(c.body),
      c);
  }

  @Override
  public Object process_type_announcement(type_announcement_construct c) {
    return new type_announcement_construct(transform_a(c.annotations, c),
      c.kind,
      c.name,
      c);
  }

  @Override
  public Object process_literal(literal_construct c) {
    return c;
  }

  @Override
  public Object process_variable(variable_construct c) {
    return new variable_construct(transform_a(c.annotations, c),
      transform(c.type),
      c.name,
      transform_a(c.post_annotations, c),
      transform(c.init),
      c);
  }

  @Override
  public Object process_loop(loop_construct c) {
    return new loop_construct(transform(c.body), c);
  }

  @Override
  public Object process_jump(jump_construct c) {
    return c;
  }

  @Override
  public Object process_comment(comment_construct the_construct) {
    return the_construct;
  }
}
