/*
 * Copyright 2014-2025 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
 */

package ideal.development.policies;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.names.*;
import ideal.development.kinds.*;
import static ideal.development.kinds.type_kinds.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import static ideal.development.flavors.flavor.*;
import ideal.development.notifications.*;
import ideal.development.modifiers.*;
import ideal.development.origins.*;
import ideal.development.declarations.*;
import ideal.development.flags.*;
import ideal.development.actions.*;
import ideal.development.values.*;

public class general_policy extends base_policy {

  public static final general_policy instance = new general_policy();

  @Override
  public signal declare_type(principal_type new_type, declaration_pass pass,
      action_context context) {

    kind the_kind = new_type.get_kind();

    readonly_list<principal_type> adjacent = context.type_graph().adjacent(new_type).elements();
    for (int k = 0; k < adjacent.size(); ++k) {
      type_utilities.prepare(adjacent.get(k), pass);
    }

    declaration new_type_declaration = new_type.get_declaration();

    if (new_type_declaration == null) {
      assert pass == declaration_pass.FLAVOR_PROFILE;
      assert new_type instanceof parametrized_type;
      // or utilities.panic("No declaration for " + new_type);
      parametrized_type ptype = (parametrized_type) new_type;
      declaration master_declaration = ptype.get_master().get_declaration();
      principal_type declared_type = (master_declaration instanceof type_announcement) ?
          ((type_announcement) master_declaration).get_declared_type() :
          ((type_declaration) master_declaration).get_declared_type();
      assert declared_type != new_type : "Got type " + new_type;
      assert declared_type instanceof parametrized_type;
      new_type_declaration = specialize_declaration(ptype,
          (parametrized_type) declared_type, pass, context);
      ptype.set_declaration(new_type_declaration);
    }

    type_declaration the_type_declaration;

    if (new_type_declaration instanceof type_declaration) {
      the_type_declaration = (type_declaration) new_type_declaration;
    } else {
      the_type_declaration = ((type_announcement) new_type_declaration).get_type_declaration();
    }

    the_type_declaration.process_declaration(pass);

    origin pos = the_type_declaration;

    if (pass == declaration_pass.FLAVOR_PROFILE) {
      if (the_kind == procedure_kind &&
          new_type instanceof parametrized_type &&
          new_type.short_name() == common_names.function_name) {
        // TODO: this should be done in type_declaration_analyzer.
        type procedure_type = common_types.procedure_type().bind_parameters(
          ((parametrized_type) new_type).get_parameters()).get_flavored(immutable_flavor);
        action_utilities.process_super_flavors(new_type, null, procedure_type, pos, context);
      }
    } else if (pass == declaration_pass.TYPES_AND_PROMOTIONS) {
      flavor_profile profile;
      if (new_type.has_flavor_profile()) {
        profile = new_type.get_flavor_profile();
      } else {
        utilities.panic("Flavor profile not set in " + new_type);
        return null;
      }

      immutable_list<type_flavor> supported_flavors = profile.supported_flavors();
      for (int i = 0; i < supported_flavors.size(); ++i) {
        type_flavor the_flavor = supported_flavors.get(i);
        immutable_list<type_flavor> superflavors = the_flavor.get_superflavors();
        for (int j = 0; j < superflavors.size(); ++j) {
          type_flavor superflavor = superflavors.get(j);
          if (profile.supports(superflavor)) {
            action_utilities.add_supertype_and_promotion(new_type.get_flavored(the_flavor),
                new_type.get_flavored(superflavor), context, pos);
          }
        }
      }

      readonly_list<declaration> declarations = the_type_declaration.get_signature();
      for (int j = 0; j < declarations.size(); ++j) {
        declaration the_declaration = declarations.get(j);
        if (the_declaration instanceof supertype_declaration) {
          declare_supertype(new_type, (supertype_declaration) the_declaration, context);
        }
      }
    } else if (pass == declaration_pass.METHODS_AND_VARIABLES) {
      // TODO: make this cleaner.
      readonly_list<type> supertypes = action_utilities.get_supertypes(new_type);
      for (int j = 0; j < supertypes.size(); ++j) {
        type supertype = supertypes.get(j);
        type_utilities.prepare(supertype, declaration_pass.METHODS_AND_VARIABLES);
      }

      if (new_type.get_kind().supports_constructors()) {
        context.add(new_type, special_name.NEW, new allocate_action(new_type, pos));
      } else if (new_type.get_kind() == reference_kind &&
                 new_type instanceof parametrized_type) {
        declare_reference((parametrized_type) new_type, context);
      } else if (the_kind == procedure_kind && new_type instanceof parametrized_type) {
        // TODO: describe how IMPLICIT name works, and why it is needed here
        // (to make runtime_util.default_equivalence work)
        type_flavor default_flavor = new_type.get_flavor_profile().default_flavor();
        type flavored_type = new_type.get_flavored(default_flavor);
        context.add(flavored_type, special_name.IMPLICIT_CALL,
            new promotion_action(flavored_type, pos));
      } else if (the_kind == singleton_kind) {
        context.add(new_type, common_names.instance_name,
            new singleton_value(new_type).to_action(pos));
      }
      process_member_declaration(the_type_declaration, context);
    } else {
      utilities.panic("Unknown declaration pass: " + pass);
    }

    return ok_signal.instance;
  }

  public void process_member_declaration(type_declaration the_type_declaration,
      action_context context) {
  }

  @Override
  public void declare_supertype(principal_type new_type,
      supertype_declaration the_supertype_declaration, action_context context) {
    if (the_supertype_declaration.has_errors()) {
      return;
    }
    readonly_list<type> super_types = the_supertype_declaration.super_types();
    for (int i = 0; i < super_types.size(); ++i) {
      type the_supertype = super_types.get(i);
      action_utilities.process_super_flavors(new_type,
          the_supertype_declaration.subtype_flavor(),
          the_supertype, the_supertype_declaration, context);
    }
  }

  private specialization_context make_specialization_context(parametrized_type new_type,
      parametrized_type declared_type) {

    dictionary<master_type, abstract_value> specialization =
        new list_dictionary<master_type, abstract_value>();

    if (new_type.get_kind() != procedure_kind) {
      immutable_list<abstract_value> new_parameters = new_type.get_parameters().the_list;
      immutable_list<abstract_value> declared_parameters = declared_type.get_parameters().the_list;

      // TODO: signal error?
      if (new_parameters.size() != declared_parameters.size()) {
        utilities.panic("new " + new_type + ", declared " + declared_type);
        assert new_parameters.size() == declared_parameters.size();
      }

      for (int i = 0; i < new_parameters.size(); ++i) {
        if (new_parameters.get(i) != declared_parameters.get(i)) {
          master_type declared_param = (master_type) declared_parameters.get(i);
          assert declared_param.get_kind() == type_alias_kind;
          specialization.put(declared_param, new_parameters.get(i));
        }
      }
    }

    return new base_specialization_context(specialization);
  }

  private specialized_type_declaration specialize_declaration(parametrized_type new_type,
      parametrized_type declared_type, declaration_pass pass,
      action_context context) {

    specialization_context spec_context = make_specialization_context(new_type, declared_type);

    return new specialized_type_declaration(new_type, declared_type, spec_context, context);

    //the_type_decl.process_declaration(pass);
  }

  private void declare_reference(parametrized_type new_type, action_context context) {
    declaration the_declaration = new_type.get_declaration();
    assert the_declaration != null;

    immutable_list<abstract_value> params = new_type.get_parameters().the_list;
    // TODO: signal an error
    assert params.size() == 1;
    assert params.first() instanceof type;
    type param = (type) params.first();

    action deref = new dereference_action(param, the_declaration, the_declaration);
    context.add(new_type.get_flavored(readonly_flavor), special_name.PROMOTION, deref);
  }
}
