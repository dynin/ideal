/*
 * Copyright 2014-2020 The Ideal Authors. All rights reserved.
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

public class naming_strategy extends debuggable implements printer_assistant, immutable_data {

  public static final string INDEX = new base_string("index");
  public static final simple_name XREF_NAME = simple_name.make("xref");

  private final immutable_list<simple_name> full_names;
  private final principal_type current_type;
  private final xref_context the_xref_context;

  private final immutable_list<simple_name> current_catalog;
  private final base_printer the_printer;
  private final dictionary<construct, string> fragments;
  private final set<string> fragment_ids;

  public static function1<string, simple_name> dash_renderer =
      new function1<string, simple_name>() {
        @Override
        public string call(simple_name name) {
          readonly_list<string> segments = name.segments;
          StringBuilder s = new StringBuilder();

          for (int i = 0; i < segments.size(); ++i) {
            s.append(utilities.s(segments.get(i)));
            if (i < segments.size() - 1) {
              s.append('-');
            }
          }

          return new base_string(s.toString());
        }
      };

  public naming_strategy(principal_type current_type, xref_context the_xref_context) {
    this(type_utilities.get_full_names(current_type), current_type, the_xref_context);
  }

  // TODO: this is only exposed so it can be used by create -pretty-print
  public naming_strategy(immutable_list<simple_name> full_names, principal_type current_type,
      xref_context the_xref_context) {
    assert full_names.is_not_empty();

    this.full_names = full_names;
    this.current_type = current_type;
    this.the_xref_context = the_xref_context;

    assert full_names.is_not_empty();
    this.current_catalog = full_names.slice(0, full_names.size() - 1);

    this.the_printer = new base_printer(printer_mode.STYLISH, this);

    fragments = new hash_dictionary<construct, string>();
    fragment_ids = new hash_set<string>();
  }

  public principal_type get_current_type() {
    return current_type;
  }

  public base_printer get_printer() {
    return the_printer;
  }

  public immutable_list<simple_name> get_full_names() {
    return full_names;
  }

  public immutable_list<simple_name> get_xref_names() {
    return make_xref_target(full_names).frozen_copy();
  }

  private analysis_context the_analysis_context() {
    return the_xref_context.the_analysis_context;
  }

  private readonly_list<simple_name> make_xref_target(readonly_list<simple_name> target) {
    assert target.is_not_empty();
    list<simple_name> xref_names = new base_list<simple_name>();
    xref_names.append_all(target.slice(0, target.size() - 1));

    simple_name last_name = full_names.last();
    xref_names.append(name_utilities.join(last_name, XREF_NAME));

    return xref_names;
  }

  // TODO: test.
  public base_string link_to_resource(readonly_list<simple_name> target_name,
      extension target_extension) {
    int shared_prefix = 0;

    while (shared_prefix < (current_catalog.size() - 1) &&
           shared_prefix < (target_name.size() - 2) &&
           current_catalog.get(shared_prefix + 1) == target_name.get(shared_prefix + 1)) {
      ++shared_prefix;
    }

    StringBuilder result = new StringBuilder();
    int parent_count = current_catalog.size() - shared_prefix;

    for (int i = 0; i < parent_count; ++i) {
      result.append(utilities.s(resource_util.PARENT_CATALOG));
      result.append(utilities.s(resource_util.PATH_SEPARATOR));
    }

    for (int i = shared_prefix; i < target_name.size(); ++i) {
      result.append(utilities.s(dash_renderer.call(target_name.get(i))));
      if (i == target_name.size() - 1) {
        result.append(utilities.s(target_extension.dot_name()));
      } else {
        result.append(utilities.s(resource_util.PATH_SEPARATOR));
      }
    }

    return new base_string(result.toString());
  }

  public text_fragment print_simple_name(simple_name name) {
    return the_printer.print_simple_name(name);
  }

  private @Nullable declaration get_declaration(construct the_construct) {
    @Nullable analyzable the_analyzable = the_analysis_context().get_analyzable(the_construct);
    if (the_analyzable != null) {
      @Nullable declaration the_declaration;
      if (the_analyzable instanceof declaration) {
        return (declaration) the_analyzable;
      } else {
        return declaration_util.get_declaration(the_analyzable.analyze());
      }
    }
    return null;
  }

  public @Nullable string link_to_type(@Nullable principal_type the_type, link_mode mode) {
    if (!publish_generator.GENERATE_XREF && mode == link_mode.XREF) {
      return null;
    }

    while (the_type != null && !the_xref_context.has_output_type(the_type)) {
      the_type = the_type.get_parent();
    }

    if (the_type == null) {
      return null;
    }

    readonly_list<simple_name> target_name = type_utilities.get_full_names(the_type);
    if (target_name.is_not_empty()) {
      if (mode == link_mode.XREF) {
        target_name = make_xref_target(target_name);
      }
      return link_to_resource(target_name, base_extension.HTML);
    } else {
      return null;
    }
  }

  @Override
  public @Nullable string link_to_construct(construct the_construct, link_mode mode) {
    return link_to_declaration(get_declaration(the_construct), mode);
  }

  public @Nullable string link_to_declaration(@Nullable declaration the_declaration,
      link_mode mode) {

    if (the_declaration == null || the_declaration.has_errors()) {
      return null;
    }

    if (the_declaration instanceof type_announcement) {
      type_announcement the_type_announcement = (type_announcement) the_declaration;
      return link_to_type(the_type_announcement.get_declared_type(), mode);
    }

    if (the_declaration instanceof type_declaration &&
        !(the_declaration instanceof type_parameter_declaration)) {
      type_declaration the_type_declaration = (type_declaration) the_declaration;
      return link_to_type(the_type_declaration.get_declared_type(), mode);
    }

    return null;
  }

  @Override
  public @Nullable string fragment_of_construct(construct the_construct, link_mode mode) {
    if (!publish_generator.GENERATE_XREF) {
      return null;
    }

    @Nullable string fragment = fragments.get(the_construct);
    if (fragment == null) {
      @Nullable analyzable the_analyzable = the_analysis_context().get_analyzable(the_construct);
      if (the_analyzable == null) {
        // Most likely, this is not_yet_implemented
      }
      if (false) {
        System.out.println("NOFRAG " + current_type + " C " + the_construct +
            " A " + the_analyzable);
      }
      return null;
    }

    return fragment;
  }

  public string add_fragment(construct the_construct) {
    @Nullable string fragment = fragments.get(the_construct);
    if (fragment != null) {
      return fragment;
    }

    if (the_construct instanceof name_construct) {
      fragment = name_to_id(((name_construct) the_construct).the_name);
    } else if (the_construct instanceof type_declaration_construct) {
      fragment = name_to_id(((type_declaration_construct) the_construct).name);
    } else {
      utilities.panic("Unknown construct " + the_construct);
    }

    string result = fragment;
    int index = 1;

    while (fragment_ids.contains(result)) {
      index += 1;
      result = new base_string(fragment, String.valueOf(index));
    }

    fragments.put(the_construct, result);
    fragment_ids.add(result);

    return result;

    // return fragment_of_declaration(get_declaration(the_construct));
  }

  private string name_to_id(action_name the_action_name) {
    return dash_renderer.call((simple_name) the_action_name);
  }

  /*
  private @Nullable string fragment_of_declaration(@Nullable declaration the_declaration) {
    if (the_declaration == null || the_declaration.has_errors()) {
      return null;
    }

    if (the_declaration instanceof type_announcement) {
      type_announcement the_type_announcement = (type_announcement) the_declaration;
      return name_to_id(the_type_announcement.short_name());
    }

    if (the_declaration instanceof type_declaration &&
        !(the_declaration instanceof type_parameter_declaration)) {
      type_declaration the_type_declaration = (type_declaration) the_declaration;
      return name_to_id(the_type_declaration.short_name());
    }

    return null;
  }
  */

  @Override
  public @Nullable documentation get_documentation(construct the_construct) {
    @Nullable declaration the_declaration = get_declaration(the_construct);
    if (the_declaration instanceof procedure_declaration) {
      return ((procedure_declaration) the_declaration).annotations().the_documentation();
    } else {
      return null;
    }
  }
}
