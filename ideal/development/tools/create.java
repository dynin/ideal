/*
 * Copyright 2014 The Ideal Authors. All rights reserved.
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
import ideal.runtime.resources.*;
import ideal.runtime.texts.*;
import ideal.runtime.logs.*;
import ideal.machine.channels.standard_channels;
import ideal.machine.resources.filesystem;

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
import ideal.development.declarations.*;
import ideal.development.functions.*;
import ideal.development.extensions.*;

class create {

  private static final simple_name CLEAN_SLATE = simple_name.make("clean_slate");

  private boolean DEBUG_PASSES;

  public static void main(String[] args) {
    create_options options = new create_options();
    flag_util.parse_flags(args, options);
    new create().start(options);
  }

  public void start(create_options options) {
    DEBUG_PASSES = options.DEBUG_PASSES;

    resolve_analyzer.CURE_UNDECLARED = options.CURE_UNDECLARED;
    resolve_analyzer.HIDE_DECLARATIONS = options.HIDE_DECLARATIONS;

    if (options.DEBUG_IMPORT) {
      import_util.start_import();
      return;
    }

    assert options.input != null;
    string input_name = options.input;

    @Nullable string output_name = options.output;

    if (options.DEBUG_REFLECT) {
      reflect_util.start_reflect(input_name);
      return;
    }

    progress("INIT");

    create_manager cm = new create_manager(filesystem.CURRENT_CATALOG);
    cm.process_targets();

    if (output_name != null) {
      cm.set_output_catalog(
          filesystem.CURRENT_CATALOG.resolve(output_name).access_catalog().content().get());
    }

    progress("PARSE");

    source_content input = new source_content(filesystem.CURRENT_CATALOG.resolve(input_name));
    list<construct> constructs = cm.parse(input);
    assert constructs != null;

    if (cm.has_errors()) {
      return;
    }

    readonly_set<simple_name> extensions = find_use_constructs(constructs);

    if (false && extensions.contains(CLEAN_SLATE)) {
      cm.process_type_operators();
    } else {
      progress("BOOTSTRAP");
      cm.process_bootstrap();

      // TODO: make processing jinterop a feature that is activated with a 'use' construct.
      if (options.target != null) {
        progress("JINTEROP");
        cm.process_jinterop();
      }

      test_library.init(cm.bootstrap_context, cm.root);
    }

    if (options.DEBUG_CONSTRUCTS) {
      progress("DISPLAY");
      output<text_fragment> out = new plain_formatter(standard_channels.stdout);
      out.write(displayer.display(constructs));
    }

    analysis_context the_context = cm.new_analysis_context();
    multi_pass_analyzer body =
        new declaration_list_analyzer(constructs, cm.root, the_context, input);

    for (analysis_pass pass : analysis_pass.values()) {
      progress(pass.toString());
      body.multi_pass_analysis(pass);
    }

    if (!cm.has_errors()) {
      //cm.ensure_everything_is_analyzed(constructs, the_context);
    }

    if (options.RUN && !cm.has_errors()) {
      progress("EXECUTE");
      analyzer_utilities.to_action(body).execute(cm.new_execution_context());
    }

    if (options.PRINT) {
      printer_target printer = new printer_target(simple_name.make("printer"));
      printer.setup(cm, the_context);
      readonly_list<simple_name> test_name =
          new base_list<simple_name>(simple_name.make("test"));
      printer.print_constructs(constructs, test_name);
    }

    if (options.PRETTY_PRINT) {
      content_writer the_writer = new content_writer(cm.output_catalog(),
          naming_strategy.dash_renderer);
      publish_generator the_generator = new publish_generator(the_context, the_writer);
      immutable_list<simple_name> test_name =
          new base_list<simple_name>(simple_name.make("test")).frozen_copy();
      naming_strategy the_naming_strategy = new naming_strategy(test_name, cm.root, the_context);
      the_generator.generate_markup(constructs, the_naming_strategy);
    }

    if (cm.has_errors()) {
      return;
    }

    if (options.target != null) {
      readonly_list<variable_declaration> targets = find_targets(constructs, the_context);

      for (int i = 0; i < targets.size(); ++i) {
        if (utilities.eq(targets.get(i).short_name().to_string(), options.target)) {
          build_target(targets.get(i), cm, the_context);
          return;
        }
      }

      log.error("Target '" + options.target + "' not found.");
    }
  }

  private void build_target(variable_declaration target_declaration, create_manager cm,
      analysis_context the_context) {
    assert target_declaration.get_init() != null;
    assert target_declaration.get_init() instanceof bound_procedure;

    bound_procedure bound_target = (bound_procedure) target_declaration.get_init();

    abstract_value the_target_value = bound_target.the_procedure_action.result();
    assert the_target_value instanceof target_value;
    target_value the_target = (target_value) the_target_value;

    the_target.process(bound_target.parameters, cm, the_context);
  }

  private void progress(String name) {
    if (DEBUG_PASSES) {
      // TODO: add timing info
      log.info(new base_string("============ ", name));
    }
  }

  // TODO: use filter()
  private readonly_set<simple_name> find_use_constructs(readonly_list<construct> constructs) {
    set<simple_name> extensions = new hash_set<simple_name>();
    readonly_list<construct> flattened = base_construct.flatten(constructs);
    for (int i = 0; i < flattened.size(); ++i) {
      construct c = flattened.get(i);
      if (c instanceof use_construct) {
        use_construct uc = (use_construct) c;
        assert uc.name instanceof simple_name;
        extensions.add((simple_name) uc.name);
      }
    }
    return extensions;
  }

  private readonly_list<variable_declaration> find_targets(readonly_list<construct> constructs,
      analysis_context context) {
    list<variable_declaration> results = new base_list<variable_declaration>();
    readonly_list<construct> flattened = base_construct.flatten(constructs);
    for (int i = 0; i < flattened.size(); ++i) {
      construct c = flattened.get(i);
      if (c instanceof variable_construct) {
        variable_construct vc = (variable_construct) c;
        analyzable a = context.get_analyzable(vc);
        if (a instanceof variable_declaration) {
          variable_declaration the_declaration = (variable_declaration) a;
          if (the_declaration.value_type() == core_types.target_type()) {
            results.append(the_declaration);
          }
        }
      }
    }
    return results;
  }
}
