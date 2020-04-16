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
import ideal.development.targets.*;
import ideal.development.extensions.*;
import ideal.development.targets.*;

class create {

  private static final simple_name CLEAN_SLATE = simple_name.make("clean_slate");

  public static void main(String[] args) {
    create_options options = new create_options();
    flag_util.parse_flags(args, options);
    new create().start(options);
  }

  public void start(create_options options) {
    create_util.DEBUG_PROGRESS = options.DEBUG_PROGRESS;

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

    create_util.progress("INIT");

    create_manager cm = new create_manager(filesystem.CURRENT_CATALOG);
    cm.process_targets();

    if (output_name != null) {
      cm.set_output_catalog(
          filesystem.CURRENT_CATALOG.resolve(output_name).access_catalog().content().get());
    }

    create_util.progress("PARSE");

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
      create_util.progress("BOOTSTRAP");
      cm.process_bootstrap();

      // TODO: make processing jinterop a feature that is activated with a 'use' construct.
      if (options.target != null) {
        create_util.progress("JINTEROP");
        cm.process_jinterop();
      }

      test_library.init(cm.bootstrap_context, cm.root);
    }

    if (options.DEBUG_CONSTRUCTS) {
      create_util.progress("DISPLAY");
      output<text_fragment> out = new plain_formatter(standard_channels.stdout);
      out.write(displayer.display(constructs));
    }

    analysis_context the_context = cm.new_analysis_context();
    multi_pass_analyzer body =
        new declaration_list_analyzer(constructs, cm.root, the_context, input);

    immutable_list<analysis_pass> passes = analysis_pass.all();
    assert passes.get(0) == analysis_pass.BEFORE_EVALUATION;

    create_util.progress(analysis_pass.TARGET_DECL.toString());
    body.multi_pass_analysis(analysis_pass.TARGET_DECL);
    assert passes.get(1) == analysis_pass.TARGET_DECL;

    if (options.target == null) {
      for (int i = 2; i < passes.size(); ++i) {
        analysis_pass pass = passes.get(i);
        create_util.progress(pass.toString());
        body.multi_pass_analysis(pass);
      }
    }

    if (!cm.has_errors()) {
      //cm.ensure_everything_is_analyzed(constructs, the_context);
    }

    if (options.RUN && !cm.has_errors()) {
      create_util.progress("EXECUTE");
      analyzer_utilities.to_action(body).execute(cm.new_execution_context());
    }

    if (options.PRINT) {
      printer_target printer = new printer_target(simple_name.make("printer"), cm);
      printer.setup(the_context);
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
      create_util.progress("TARGETS");
      readonly_list<target_declaration> targets = find_targets(constructs, the_context);

      for (int i = 0; i < targets.size(); ++i) {
        if (utilities.eq(targets.get(i).short_name().to_string(), options.target)) {
          targets.get(i).process();
          return;
        }
      }

      log.error("Target '" + options.target + "' not found.");
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

  private readonly_list<target_declaration> find_targets(readonly_list<construct> constructs,
      analysis_context context) {
    list<target_declaration> results = new base_list<target_declaration>();
    // TODO: replace with list.filter() and list.map();
    for (int i = 0; i < constructs.size(); ++i) {
      construct c = constructs.get(i);
      if (c instanceof target_construct) {
        target_construct tc = (target_construct) c;
        analyzable a = context.get_analyzable(tc);
        assert (a instanceof target_declaration);
        results.append((target_declaration) a);
      }
    }
    return results;
  }
}
