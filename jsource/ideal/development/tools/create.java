/*
 * Copyright 2014-2022 The Ideal Authors. All rights reserved.
 *
 * Use of this source code is governed by a BSD-style
 * license that can be found in the LICENSE file or at
 * https://theideal.org/license/
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
import ideal.machine.elements.runtime_util;
import ideal.machine.channels.standard_channels;
import ideal.machine.resources.*;

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
import ideal.development.origins.*;
import ideal.development.printers.*;
import ideal.development.flags.*;
import ideal.development.targets.*;
import ideal.development.tests.*;

class create {

  private static final simple_name CLEAN_SLATE = simple_name.make("clean_slate");

  public static void main(String[] args) {
    list<string> arguments = new base_list<string>();
    for (int i = 0; i < args.length; ++i) {
      arguments.append(new base_string(args[i]));
    }
    create_flags the_create_flags = new create_flags(arguments, panic_procedure);
    status the_status = new create().start(the_create_flags);
    System.exit(the_status.is_ok ? 0 : 1);
  }

  private static procedure1<Void, string> panic_procedure =
    new procedure1<Void, string>() {
      @Override public Void call(string message) {
        utilities.panic(message);
        return null;
      }
    };

  public status start(create_flags the_create_flags) {
    debug.initialize(the_create_flags);
    // debug.DO_REDUNDANT_CHECKS = !debug.CACHE_ACTIONS;
    if (the_create_flags.UNIT_TESTS) {
      if (false) {
        System.out.println(network.NETWORK_CATALOG.resolve(
            new base_string("https://dynin.com/")).access_string(null).content().get());
      }
      all_tests.run_all_tests();
      return status.ok;
    }

    if (the_create_flags.DEBUG_IMPORT) {
      import_util.start_import();
      return status.ok;
    }

    create_util.progress("INIT");

    assert the_create_flags.input != null;
    resource_identifier input_id = filesystem.CURRENT_CATALOG.resolve(the_create_flags.input);
    create_util.progress_loading(input_id);

    source_content input = new source_content(input_id);

    @Nullable string output_name = the_create_flags.output;

    resource_catalog top_catalog = filesystem.CURRENT_CATALOG;
    if (the_create_flags.top != null) {
      top_catalog = top_catalog.resolve(the_create_flags.top).access_catalog();
    }

    create_manager cm = new create_manager(top_catalog);

    if (the_create_flags.DEBUG_REFLECT) {
      reflect_util.start_reflect(cm, input);
      return status.ok;
    }

    create_util.progress("PARSE");

    list<construct> constructs = cm.parse(input);
    assert constructs != null;

    if (cm.has_errors()) {
      return status.error;
    }

    readonly_set<simple_name> extensions = find_use_constructs(constructs);
    boolean clean_slate = extensions.contains(CLEAN_SLATE);

    cm.process_targets();

    create_util.progress("BOOTSTRAP");
    cm.process_bootstrap(!clean_slate);

    if (output_name != null) {
      cm.set_output_catalog(
          filesystem.CURRENT_CATALOG.resolve(output_name).access_catalog());
    }

    if (the_create_flags.DEBUG_CONSTRUCTS) {
      create_util.progress("DISPLAY");
      output<text_fragment> out = new plain_formatter(standard_channels.stdout);
      out.write(runtime_util.display(constructs));
    }

    analysis_context the_context = cm.get_analysis_context();
    list_analyzer body = new list_analyzer(constructs, cm.root, the_context, input);

    immutable_list<analysis_pass> passes = analysis_pass.all();
    assert passes.first() == analysis_pass.BEFORE_EVALUATION;

    create_util.progress(analysis_pass.TARGET_DECL.toString());
    body.multi_pass_analysis(analysis_pass.TARGET_DECL);
    assert passes.get(1) == analysis_pass.TARGET_DECL;

    if (the_create_flags.target == null) {
      for (int i = 2; i < passes.size(); ++i) {
        analysis_pass pass = passes.get(i);
        create_util.progress(pass.toString());
        body.multi_pass_analysis(pass);
      }
    }

    if (!cm.has_errors()) {
      //cm.ensure_everything_is_analyzed(constructs, the_context);
    }

    if (the_create_flags.RUN && !cm.has_errors()) {
      create_util.progress("EXECUTE");
      body.analyze().to_action().execute(null_wrapper.instance, cm.new_execution_context());
    }

    if (the_create_flags.PRINT) {
      printer_target printer = new printer_target(simple_name.make("printer"), cm);
      printer.setup(the_context);
      readonly_list<simple_name> test_name =
          new base_list<simple_name>(simple_name.make("test"));
      printer.print_constructs(constructs, test_name);
    }

    if (the_create_flags.PRETTY_PRINT) {
      content_writer the_writer = new content_writer(cm.output_catalog(),
          printer_util.dash_renderer);
      publish_generator the_generator =
          new publish_generator(publish_mode.FILE_MODE, the_context, the_writer);
      xref_context the_xref_context = the_generator.the_xref_context;
      immutable_list<simple_name> test_name =
          new base_list<simple_name>(simple_name.make("test")).frozen_copy();
      the_xref_context.add_named_output(cm.root, test_name);
      the_xref_context.the_mapping_visitor.visit(body);
      declaration library_declaration =
          common_types.library_namespace().get_declaration();
      assert library_declaration != null;
      the_xref_context.the_mapping_visitor.visit(library_declaration);
      new populate_xref(the_xref_context, cm.root).process_construct_list(constructs);
      the_generator.generate_markup(constructs, the_xref_context.get_naming_strategy(cm.root));
    }

    if (cm.has_errors()) {
      return status.error;
    }

    if (the_create_flags.GENERATE) {
      content_writer the_writer = new content_writer(cm.output_catalog(),
          printer_util.dash_renderer);
      java_generator generator = new java_generator(java_library.get_instance(), the_writer);
      generator.generate_top_level(cm.root, body.declarations(), new empty<import_declaration>());
    }

    if (the_create_flags.target != null) {
      create_util.progress("TARGETS");
      readonly_list<target_declaration> targets = find_targets(body);

      for (int i = 0; i < targets.size(); ++i) {
        if (utilities.eq(targets.get(i).short_name().to_string(), the_create_flags.target)) {
          targets.get(i).process();
          // TODO: process more than one target.
          return cm.has_errors() ? status.error: status.ok;
        }
      }

      log.error("Target '" + the_create_flags.target + "' not found.");
    }

    return cm.has_errors() ? status.error: status.ok;
  }

  // TODO: use filter()
  private readonly_set<simple_name> find_use_constructs(readonly_list<construct> constructs) {
    set<simple_name> extensions = new hash_set<simple_name>();
    for (int i = 0; i < constructs.size(); ++i) {
      construct c = constructs.get(i);
      if (c instanceof use_construct) {
        use_construct uc = (use_construct) c;
        assert uc.name instanceof simple_name;
        extensions.add((simple_name) uc.name);
      }
    }
    return extensions;
  }

  private readonly_list<target_declaration> find_targets(list_analyzer body) {
    list<target_declaration> results = new base_list<target_declaration>();
    // TODO: replace with list.filter() and list.map();
    readonly_list<analyzable> elements = body.elements();
    for (int i = 0; i < elements.size(); ++i) {
      analyzable a = elements.get(i);
      if (a instanceof target_declaration) {
        results.append((target_declaration) a);
      }
    }
    return results;
  }
}
