/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
import ideal.development.analyzers.*;

import javax.annotation.Nullable;

public class xref_context extends debuggable {
  private final static int num_modes = xref_mode.values().length;

  public final naming_rewriter the_naming_rewriter;
  public final mapping_visitor the_mapping_visitor;
  private final list<type_declaration_construct> the_output_declarations;
  private final dictionary<master_type, naming_strategy> output_types;
  private final set<master_type> skip_types;
  private final dictionary<origin, list<origin>>[] mapping;

  public xref_context(naming_rewriter the_naming_rewriter) {
    this.the_naming_rewriter = the_naming_rewriter;
    this.the_mapping_visitor = new mapping_visitor();
    this.the_output_declarations = new base_list<type_declaration_construct>();
    this.output_types = new hash_dictionary<master_type, naming_strategy>();
    this.skip_types = new hash_set<master_type>();
    mapping = new dictionary[num_modes * 2];
    for (int i = 0; i < mapping.length; ++i) {
      mapping[i] = new list_dictionary<origin, list<origin>>();
    }
  }

  public void process_type(principal_type the_type) {
    the_mapping_visitor.visit(the_type.get_declaration());
  }

  public @Nullable analyzable get_analyzable(construct the_construct) {
    return the_mapping_visitor.get_analyzable(the_construct);
  }

  public void put_analyzable(construct the_construct, analyzable the_analyzable) {
    the_mapping_visitor.put_analyzable(the_construct, the_analyzable);
  }

  public boolean is_ignorable(construct the_construct) {
    return the_mapping_visitor.is_ignorable(the_construct);
  }

  private master_type normalize(principal_type the_principal_type) {
    if (the_principal_type instanceof master_type) {
      return (master_type) the_principal_type;
    } else {
      return ((parametrized_type) the_principal_type).get_master();
    }
  }

  public void add_output_declaration(principal_type the_type,
      type_declaration_construct the_declaration_construct) {
    if (has_output_type(the_type)) {
      return;
    }
    output_types.put(normalize(the_type), new naming_strategy(the_type, this));

    type_declaration the_type_declaration = declaration_util.to_type_declaration(
        get_analyzable(the_declaration_construct));
    assert the_type_declaration != null;
    assert the_type_declaration.get_declared_type() == the_type;
    the_output_declarations.append(the_declaration_construct);
  }

  // This is used in -pretty-print.
  public void add_named_output(principal_type the_type, readonly_list<simple_name> full_names) {
    if (has_output_type(the_type)) {
      return;
    }
    output_types.put(normalize(the_type), new naming_strategy(full_names, the_type, this));
  }

  public void add_skip_type(principal_type the_type) {
    skip_types.add(normalize(the_type));
  }

  public boolean is_skip_type(principal_type the_type) {
    return skip_types.contains(normalize(the_type));
  }

  public immutable_list<type_declaration_construct> output_constructs() {
    return the_output_declarations.frozen_copy();
  }

  public immutable_list<type_declaration> output_declarations() {
    // TODO: use list.map
    list<type_declaration> declarations = new base_list<type_declaration>();

    for (int i = 0; i < the_output_declarations.size(); ++i) {
      type_declaration_construct the_declaration_construct = the_output_declarations.get(i);
      type_declaration the_type_declaration = declaration_util.to_type_declaration(
          get_analyzable(the_declaration_construct));
      assert the_type_declaration != null;
      declarations.append(the_type_declaration);
    }

    return declarations.frozen_copy();
  }

  public boolean has_output_type(principal_type the_principal_type) {
    return output_types.contains_key(normalize(the_principal_type));
  }

  public naming_strategy get_naming_strategy(principal_type the_principal_type) {
    naming_strategy result = output_types.get(normalize(the_principal_type));
    assert result != null;
    return result;
  }

  public void add_successor(type_declaration source, type_declaration target) {
    //System.out.println("SRC " + source + " *SUCCESOR* TGT " + target);
    add_mapping(source, xref_mode.SUCCESSOR.ordinal(), target);
    add_mapping(target, num_modes + xref_mode.SUCCESSOR.ordinal(), source);
  }

  public void add(declaration source, xref_mode the_xref_mode, construct target) {
    //System.out.println("SRC " + source + " M " + the_xref_mode + " TGT " + target);
    add_mapping(source, the_xref_mode.ordinal(), target);
    declaration the_declaration = origin_to_declaration(target);
    if (the_declaration != null) {
      add_mapping(the_declaration, num_modes + the_xref_mode.ordinal(), source);
    }
  }

  public void add_action(declaration source, xref_mode the_xref_mode, action target) {
    //System.out.println("SRC " + source + " M " + the_xref_mode + " TGT " + target);
    add_mapping(source, the_xref_mode.ordinal(), target);
    declaration the_declaration = origin_to_declaration(target);
    if (the_declaration != null) {
      add_mapping(the_declaration, num_modes + the_xref_mode.ordinal(), source);
    }
  }

  public @Nullable declaration origin_to_declaration(origin the_origin) {
    if (the_origin instanceof declaration) {
      return (declaration) the_origin;
    } else if (the_origin instanceof construct) {
      @Nullable analyzable the_analyzable = get_analyzable((construct) the_origin);

      if (the_analyzable == null) {
        return null;
      }

      if (the_analyzable instanceof declaration) {
        return (declaration) the_analyzable;
      } else if (the_analyzable instanceof declaration_extension) {
        return ((declaration_extension) the_analyzable).get_declaration();
      } else {
        return declaration_util.get_declaration(the_analyzable.analyze());
      }
    } else {
      return declaration_util.get_declaration(the_origin);
    }
  }

  public @Nullable type_declaration get_successor(@Nullable declaration source) {
    return get_mapping(source, xref_mode.SUCCESSOR.ordinal());
  }

  public @Nullable type_declaration get_predecessor(@Nullable declaration target) {
    return get_mapping(target, num_modes + xref_mode.SUCCESSOR.ordinal());
  }

  private @Nullable type_declaration get_mapping(@Nullable origin source, int slot) {
    @Nullable readonly_list<origin> origins = get_mapping_list(source, slot);
    if (origins == null) {
      return null;
    }
    assert origins.size() <= 1;
    if (origins.is_empty()) {
      return null;
    } else {
      return (type_declaration) origins.first();
    }
  }

  public @Nullable readonly_list<origin> get_targets(@Nullable origin source,
      xref_mode the_xref_mode) {
    return get_mapping_list(source, the_xref_mode.ordinal());
  }

  public @Nullable readonly_list<origin> get_sources(@Nullable origin target,
      xref_mode the_xref_mode) {
    return get_mapping_list(target, num_modes + the_xref_mode.ordinal());
  }

  private void add_mapping(origin source, int slot, origin target) {
    dictionary<origin, list<origin>> the_dictionary = mapping[slot];
    list<origin> the_list = the_dictionary.get(source);
    if (the_list == null) {
      the_list = new base_list<origin>();
      the_dictionary.put(source, the_list);
    }
    the_list.append(target);
  }

  private @Nullable readonly_list<origin> get_mapping_list(@Nullable origin source,
      int slot) {
    if (source == null) {
      return null;
    }
    dictionary<origin, list<origin>> the_dictionary = mapping[slot];
    list<origin> the_list = the_dictionary.get(source);
    if (the_list == null) {
      return null;
    }
    return the_list;
  }

  public @Nullable principal_type get_enclosing_type(construct the_construct) {
    assert the_construct != null;
    @Nullable analyzable the_analyzable = get_analyzable(the_construct);
    if (the_analyzable == null) {
      return null;
    }

    if (the_analyzable instanceof declaration && ((declaration) the_analyzable).has_errors()) {
      return null;
    }

    principal_type result;
    if (the_analyzable instanceof type_announcement) {
      result = ((type_announcement) the_analyzable).declared_in_type();
    } else if (the_analyzable instanceof type_declaration) {
      result = ((type_declaration) the_analyzable).get_declared_type();
    } else {
      result = ((base_analyzer) the_analyzable).parent();
    }
    return get_output_type(result);
  }

  public @Nullable principal_type get_output_type(@Nullable principal_type the_type) {
    while (the_type != null) {
      if (has_output_type(the_type) || is_skip_type(the_type)) {
        return the_type;
      }
      the_type = the_type.get_parent();
    }
    return null;
  }
}
