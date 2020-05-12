/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.actions;

import ideal.library.elements.*;
import ideal.runtime.elements.*;
import ideal.runtime.logs.*;
import ideal.development.elements.*;
import ideal.development.declarations.*;
import ideal.development.types.*;
import ideal.development.flavors.*;
import javax.annotation.Nullable;

public class specialized_type_declaration extends debuggable implements type_declaration {

  private final parametrized_type the_type;
  private final parametrized_type main_type;
  private final type_declaration main_declaration;
  private final specialization_context the_specialization_context;
  private final list<declaration> signature;
  private declaration_pass last_pass;

  public specialized_type_declaration(parametrized_type the_type,
      parametrized_type main_type,
      specialization_context the_specialization_context,
      analysis_context the_analysis_context) {
    this.the_type = the_type;
    this.main_type = main_type;
    this.main_declaration = (type_declaration) main_type.get_declaration();
    this.the_specialization_context = the_specialization_context;
    this.signature = new base_list<declaration>();
    last_pass = declaration_pass.NONE;

    assert the_type.get_master() == main_type.get_master();

    // We need this so that code in specialized type can access the static declarations
    // in the master type.
    action_utilities.add_promotion(the_analysis_context, the_type, get_master(), this);
  }

  public master_type get_master() {
    return the_type.get_master();
  }

  @Override
  public kind get_kind() {
    return main_declaration.get_kind();
  }

  @Override
  public action_name short_name() {
    return main_declaration.short_name();
  }

  @Override
  public annotation_set annotations() {
    return main_declaration.annotations();
  }

  @Override
  public principal_type get_declared_type() {
    return the_type;
  }

  @Override
  public principal_type declared_in_type() {
    return main_declaration.declared_in_type();
  }

  @Override
  public void process_declaration(declaration_pass pass) {
    assert pass != null && pass != declaration_pass.NONE;
    if (false) {
      log.debug("Pass: " + pass + ", spec: " + the_specialization_context);
      log.debug("NT: " + the_type + " @" + System.identityHashCode(the_type));
    }

    if (pass.is_before(last_pass) || pass == last_pass) {
      return;
    }

    //main_type.process_declaration(pass);

    if (last_pass.is_before(declaration_pass.TYPES_AND_PROMOTIONS)) {
      last_pass = declaration_pass.TYPES_AND_PROMOTIONS;
      do_process_types_and_promotions();
    }
    if (pass == declaration_pass.METHODS_AND_VARIABLES &&
        last_pass.is_before(declaration_pass.METHODS_AND_VARIABLES)) {
      last_pass = declaration_pass.METHODS_AND_VARIABLES;
      do_process_methods_and_variables();
    }
  }

  private void do_process_types_and_promotions() {
    flavor_profile the_flavor_profile = get_master().get_flavor_profile();

    if (ENABLE_SHALLOW && is_collection_type(the_type)) {
      immutable_list<abstract_value> new_parameters = the_type.get_parameters().internal_access();
      assert new_parameters.size() == 1;
      if (new_parameters.first().type_bound().get_flavor() == flavors.deeply_immutable_flavor) {
        log.debug("*** " + the_type);
        the_flavor_profile = flavor_profiles.combine(the_flavor_profile,
            flavor_profiles.shallow_mutable_profile);
      }
    }

    readonly_list<supertype_declaration> supertypes =
        declaration_util.get_declared_supertypes(main_declaration);
    for (int i = 0; i < supertypes.size(); ++i) {
      supertype_declaration supertype = supertypes.get(i);
      supertype_declaration specialized = supertype.specialize(the_specialization_context,
          the_type);
      append(specialized);
      the_flavor_profile = flavor_profiles.combine(the_flavor_profile,
          action_utilities.get_profile(specialized));
    }

    if (ENABLE_SHALLOW && the_flavor_profile != get_master().get_flavor_profile()) {
      log.debug("!!! " + the_type);
      log.debug(">>> " + the_flavor_profile);
    }

    if (the_type.has_flavor_profile()) {
      // TODO: signal an error if the flavor profile is wrong.
      assert the_type.get_flavor_profile() == the_flavor_profile;
    } else {
      the_type.set_flavor_profile(the_flavor_profile);
    }
  }

  private void do_process_methods_and_variables() {
    readonly_list<procedure_declaration> procedures =
        declaration_util.get_declared_procedures(main_declaration);

    for (int i = 0; i < procedures.size(); ++i) {
      procedure_declaration procedure = procedures.get(i);
      if (specialize_procedure(procedure)) {
        append(procedure.specialize(the_specialization_context, the_type));
      }
    }

    readonly_list<variable_declaration> variables =
        declaration_util.get_declared_variables(main_declaration);
    for (int i = 0; i < variables.size(); ++i) {
      variable_declaration variable = variables.get(i);
      if (specialize_variable(variable)) {
        append(variable.specialize(the_specialization_context, the_type));
      }
    }

    // TODO: this shouldn't be necessary once dependencies are robust.
    immutable_list<principal_type> the_parameters =
        the_type.get_parameters().principals_set().elements();
    for (int i = 0; i < the_parameters.size(); ++i) {
      type_utilities.prepare(the_parameters.get(i), declaration_pass.METHODS_AND_VARIABLES);
    }
  }

  private boolean specialize_procedure(procedure_declaration the_procedure) {
    return the_procedure.get_category() != procedure_category.STATIC && !the_procedure.has_errors();
  }

  private boolean specialize_variable(variable_declaration the_variable) {
    return the_variable.get_category() != variable_category.STATIC && !the_variable.has_errors();
  }

  private void append(declaration the_declaration) {
    signature.append(the_declaration);
  }

  @Override
  public readonly_list<declaration> get_signature() {
    return signature;
  }

  @Override
  public origin deeper_origin() {
    return main_declaration;
  }

  private static boolean ENABLE_SHALLOW = false;

  private static final simple_name list_name = simple_name.make("list");
  private static final simple_name collection_name = simple_name.make("collection");

  private static boolean is_collection_type(principal_type the_type) {
    action_name the_name = the_type.short_name();
    // TODO: set, etc...
    return the_name == collection_name || the_name == list_name;
  }
}
