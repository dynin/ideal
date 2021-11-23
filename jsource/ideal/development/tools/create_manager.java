/*
 * Copyright 2014-2021 The Ideal Authors. All rights reserved.
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
import ideal.development.origins.*;
import ideal.development.policies.*;
import ideal.development.comments.*;
import ideal.development.targets.*;

public class create_manager implements target_manager, type_bootstrapper {

  public final base_semantics language;
  public final principal_type root;
  private final resource_catalog top_catalog;
  public final analysis_context bootstrap_context;
  public final origin root_origin;
  private final common_scanner scanner;
  private output_counter<notification> notifications;
  private @Nullable resource_catalog output_catalog;

  public create_manager(resource_catalog top_catalog) {
    language = new base_semantics();
    process_kinds(language);
    bootstrap_context = new base_analysis_context(language);
    root = common_types.root_type();
    this.top_catalog = top_catalog;
    analyzer_utilities.set_loader(loader_procedure());
    root_origin = origin_utilities.builtin_origin; // TODO: use resource id as origin
    scanner = new common_scanner();
    scanner.add_kinds(language.all_kinds());
    set_notification_handler((output<notification>) (output) log.log_output);
  }

  private void process_kinds(base_semantics language) {
    language.add_kind(type_kinds.union_kind, base_policy.instance);
    language.add_kind(type_kinds.type_alias_kind, base_policy.instance);
    language.add_kind(type_kinds.block_kind, namespace_policy.instance);

    language.add_kind(type_kinds.class_kind, general_policy.instance);
    language.add_kind(type_kinds.datatype_kind, general_policy.instance);
    language.add_kind(type_kinds.interface_kind, general_policy.instance);
    language.add_kind(type_kinds.singleton_kind, general_policy.instance);
    language.add_kind(type_kinds.package_kind, namespace_policy.instance);
    language.add_kind(type_kinds.program_kind, general_policy.instance);
    language.add_kind(type_kinds.module_kind, namespace_policy.instance);
    language.add_kind(type_kinds.concept_kind, general_policy.instance);
    language.add_kind(type_kinds.enum_kind, general_policy.instance);
    language.add_kind(type_kinds.project_kind, namespace_policy.instance);
    language.add_kind(type_kinds.service_kind, general_policy.instance);
    language.add_kind(type_kinds.world_kind, general_policy.instance);
    language.add_kind(type_kinds.namespace_kind, namespace_policy.instance);
    language.add_kind(type_kinds.test_suite_kind, general_policy.instance);
    // TODO: this should be a special keyword
    language.add_kind(type_kinds.reference_kind, general_policy.instance);
    // TODO: this should be a special keyword
    language.add_kind(type_kinds.procedure_kind, general_policy.instance);
    language.add_kind(type_kinds.html_content_kind, namespace_policy.instance);
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
    readonly_list<token<Object>> tokens = scanner.scan(source);

    tokens = documenter_filter.transform(tokens);

    return loader.parse((readonly_list<token>) (readonly_list) tokens);
  }

  public void process_bootstrap(boolean load_library) {
    // TODO: resolve interdependency.
    process_type_operators();

    principal_type elements = common_types.elements_package();
    bootstrap_type(elements, analysis_pass.IMPORT_AND_TYPE_VAR_DECL);
    bootstrap_type(common_types.operators_package(), analysis_pass.METHOD_AND_VARIABLE_DECL);

    process_bootstrap_ops(bootstrap_context);

    java_library.bootstrap_on_demand(this, bootstrap_context);

    if (load_library) {
      bootstrap_ideal_namespace();
      action_utilities.add_promotion(bootstrap_context, root, elements, root_origin);
      test_library.init(bootstrap_context, root);
    }
  }

  public void process_type_operators() {
    principal_type operators = common_types.operators_package();
    type_union_op the_op = new type_union_op();
    bootstrap_context.add(operators, the_op.name(), the_op.to_action(root_origin));
  }

  private string to_resource_name(action_name name) {
    assert name instanceof simple_name;
    return name.to_string();
  }

  @Override
  public void bootstrap_type(principal_type the_type, analysis_pass pass) {
    assert the_type.get_declaration() == null;

    resource_catalog source_catalog;
    if (the_type.get_parent() == common_types.ideal_namespace()) {
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
    type_declaration_analyzer the_analyzer =
        new type_declaration_analyzer(the_declaration, the_type.get_parent(), bootstrap_context);
    the_analyzer.multi_pass_analysis(pass);

    assert the_type.get_declaration() != null;

    if (has_errors()) {
      utilities.panic("Errors bootstrapping " + the_type.short_name());
    }
  }

  public void bootstrap_ideal_namespace() {
    principal_type the_type = common_types.ideal_namespace();
    assert the_type.get_declaration() == null;

    source_content type_source = load_source(top_catalog, to_resource_name(the_type.short_name()));
    list<construct> constructs = parse(type_source);
    assert constructs != null;

    list_analyzer body = new list_analyzer(constructs, root, bootstrap_context, root_origin);
    body.multi_pass_analysis(analysis_pass.TARGET_DECL);

    if (has_errors()) {
      utilities.panic("Errors bootstrapping " + the_type.short_name());
    }
  }

  public void check(analyzable the_analyzable) {
    // TODO: do we need init_context() here?
    the_analyzable.analyze();
  }

  public boolean is_bootstrapped() {
    return common_types.is_bootstrapped();
  }

  private void process_bootstrap_ops(action_context context) {
    add_operator(new is_op(operator.IS_OPERATOR, false));
    add_operator(new is_op(operator.IS_NOT_OPERATOR, true));

    add_operator(new cast_op(operator.SOFT_CAST));
    add_operator(new cast_op(operator.HARD_CAST));

    add_operator(new assign_op());
    add_operator(new add_op());

    overloaded_procedure add_assign = new overloaded_procedure(
        new add_assign_op(common_types.immutable_nonnegative_type()));
    add_assign.add(new add_assign_op(common_types.immutable_integer_type()));
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
    add_operator(operator.COMPARE);

    add_operator(operator.LOGICAL_AND);
    add_operator(operator.LOGICAL_OR);
    add_operator(operator.LOGICAL_NOT);

    add_operator(operator.SUBTRACT_ASSIGN);
  }

  /** Introduce target declarations. */
  public void process_targets() {
    add_target(new analyze_target(simple_name.make(new base_string("analyze")), this));
    add_target(new java_generator_target(
        simple_name.make(new base_string("generate_java")), this));
    add_target(new printer_target(simple_name.make(new base_string("print_source")), this));
    add_target(new publish_target(simple_name.make(new base_string("print_documentation")),
        this, publish_mode.FILE_MODE));
    add_target(new publish_target(simple_name.make(new base_string("print_site")),
        this, publish_mode.WEBSITE_MODE));
  }

  private void add_target(target_value the_target) {
    bootstrap_context.add(root, the_target.name(), the_target.to_action(root_origin));
  }

  public @Nullable readonly_list<construct> load_resource(
      type_announcement_construct the_declaration) {

    @Nullable source_content declaration_sourcee = origin_utilities.get_source(the_declaration);
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

    boolean is_html = the_declaration.kind == type_kinds.html_content_kind;
    extension the_extension = is_html ? base_extension.HTML : base_extension.IDEAL_SOURCE;

    resource_identifier source_id = declaration_catalog.resolve(name, the_extension);
    if (!source_id.exists()) {
      new base_notification(
          new base_string("Can't locate resource for type declaration"), the_declaration).report();
      return null;
    }

    // TODO: signal non-fatal error
    source_content source = new source_content(source_id);
    create_util.progress_loading(source_id);

    if (is_html) {
      comment_construct the_comment_construct = parse_html_content(source);
      return new base_list<construct>(the_comment_construct);
    } else {
      list<construct> constructs = parse(source);
      // TODO: signal non-fatal error
      assert constructs.is_not_empty();
      return constructs;
    }
  }

  procedure1<readonly_list<construct>, type_announcement_construct> loader_procedure() {
    return new procedure1<readonly_list<construct>, type_announcement_construct>() {
      @Override
      public readonly_list<construct> call(type_announcement_construct announcement) {
        return load_resource(announcement);
      }
    };
  }

  private void add_operator(procedure_value pv) {
    operator the_operator = (operator) pv.name();
    add_operator(the_operator, pv);
  }

  private void add_operator(operator the_operator) {
    add_operator(the_operator, null);
  }

  private void add_operator(operator the_operator, @Nullable procedure_value operator_procedure) {
    principal_type parent = common_types.operators_package();
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
    if (operator_procedure == null) {
      procedure_executor the_procedure_executor =
          (procedure_executor) ((data_value_action) the_action).the_value;
      operator_procedure = new procedure_executor(the_procedure_executor.get_declaration(),
          the_operator);
    } else {
      ((base_procedure) operator_procedure).set_declaration(
          (procedure_declaration) the_action.get_declaration());
    }

    bootstrap_context.add(parent, the_operator, operator_procedure.to_action(the_action));
  }

  public static comment_construct parse_html_content(source_content source) {
    final doc_parser parser = new doc_parser(doc_comment_processor.get_grammar(),
        new procedure1<Void, string>() {
          @Override public Void call(string message) {
            new base_notification(message, source).report();
            return null;
          }
        });
    string content = source.content;
    text_fragment the_text = parser.parse_content(content);
    comment the_comment = new comment(comment_type.BLOCK_DOC_COMMENT, content, content);
    return new comment_construct(the_comment, the_text, source);
  }
}
