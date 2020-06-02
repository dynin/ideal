/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://developers.google.com/open-source/licenses/bsd
 */

package ideal.development.tools;

import ideal.library.elements.*;
import javax.annotation.Nullable;
import ideal.library.channels.*;
import ideal.library.texts.*;
import ideal.library.resources.*;
import ideal.runtime.elements.*;
import ideal.runtime.channels.*;
import ideal.runtime.resources.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;

import ideal.development.elements.*;
import ideal.development.actions.*;
import ideal.development.scanners.*;
import ideal.development.kinds.*;
import ideal.development.types.*;
import ideal.development.values.*;
import ideal.development.constructs.*;
import ideal.development.names.*;
import ideal.development.analyzers.*;
import ideal.development.parsers.*;
import ideal.development.transformers.*;
import ideal.development.printers.*;
import ideal.development.documenters.*;
import ideal.development.flavors.*;
import ideal.development.notifications.*;
import ideal.development.declarations.*;
import ideal.development.functions.*;
import ideal.development.transformers.*;
import ideal.development.targets.*;

public class create_manager implements target_manager, type_bootstrapper {

  public final base_semantics language;
  public final principal_type root;
  private final resource_catalog top_catalog;
  public final analysis_context bootstrap_context;
  private final origin root_origin;
  private output_counter<notification> notifications;
  private @Nullable resource_catalog output_catalog;

  public create_manager(resource_catalog top_catalog) {
    language = new base_semantics();
    root = core_types.root_type();
    this.top_catalog = top_catalog;
    bootstrap_context = new create_analysis_context(this, language);
    root_origin = semantics.BUILTIN_POSITION; // TODO: use resource id as origin
    set_notification_handler((output<notification>) (output) log.log_output);
  }

  public common_library library() {
    return language.library();
  }

  public void set_notification_handler(output<notification> handler) {
    notifications = new output_counter<notification>(handler);
    notification_context.set(notifications);
  }

  @Override
  public boolean has_errors() {
    return notifications.get_count() > 0;
  }

  @Override
  public resource_catalog top_catalog() {
    return top_catalog;
  }

  public @Nullable resource_catalog output_catalog() {
    return output_catalog;
  }

  public void set_output_catalog(resource_catalog output_catalog) {
    assert this.output_catalog == null;
    assert output_catalog != null;
    this.output_catalog = output_catalog;
  }

  public analysis_context get_analysis_context() {
    return bootstrap_context;
  }

  public execution_context new_execution_context() {
    return new base_execution_context(root.short_name());
  }

  public principal_type new_block(string name, analysis_context context) {
    master_type result = new master_type(type_kinds.block_kind, flavor_profiles.nameonly_profile,
        new special_name(name, new base_string("create_manager")), root, context, null);
    // TODO: create a declaration and call result.process_declaration();
    action_utilities.add_promotion(context, result, root, root_origin);
    return result;
  }

  private source_content load_source(resource_catalog parent_catalog, string filename) {
    resource_identifier source_id = parent_catalog.resolve(filename, base_extension.IDEAL_SOURCE);
    create_util.progress_loading(source_id);
    return new source_content(source_id);
  }

  public list<construct> parse(source_content source) {
    readonly_list<token> tokens = new common_scanner().scan(source);

    tokens = documenter_filter.transform(tokens);

    return loader.parse(tokens);
  }

  public list<construct> process_source(source_content source, principal_type parent,
      analysis_context context) {
    list<construct> constructs = parse(source);
    if (constructs != null) {
      check(new declaration_list_analyzer(constructs, parent, context, root_origin));
    }
    return constructs;
  }

  public void process_bootstrap(boolean load_library) {
    // TODO: resolve interdependency.
    process_type_operators();

    principal_type elements = library().elements_package();
    bootstrap_type(elements);
    bootstrap_type(library().operators_package());

    process_bootstrap_ops(bootstrap_context);

    java_library.bootstrap_on_demand(this);

    if (load_library) {
      action_utilities.add_promotion(bootstrap_context, root, elements, root_origin);
      test_library.init(bootstrap_context, root);
    }
  }

  public void process_type_operators() {
    principal_type operators = library().operators_package();
    type_union_op the_op = new type_union_op();
    // TODO: remove--this is now handled in common_library init.
    // core_types.union_master_type().set_context(bootstrap_context);
    bootstrap_context.add(operators, the_op.name(), the_op.to_action(root_origin));
  }

  private string to_resource_name(action_name name) {
    assert name instanceof simple_name;
    return name.to_string();
  }

  @Override
  public void bootstrap_type(principal_type the_type) {
    assert the_type.get_declaration() == null;

    resource_catalog source_catalog;
    if (the_type.get_parent() == library().ideal_namespace()) {
      source_catalog = top_catalog.resolve(to_resource_name(the_type.short_name())).
          access_catalog();
    } else {
      source_catalog = top_catalog.resolve(to_resource_name(the_type.get_parent().short_name())).
          access_catalog();
    }

    source_content type_source = load_source(source_catalog,
        to_resource_name(the_type.short_name()));
    list<construct> constructs = parse(type_source);
    assert constructs != null;

    // TODO: gracefully handle errors.
    assert constructs.size() == 1;
    type_declaration_construct the_declaration = (type_declaration_construct) constructs.first();
    check(new type_declaration_analyzer(the_declaration, the_type.get_parent(), bootstrap_context));
    assert the_type.get_declaration() != null;
    assert bootstrap_context.get_analyzable(the_declaration) != null;

    if (has_errors()) {
      utilities.panic("Errors bootstrapping " + the_type.short_name());
    }
  }

  public void check(analyzable the_analyzable) {
    // TODO: do we need init_context() here?
    the_analyzable.analyze();
  }

  public boolean is_bootstrapped() {
    return language.library().is_bootstrapped();
  }

  private void process_bootstrap_ops(analysis_context context) {
    add_operator(new is_op(operator.IS_OPERATOR, false));
    add_operator(new is_op(operator.IS_NOT_OPERATOR, true));

    add_operator(new cast_op());
    add_operator(new assign_op());
    add_operator(new add_op());

    overloaded_procedure add_assign = new overloaded_procedure(
        new add_assign_op(library().immutable_nonnegative_type()));
    add_assign.add(new add_assign_op(library().immutable_integer_type()));
    add_operator(add_assign);

    add_operator(new multiply_op());
    add_operator(new multiply_assign_op());
    add_operator(new concatenate_op());
    add_operator(new concatenate_assign_op());
    add_operator(new less_op());

    // TODO: this is not used.
    //add_operator(new escape_fn(context));

    add_operator(operator.MODULO);
    add_operator(operator.SUBTRACT);
    add_operator(operator.NEGATE);

    add_operator(operator.EQUAL_TO);
    add_operator(operator.NOT_EQUAL_TO);

    add_operator(operator.GREATER);
    add_operator(operator.LESS_EQUAL);
    add_operator(operator.GREATER_EQUAL);

    add_operator(operator.LOGICAL_AND);
    add_operator(operator.LOGICAL_OR);
    add_operator(operator.LOGICAL_NOT);

    add_operator(operator.SUBTRACT_ASSIGN);
  }

  /** Introduce target declarations. */
  public void process_targets() {
    add_target(new analyze_target(simple_name.make(new base_string("analyze")), this));
    add_target(new java_generator_target(simple_name.make(new base_string("generate_java")), this));
    add_target(new printer_target(simple_name.make(new base_string("print_source")), this));
    add_target(new publish_target(simple_name.make(new base_string("print_documentation")), this));
  }

  private void add_target(target_value the_target) {
    bootstrap_context.add(root, the_target.name(), the_target.to_action(root_origin));
  }

  /*
  public void process_project(list<construct> constructs, analysis_context context) {
    declaration_list_analyzer body =
        new declaration_list_analyzer(constructs, root, context, root_origin);
    check(body);
    if (!has_errors()) {
      ensure_everything_is_analyzed(constructs, context);
    }
  }

  protected void ensure_everything_is_analyzed(list<construct> constructs,
      analysis_context context) {
    readonly_list<construct> flattened = base_construct.flatten(constructs);
    for (int i = 0; i < flattened.size(); ++i) {
      construct the_construct = flattened.get(i);
      @Nullable analyzable the_analyzable = context.get_analyzable(the_construct);
      if (the_analyzable == null) {
        new base_notification(
            new base_string("Not analyzed " + the_construct), the_construct).report();
      } else if (the_analyzable.deeper_origin() != the_construct) {
        // TODO: enforce 1:1 mapping...
      }
    }
  }
  */

  public @Nullable readonly_list<construct> load_type_body(
      type_announcement_construct the_declaration) {

    @Nullable source_content declaration_sourcee = position_util.get_source(the_declaration);
    assert declaration_sourcee != null;
    assert declaration_sourcee.name instanceof resource_identifier;

    resource_catalog declaration_catalog =
        ((resource_identifier) declaration_sourcee.name).parent().access_catalog();

    assert the_declaration.name instanceof simple_name;
    string name = to_resource_name(the_declaration.name);

    resource_identifier catalog_id = declaration_catalog.resolve(name);
    if (catalog_id.exists()) {
      declaration_catalog = catalog_id.access_catalog();
    }

    resource_identifier source_id = declaration_catalog.resolve(name, base_extension.IDEAL_SOURCE);
    if (!source_id.exists()) {
      new base_notification(
          new base_string("Can't locate resource for type declaration"), the_declaration).report();
      return null;
    }

    // TODO: signal non-fatal error
    source_content source = new source_content(source_id);
    create_util.progress_loading(source_id);

    list<construct> constructs = parse(source);
    // TODO: signal non-fatal error
    assert constructs.is_not_empty();
    return constructs;
  }

  private void add_operator(procedure_value pv) {
    operator the_operator = (operator) pv.name();
    add_operator(the_operator, pv);
  }

  private void add_operator(operator the_operator) {
    add_operator(the_operator, null);
  }

  private void add_operator(operator the_operator, @Nullable procedure_value operator_procedure) {
    principal_type parent = library().operators_package();
    simple_name symbol = the_operator.symbol();

    readonly_list<action> actions = bootstrap_context.lookup(parent, symbol);
    // TODO: signal error.
    if (actions.is_empty()) {
      utilities.panic("Operator not found: " + symbol);
    }

    if (actions.size() > 1) {
      utilities.panic("Duplicate operator " + symbol);
    }

    action the_action = actions.first();
    if (operator_procedure != null) {
      bootstrap_context.add(parent, the_operator, operator_procedure.to_action(the_action));
    } else {
      bootstrap_context.add(parent, the_operator, the_action);
    }
  }

  public static @Nullable type_declaration_analyzer get_declaration(list<construct> constructs,
      analysis_context context) {

    if (constructs == null) {
      return null;
    }

    if (constructs.size() != 1) {
      log.error("Exactly one declaration expected.");
      return null;
    }

    analyzable a = context.get_analyzable(constructs.first());
    if (! (a instanceof type_declaration_analyzer)) {
      log.error("Type declaration expected.");
      return null;
    }

    return (type_declaration_analyzer) a;
  }
}
